package se.effectivecode.tools.deploymentserver.configuration;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import se.effectivecode.tools.deploymentserver.io.projectfile.DeploymentProject;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Ignore
class ProjectHolderTest {
    @Tested
    ProjectHolder classToTest = ProjectHolder.getInstance();

    @Injectable
    DeploymentProject dProject;

    @Test
    void getInstanceShouldAlwaysReturnAnInstanceOfProjectHolder() {
        assertTrue(ProjectHolder.class.isInstance(ProjectHolder.getInstance()));
    }

    @Test
    void removeProject() {
        new Expectations() {{

        }};
        initTestData();
        List<Project> projects = classToTest.getProjects();
        int size = projects.size();
        Project toRemove = projects.get(0);
        classToTest.removeProject(toRemove);
        List<Project> listAfterRemove = classToTest.getProjects();
        assertEquals(size - 1, listAfterRemove.size());
        assertFalse(listAfterRemove.contains(toRemove));
    }

    @Test
    void addProject() {
        new Expectations() {{

        }};
        String name = "service-earkiv";
        String version = "1.0.2-SNAPSHOT";
        Project project = Project.builder().name(name).version(version).build();
        classToTest.addProject(project);
        boolean exist = false;
        for (Project p : classToTest.getProjects()) {
            if (p.equals(project)) {
                exist = true;
                break;
            }
        }
        assertTrue(exist);
    }

    @Test
    void addThreeProject() throws MalformedURLException {
        classToTest.clear();
        assertEquals(0, classToTest.getProjects().size());
        Project project = Project.builder().gitURL(new URL("https://git.dom.se/scm/mal/service-earkiv.git")).build();
        classToTest.addProject(project);
        project = Project.builder().gitURL(new URL("https://git.dom.se/scm/mal/service-earkiv.git")).build();
        classToTest.addProject(project);
        project = Project.builder().gitURL(new URL("https://git.dom.se/scm/mal/service-earkiv-sokmal.git")).build();
        classToTest.addProject(project);
        assertEquals(2, classToTest.getProjects().size());
    }


    @Test
    void getProjectsShouldReturnAnListOfActiveProject() {
        new Expectations() {{
            dProject.writeToXML(new ArrayList());
            result = getProjectTestData();
        }};
        assertEquals(3, classToTest.getProjects().size());
    }

    private void initTestData() {
        classToTest.clear();
        classToTest.addProject(Project.builder().name("service-earkiv").version("1.0").build());
        classToTest.addProject(Project.builder().name("service-earkiv-sokmal").version("0.9-SNAPSHOT").build());
        classToTest.addProject(Project.builder().name("app-earkivutlamning-web").version("1.4.8-SNAPSHOT").build());
    }

    private List<Project> getProjectTestData() {
        List<Project> projects = new ArrayList<>();
        projects.add(Project.builder().name("service-earkiv").version("1.0").build());
        projects.add(Project.builder().name("service-earkiv-sokmal").version("0.9-SNAPSHOT").build());
        projects.add(Project.builder().name("app-earkivutlamning-web").version("1.4.8-SNAPSHOT").build());
        return projects;
    }
}