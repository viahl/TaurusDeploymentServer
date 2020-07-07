package se.effectivecode.tools.deploymentserver.io.docker;


import org.junit.jupiter.api.Test;
import se.effectivecode.tools.deploymentserver.io.logger.ConsoleResult;

import java.io.IOException;
import java.nio.charset.Charset;

class WindowShellCommandTest {
    private WindowShellCommand classToTest = new WindowShellCommand();
    private String project = "SOUL";
    private String version = "1.0.0-SNAPSHOT";

    @Test
    public void verifyExecuteCommand() throws IOException {
        ConsoleResult consoleResult = ConsoleResult.createConsoleResult(project, version);
        classToTest.executeCommand("dir", consoleResult);
        consoleResult.closeFileOutputStream();
    }

    @Test
    public void verifyThatSetIsWorkingAsCommand() throws IOException {
        ConsoleResult consoleResult = ConsoleResult.createConsoleResult(project, version);
        classToTest.executeCommand("SET", consoleResult);
        consoleResult.closeFileOutputStream();
    }

    @Test
    public void verifyThatDockerExist() throws IOException {
        ConsoleResult consoleResult = ConsoleResult.createConsoleResult(project, version);
        classToTest.executeCommand("docker --version", consoleResult);
        consoleResult.closeFileOutputStream();
    }

    @Test
    public void tryingToBuildAContainer() throws IOException {
        ConsoleResult consoleResult = ConsoleResult.createConsoleResult(project, version);
        classToTest.executeCommand("docker build --file service-earkiv --tag=service-earkiv .", consoleResult);
        consoleResult.closeFileOutputStream();
    }
}