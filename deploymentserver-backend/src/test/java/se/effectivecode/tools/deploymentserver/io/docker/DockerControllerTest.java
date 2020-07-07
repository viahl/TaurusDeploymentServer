package se.effectivecode.tools.deploymentserver.io.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.Test;
import se.effectivecode.tools.deploymentserver.io.logger.ConsoleResult;
import se.effectivecode.tools.deploymentserver.model.DockerConfig;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DockerControllerTest {
    DockerController classToTest = new DockerController();

    @Test
    void startDockerServiceEarkivShouldStartTheServiceInANewNetwork() throws MalformedURLException {
        String filePath = "C:\\dev\\code\\deploymentServer\\deploymentserver-backend\\repo\\service-earkiv";
        Project project = Project.builder()
                .gitURL(new URL("https://git.dom.se/scm/mal/service-earkiv.git"))
                .version("1.0-SNAPSHOT")
                .name("service-earkiv")
                .gitAbsolutePath(filePath)
                .build();
        Boolean started = classToTest.startDocker(project);
        assertTrue(started);
    }

    @Test
    void removeImageShouldRemoveAnImageIfItExist() throws DockerException, InterruptedException, IOException {
        DefaultDockerClient client = classToTest.getDockerClient();
        DockerConfig config = DockerConfig.builder().containerName("service-earkiv").build();
        ConsoleResult consoleResult = ConsoleResult.createConsoleResult("service-earkiv", "2.0");
        classToTest.removeImage(client, config, consoleResult);
    }

    @Test
    void buildImageShouldTakeSomeParametersAndThenBuildTheImage() throws IOException {
        ConsoleResult consoleResult = ConsoleResult.createConsoleResult("service-earkiv", "2.0");

        DockerConfig config = DockerConfig.builder()
                .containerName("service-earkiv")
                .dockerfileLocation("C:\\dev\\vera\\malhantering\\service-earkiv")
                .build();

        Project project = Project.builder()
                .name("service-earkiv")
                .version("1.0.0-SNAPSHOT")
                .build();

        assertTrue(classToTest.buildImage(config, project, consoleResult));
    }

    @Test
    void crappySystemProperties() {
        Properties properties = System.getProperties();
        for (String name : properties.stringPropertyNames()) {
            System.out.println("name [" + name + "] value [" + properties.getProperty(name) + "]");
        }
    }
}