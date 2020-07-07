package se.effectivecode.tools.deploymentserver.io.repository;

import org.junit.jupiter.api.Test;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GitRepositoryHandlerTest {
    GitRepositoryHandler classToTest = new GitRepositoryHandler();

    @Test
    void downloadRepositoryWithCredentials() throws MalformedURLException {
        URL repositoryURL = new URL("https://git.dom.se/scm/mal/service-earkiv.git");
        classToTest.downloadRepository(repositoryURL);
    }

    @Test
    void downloadRepository() throws MalformedURLException {
        URL repositoryURL = new URL("https://git.dom.se/scm/~thombjo/swagger-ui.git");
        classToTest.downloadRepository(repositoryURL);
    }

    @Test
    void readProjectInformationFromRepository() {

    }

    @Test
    void extractRepoNameShouldReturnThePartAfterTheLastDash() throws MalformedURLException {
        URL repositoryURL = new URL("https://git.dom.se/scm/~thombjo/swagger-ui.git");
        String expected = "swagger-ui";
        assertEquals(expected, classToTest.extractRepoName(repositoryURL));
    }

    @Test
    void updateProjectShouldUpdateAnExistingProject() {
        String filePath = "C:\\dev\\code\\deploymentServer\\deploymentserver-backend\\repo\\service-earkiv";
        Project project = Project.builder().gitAbsolutePath(filePath).build();
        classToTest.updateProject(project);
    }

    @Test
    void getBranches() {
        String filePath = "C:\\dev\\code\\deploymentServer\\deploymentserver-backend\\repo\\service-earkiv";
        Project project = Project.builder().gitAbsolutePath(filePath).build();
        classToTest.getBranches(project);
    }

    @Test
    void checkoutBranchDevelop(){
        String filePath = "C:\\dev\\code\\deploymentServer\\deploymentserver-backend\\repo\\service-earkiv";
        Project project = Project.builder().gitAbsolutePath(filePath).build();
        classToTest.checkoutBranch(project, "develop");
    }
}