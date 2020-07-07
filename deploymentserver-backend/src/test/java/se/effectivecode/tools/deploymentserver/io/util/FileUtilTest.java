package se.effectivecode.tools.deploymentserver.io.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {
    String FILENAME = "FileSomSkallSparas.xml";
    String DIRECTORY = "C:\\dev\\code\\deploymentServer\\deploymentserver-backend\\repo\\service-earkiv";

    @Test
    void writeToFile() {
        String data = "data som skall sparas";
        File fileToSave = new File("." + File.separator + FILENAME);
        FileUtil.writeToFile(fileToSave, data);
        String readFromFile = FileUtil.readFromFile(fileToSave);
        assertEquals(data.trim(), readFromFile.trim());
        fileToSave.deleteOnExit();
    }

    @Test
    void removeDirectory() {
        File dirToRemove = new File(DIRECTORY);
        assertFalse(FileUtil.removeDirectory(dirToRemove));
    }
}