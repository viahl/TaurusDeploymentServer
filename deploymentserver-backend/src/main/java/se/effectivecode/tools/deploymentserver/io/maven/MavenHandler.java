package se.effectivecode.tools.deploymentserver.io.maven;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.effectivecode.tools.deploymentserver.io.docker.ShellCommand;
import se.effectivecode.tools.deploymentserver.io.logger.ConsoleResult;
import se.effectivecode.tools.deploymentserver.io.util.FileUtil;
import se.effectivecode.tools.deploymentserver.util.StringUtil;
import se.effectivecode.tools.deploymentserver.configuration.ProjectHolder;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class MavenHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(MavenHandler.class);
    private StringUtil util = new StringUtil();

    public void updateProjectWithMavenInformation(Project project) {
        File gitPath = project.getGitDirectory();
        File pomXMLPath = new File(gitPath.getAbsolutePath() + File.separator + "pom.xml");
        if (pomXMLPath.exists()) {
            FileReader fileReader = null;
            try {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                fileReader = new FileReader(pomXMLPath);
                Model model = reader.read(fileReader);
                project.setName(model.getName());
                project.setVersion(model.getVersion());
                Properties properties = model.getProperties();
                String deploymentPing = properties.getProperty("deployment.ping");
                String deploymentDbPing = properties.getProperty("deployment.db.ping");
                String deploymentHost = properties.getProperty("deployment.host");
                String logStatement = "deploymentPing [" + deploymentPing + "]\n";
                logStatement += "deploymentDbPing [" + deploymentDbPing + "]\n";
                logStatement += "deploymentHost [" + deploymentHost + "]";
                LOGGER.debug(logStatement);
                if (util.isNotEmpty(deploymentHost)) {
                    if (util.isNotEmpty(deploymentDbPing)) {
                        String url = deploymentHost + "/" + deploymentDbPing;
                        LOGGER.debug("From pom.xml DBPing url [" + url + "]");
                        project.setDbPing(new URL(url));
                    }
                    if (util.isNotEmpty(deploymentPing)) {
                        String url = deploymentHost + "/" + deploymentPing;
                        LOGGER.debug("From pom.xml Ping url [" + url + "]");
                        project.setPing(new URL(url));
                    }
                }
                ProjectHolder.getInstance().updateProject(project);
            } catch (IOException ioe) {
                LOGGER.error("Problem with IO communication", ioe);
            } catch (XmlPullParserException xmle) {
                LOGGER.debug("Problem to Parse the XML file", xmle);
            } finally {
                FileUtil.closeStreams(fileReader);
            }
        } else {
            LOGGER.debug("Can not find an pom.xml at the path [" + pomXMLPath.getAbsolutePath() + "]");
        }
        LOGGER.debug("Leaving MavenHandler with updated project information.");
    }

    public boolean buildMaven(Project project, ConsoleResult consoleResult) {
        boolean buildMavenStatus = false;
        try {
            File projectPath = new File(project.getGitAbsolutePath());
            ShellCommand shellCommand = ShellCommand.getShellCommand();
            shellCommand.executeCommand(getBuildMavenCommand(projectPath), consoleResult);
            shellCommand.shutDownShellCommand();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildMavenStatus;
    }

    private String getBuildMavenCommand(File projectPath) {
        String command = "cd " + projectPath.getAbsolutePath();
        command += " && mvn clean install";
        return command;
    }
}