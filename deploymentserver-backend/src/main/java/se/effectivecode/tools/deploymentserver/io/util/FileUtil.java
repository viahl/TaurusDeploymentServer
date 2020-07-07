package se.effectivecode.tools.deploymentserver.io.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;


public class FileUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static void writeToFile(File file, String data) {

        if (file != null) {
            BufferedWriter bw = null;
            FileWriter fw = null;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                Files.newBufferedWriter(file.toPath(), StandardOpenOption.TRUNCATE_EXISTING);
                fw = new FileWriter(file.getAbsolutePath());
                bw = new BufferedWriter(fw);
                bw.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeStreams(bw);
                closeStreams(fw);
            }
        }
    }

    public static String readFromFile(File file) {
        String outXML = "";
        if (file != null) {
            FileReader fr = null;
            BufferedReader br;
            br = null;
            try {
                fr = new FileReader(file.getAbsolutePath());
                br = new BufferedReader(fr);
                String tmpStr;
                while ((tmpStr = br.readLine()) != null) {
                    outXML += tmpStr + "\n";
                }
            } catch (IOException ioe) {
                LOGGER.error("Communication problem", ioe);
            } finally {
                closeStreams(br);
                closeStreams(fr);
            }
        }
        return outXML;
    }

    public static void closeStreams(Closeable streamToClose) {
        if (streamToClose != null) {
            try {
                streamToClose.close();
            } catch (IOException e) {
                /*
                We are only interested to close down the stream.
                An IOException only happends if the stream is no
                existent or already closed down.
                Therefore we ignore the exception.
                */
            }
        }
    }

    public static boolean removeDirectory(File directoryToBeRemoved) {
        boolean directoryDeleted;
        try {
            delete(directoryToBeRemoved);
            directoryDeleted = true;
        } catch (IOException e) {
            LOGGER.debug("Could not delete file [" + e.getMessage() + "]", e);
            directoryDeleted = false;
        }
        return directoryDeleted;
    }

    static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File dirFile : file.listFiles()) {
                delete(dirFile);
            }
        }
        if (!file.delete()) {
            throw new FileNotFoundException("Could not delete file: " + file);
        }
    }
}
