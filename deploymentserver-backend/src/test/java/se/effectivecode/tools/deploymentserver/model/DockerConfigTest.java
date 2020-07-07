package se.effectivecode.tools.deploymentserver.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DockerConfigTest {
    DockerConfig classToTest;

    @Test
    void volumeShouldReturnEmptyStringWhenThereIsNoVolumeDefined() {
        classToTest = DockerConfig.builder().volumeWindows("").volumeLinux("").build();
        assertEquals("", classToTest.volume());
    }

    @Test
    void volumeShouldReturnMinusVSpaceThenTheVolumeDefinition() {
        String volumeWindows = "c:\\dev\\empty";
        String volumeLinux = "/opt/apps/data";
        classToTest = DockerConfig.builder().volumeLinux(volumeLinux).volumeWindows(volumeWindows).build();
        assertEquals(" -v " + volumeWindows, classToTest.volume());
    }

    @Test
    void bindPortsShouldReturnAnMapContainingAnListOfBindPortsAndAnotherListOfExposedPorts(){

    }
}