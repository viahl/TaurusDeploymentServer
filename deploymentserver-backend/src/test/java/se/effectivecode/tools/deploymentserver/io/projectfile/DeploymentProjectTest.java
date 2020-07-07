package se.effectivecode.tools.deploymentserver.io.projectfile;

import org.junit.jupiter.api.Test;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeploymentProjectTest {
    DeploymentProject classToTest = new DeploymentProject();

    @Test
    void verifyThatTheSameAmountOfProjectsThatIsWrittenDownCanBeReadUpFromTheFile() {
        int expected = 4;
        List<Project> projects = getProjectData();
        assertEquals(expected, projects.size());

        classToTest.writeToXML(projects);
        List<Project> list = classToTest.readProjectsFromFile();
        assertEquals(expected, list.size());
    }

    @Test
    void getProjectFileShouldHaveTheNameOfTheFileToDeploymentProject() throws IOException {
        File projectFile = classToTest.getProjectFile();
        String expected = "DeploymentProject.xml";
        assertEquals(expected, projectFile.getName());
    }

    @Test
    void getProjectFileShouldHaveTheParentNameDirectoryProject() throws IOException {
        File projectFile = classToTest.getProjectFile();
        String expected = "project";
        assertEquals(expected, projectFile.getParent());
    }

    private List<Project> getProjectData() {
        List<Project> outList = new ArrayList<>();
        outList.add(Project.builder().name("app-earkivutlämning").version("0.0.1-SNAPSHOT").build());
        outList.add(Project.builder().name("service-earkiv").version("0.1.1-SNAPSHOT").build());
        outList.add(Project.builder().name("service-earkiv-sokmal").version("1.0.7").build());
        outList.add(Project.builder().name("utlämning-simulatorn").version("0.4.3").build());
        return outList;
    }
}
