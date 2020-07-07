package se.effectivecode.tools.deploymentserver.io.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.Network;
import com.spotify.docker.client.messages.NetworkConfig;
import com.spotify.docker.client.messages.NetworkCreation;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.RemovedImage;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.effectivecode.tools.deploymentserver.io.logger.ConsoleResult;
import se.effectivecode.tools.deploymentserver.io.maven.MavenHandler;
import se.effectivecode.tools.deploymentserver.io.util.FileUtil;
import se.effectivecode.tools.deploymentserver.model.DockerConfig;
import se.effectivecode.tools.deploymentserver.model.Project;
import se.effectivecode.tools.deploymentserver.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DockerController {
    private final static String propertiesFileName = "docker.properties";
    private final static Logger LOGGER = LoggerFactory.getLogger(DockerController.class);
    private final static String NETWORK = "DEPLOYMENT-NETWORK";

    public static final String UP = "Up";
    public static final String NEXT_CMD = " && ";
    private MavenHandler mavenHandler = new MavenHandler();

    void setupNetwork() {
        DockerClient client = null;
        try {
            client = DefaultDockerClient.fromEnv().build();
            List<Network> networks = client.listNetworks(DockerClient.ListNetworksParam.builtInNetworks(), DockerClient.ListNetworksParam.customNetworks());
            boolean networkExist = false;
            for (Network network : networks) {
                if (network.name().equalsIgnoreCase(NETWORK)) {
                    networkExist = true;
                    break;
                }
            }
            if (!networkExist) {
                NetworkConfig networkConfig = NetworkConfig.builder()
                        .checkDuplicate(true)
                        .attachable(true)
                        .name(NETWORK)
                        .build();
                NetworkCreation network = client.createNetwork(networkConfig);
                LOGGER.debug("Network warning [" + network.warnings() + "]");
            }
        } catch (DockerException | InterruptedException | DockerCertificateException e) {
            LOGGER.error("Some kinds of error have occured ", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public Boolean startDocker(Project project) {
        setupNetwork();
        boolean status = true;
        DockerClient client = getDockerClient();
        try {
            File gitLocalPath = project.getGitDirectory();
            DockerConfig dockerConfig = readInLocalPropertiesFile(gitLocalPath);
            String containerName = project.getName();
            ConsoleResult consoleResult = ConsoleResult.createConsoleResult(project.getName(), project.getVersion());
            mavenHandler.buildMaven(project, consoleResult);
            Container container = getContainerWithName(containerName, client);
            LOGGER.debug("Check if container exist");
            inactivateContainer(client, container);
            clearDockerFromContainer(client, container, dockerConfig);
            if (setupImage(client, dockerConfig, project, consoleResult)) {
                ContainerConfig config = createContainerConfig(dockerConfig);
                ContainerCreation newContainer = client.createContainer(config, dockerConfig.getContainerName());
                client.startContainer(newContainer.id());
            } else {
                status = false;
            }
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Some kinds of error have occured ", e);
        } catch (IOException ioe) {
            LOGGER.error("IOE problem", ioe);
        } finally {
            FileUtil.closeStreams(client);
        }
        return status;
    }

    boolean setupImage(DockerClient client, DockerConfig config, Project project, ConsoleResult consoleResult) throws DockerException, InterruptedException {
        removeImage(client, config, consoleResult);
        return buildImage(config, project, consoleResult);
    }

    boolean buildImage(DockerConfig config, Project project, ConsoleResult consoleResult) {
        boolean buildStatus = false;
        ShellCommand shellCommand = ShellCommand.getShellCommand();
        LOGGER.debug("Got command shell for system [" + shellCommand.name() + "]");
        String command = produceDockerBuildCommand(config);
        try {
            consoleResult = ConsoleResult.createConsoleResult(project.getName(), project.getVersion());
            consoleResult.addLineToFile("Start building image");
            shellCommand.executeCommand(command, consoleResult);
            consoleResult.addLineToFile("Stop building image");
            shellCommand.shutDownShellCommand();
            buildStatus = true;
        } catch (IOException ioe) {
            LOGGER.debug("Something strange happend here", ioe);
        } finally {
            consoleResult.closeFileOutputStream();
        }
        return buildStatus;
    }

    private String produceDockerBuildCommand(DockerConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("cd ").append(config.getDockerfileLocation());
        sb.append(NEXT_CMD);
        sb.append("docker build --file " + "Dockerfile" + " --tag=" + config.getContainerName() + " .");
        return sb.toString();
    }

    void removeImage(DockerClient client, DockerConfig config, ConsoleResult consoleResult) throws DockerException, InterruptedException {
        List<Image> images = client.listImages(DockerClient.ListImagesParam.allImages());
        boolean imageRemoved = false;
        for (Image image : images) {
            for (String tag : image.repoTags()) {
                if (tag.startsWith(config.getContainerName())) {
                    List<RemovedImage> removedImages = client.removeImage(tag);
                    imageRemoved = removedImages.size() > 0;
                    consoleResult.addLineToFile("Remove image [" + tag + "]");
                }
            }
        }
        if (!imageRemoved) {
            LOGGER.debug("Could not find any image to remove with the name [" + config.getContainerName() + "]");
        }
    }

    private void inactivateContainer(DockerClient client, Container container) throws DockerException, InterruptedException {
        if (isContainerActive(container)) {
            LOGGER.debug("Container exist and should be killed and removed");
            client.killContainer(container.id());
        }
    }

    private void clearDockerFromContainer(DockerClient client, Container container, DockerConfig dockerConfig) throws DockerException, InterruptedException {
        if (existContainer(container, dockerConfig)) {
            client.removeContainer(container.id());
            LOGGER.debug("Does the container [" + container.id() + "] still exist?  [" + isContainerActive(container) + "]");
        }
    }

    boolean existContainer(Container container, DockerConfig config) {
        boolean status = false;
        if (container != null) {
            status = container.image().equalsIgnoreCase(config.getContainerName());
        }
        return status;
    }

    ContainerConfig createContainerConfig(DockerConfig dockerConfig) {
        ContainerConfig.Builder builder = ContainerConfig.builder();
        builder.hostConfig(getHostConfig(dockerConfig));
        builder.image(dockerConfig.getContainerName());
        builder.exposedPorts(dockerConfig.getExposedPorts());
        builder.hostname(dockerConfig.getHostname());
        builder.domainname(dockerConfig.getHostname());
        if (StringUtil.isNotEmpty(dockerConfig.volume())) {
            builder.addVolume(dockerConfig.volume());
        }
        String containerB = ToStringBuilder.reflectionToString(builder);
        LOGGER.error("\n\n ContainerBuilder [" + containerB + "]\n\n");
        return builder.build();
    }

    HostConfig getHostConfig(DockerConfig dockerConfig) {
        HostConfig.Builder hostBuilder = HostConfig.builder();
        Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String portCombo : dockerConfig.getSplitPorts()) {
            LOGGER.error("portCombo [" + portCombo + "]");
            int splitIndex = portCombo.indexOf(DockerConfig.COLON);
            String containerPort = portCombo.substring(splitIndex + 1) + "/tcp";
            String hostPort = portCombo.substring(0, splitIndex);
            List<PortBinding> portList = new ArrayList<>();
            portList.add(PortBinding.of("0.0.0.0", hostPort));
            portBindings.put(containerPort, portList);
            LOGGER.error("Containerport [" + containerPort + "], hostport [" + hostPort + "]");
        }
        hostBuilder.portBindings(portBindings);
        hostBuilder.networkMode(NETWORK);
        return hostBuilder.build();
    }

    DockerConfig readInLocalPropertiesFile(File gitLocalPath) {
        DockerConfig config = null;
        InputStream inputStream = null;
        try {
            File propertiesFile = new File(gitLocalPath.getAbsolutePath()
                    + File.separator + propertiesFileName);
            Properties properties = new Properties();
            inputStream = new FileInputStream(propertiesFile);
            properties.load(inputStream);
            config = loadDockerConfig(properties, gitLocalPath);
        } catch (IOException ioe) {
            LOGGER.debug("IOException ", ioe);
        } finally {
            FileUtil.closeStreams(inputStream);
        }
        return config;
    }

    private DockerConfig loadDockerConfig(Properties properties, File gitLocalPath) {
        String container_name = properties.getProperty("container_name");
        String port = properties.getProperty("port");
        String volumeWindows = properties.getProperty("volume_windows");
        String volumeLinux = properties.getProperty("volume_linux");
        boolean restart = properties.getProperty("restart") != null;
        String endline = properties.getProperty("endline");
        String hostname = properties.getProperty("hostname");
        String dockerfile = getDockerFile(properties.getProperty("dockerfile.name"), gitLocalPath);
        return DockerConfig.builder()
                .containerName(container_name)
                .portString(port)
                .volumeWindows(volumeWindows)
                .volumeLinux(volumeLinux)
                .restart(restart)
                .endline(endline)
                .network(NETWORK)
                .hostname(hostname)
                .dockerfileLocation(dockerfile)
                .build();
    }

    private String getDockerFile(String property, File gitLocalPath) {
        String dockerfilePath = gitLocalPath.getAbsolutePath();
        if (StringUtil.isEmpty(property)) {
            dockerfilePath = property;
        }
        return dockerfilePath;
    }

    boolean isContainerActive(Container container) {
        boolean status = false;
        LOGGER.debug("Container [" + ToStringBuilder.reflectionToString(container) + "]");
        if (container != null) {
            status = container.status().contains(UP);
            LOGGER.debug("Container status [" + container.status() + "] return value [" + status + "]");
        }
        return status;
    }

    Container getContainerWithName(String containerName, DockerClient client) {
        Container containerOut = null;
        try {
            DockerClient.ListContainersParam listContainersParam = DockerClient.ListContainersParam.allContainers();
            List<Container> containers = client.listContainers(listContainersParam);
            for (Container container : containers) {
                LOGGER.debug("Container name is [" + container.names() + "] image [" + container.image() + "] imageId [" + container.id() + "]");
                if (StringUtil.isNotEmpty(container.image()) && container.image().equalsIgnoreCase(containerName)) {
                    containerOut = container;
                }
                LOGGER.debug("status [" + container.status() + "]");
            }
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug("Something went wrong when find the right container.", e);
        }
        LOGGER.debug(ToStringBuilder.reflectionToString(containerOut));
        return containerOut;
    }

    public boolean removeDocker(Project project) {
        DefaultDockerClient client = getDockerClient();
        Container container = killContainerFromRunning(project, client);
        removeContainer(client, container);
        boolean removeStatus = getContainerWithName(project.getName(), client) == null;

        if (client != null) {
            client.close();
        }

        return removeStatus;
    }

    private Container killContainerFromRunning(Project project, DefaultDockerClient client) {
        Container container = null;
        try {
            container = getContainerWithName(project.getName(), client);
            if (isContainerActive(container)) {
                client.killContainer(container.id());
            }
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug("Something gone wrong when trying to handle the docker container");
        }
        return container;
    }

    DefaultDockerClient getDockerClient() {
        DefaultDockerClient client = null;
        try {
            client = DefaultDockerClient.fromEnv().build();
        } catch (DockerCertificateException e) {
            LOGGER.debug("Something gone wrong when trying to handle the docker container");
        }
        return client;
    }

    private void removeContainer(DefaultDockerClient client, Container container) {
        try {
            if (container != null) {
                client.removeContainer(container.id());
            }
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug("Something gone wrong when trying to handle the docker container");
        }
    }
}