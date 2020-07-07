package se.effectivecode.tools.deploymentserver.io.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectRESTTest {

    ProjectREST classToTest = new ProjectREST();
    private String GIT_URL = "https://git.dom.se/scm/mal/service-earkiv.git";

    @Test
    void registerProjectShouldDownloadAProjectAndThenRegisterItToTheRegister() {
        String response = classToTest.registerProject(GIT_URL);
        assertEquals("Something wrong", response);
    }

    @Test
    void removeProjectshouldRemoveCreatedProect() {
        String outInformationToCaller = classToTest.removeProject(GIT_URL);
        assertEquals("Something is not right here", outInformationToCaller);
    }

    @Test
    void deployProjectShouldStopContainerRemoveImageCreateImageAndThenRestartTheContainer() {
        String informationAboutStatus = classToTest.deployProject(GIT_URL, null);
        assertEquals("Something happends here", informationAboutStatus);
    }
}