package se.effectivecode.tools.deploymentserver.io.logger;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleResultTest {
    ConsoleResult classToTest = ConsoleResult.createConsoleResult("Service-earkiv", "1.0.0-SNAPSHOT");

    ConsoleResultTest() throws IOException {
    }

    @Test
    void createOutputTextFileStream() throws IOException {
        assertFalse(classToTest.streamExist());
        String project = "soul";
        String version = "1.0-SNAPSHOT";
        classToTest.createOutputTextFileStream(project, version);
        assertTrue(classToTest.streamExist());
    }

    @Test
    void addLineToFile() throws IOException {
        String project = "soul";
        String version = "1.0-SNAPSHOT";
        classToTest.createOutputTextFileStream(project, version);
        assertTrue(classToTest.streamExist());
        classToTest.addLineToFile("TestLine");
        classToTest.addLineToFile("TestLine 2");
        classToTest.closeFileOutputStream();
        assertFalse(classToTest.streamExist());
    }

    @Test
    void closeFileOutputStream() throws IOException {
        String project = "soul";
        String version = "1.0-SNAPSHOT";
        classToTest.createOutputTextFileStream(project, version);
        assertTrue(classToTest.streamExist());
        classToTest.closeFileOutputStream();
        assertFalse(classToTest.streamExist());
    }

    @Test
    void getDateAsStringShouldReturnAStringContainingTheDateAndTime() {
        String dateAsString = classToTest.getDateAsString();
        System.out.println(dateAsString);
    }
}