package se.effectivecode.tools.deploymentserver.io.projectfile;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.effectivecode.tools.deploymentserver.io.util.FileUtil;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * DeploymentProject handles information about the project.
 * Reads up, and writes down information about the project.
 * The location of the project file is /project/project.xml
 *
 * @author Viggo Ahl, Effective Code AB
 */
public class DeploymentProject {
    private final static Logger LOG = LoggerFactory.getLogger(DeploymentProject.class);
    private final static String FILENAME = "DeploymentProject.xml";
    private final static String PATH = "project";

    public void writeToXML(List<Project> projects) {
        XStream xstream = new XStream();
        xstream.alias(PATH, Project.class);
        String xml = xstream.toXML(projects);
        File file = getProjectFile();
        file.getParentFile().mkdirs();
        FileUtil.writeToFile(file, xml);
        LOG.debug("Does the file exist now [" + file.exists() + "] with absolute path [" + file.getAbsolutePath() + "]");
    }

    public List<Project> readProjectsFromFile() {
        XStream xStream = new XStream();
        xStream.alias(PATH, Project.class);
        File file = getProjectFile();
        LOG.debug("Does " + FILENAME + " exist [" + file.exists() + "]");
        List<Project> projectList = new ArrayList<>();
        if (!file.exists()) {
            LOG.debug("File does not exist and should now be created");
            writeToXML(new ArrayList<Project>());
        } else {
            LOG.debug("There exist an [" + FILENAME + ". Now we read from it to the ProjectList");
            projectList = ArrayList.class.cast(xStream.fromXML(file));
        }
        LOG.debug("Returning an list of size [" + projectList.size() + "]");
        return projectList;
    }

    File getProjectFile() {
        return new File(PATH + File.separator + FILENAME);
    }
}
