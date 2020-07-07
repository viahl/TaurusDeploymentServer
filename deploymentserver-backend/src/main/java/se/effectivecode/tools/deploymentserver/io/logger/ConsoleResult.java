package se.effectivecode.tools.deploymentserver.io.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ConsoleResult log the output from the console to a file on the path {project}/{version}/{timestamp}/output.txt
 */
public class ConsoleResult {
    /*
        TODO Fixa så att ConsoleResult kan logga samtidigt till fil, men också till GUI ruta i Angular.
        Det senare är en mycket senare och intressantare fråga hur man skall lösa det på ett snyggt och trevligt sätt.
         */
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsoleResult.class);
    public static final String TIME_PATTERN = "yyyy-MM-dd_HH-mm-ss";
    private FileOutputStream fileOutputStream;
    private final static String FILE_NAME = "output.txt";
    private final static String OUTDIR = "target";
    private Queue<String> processQueue = new LinkedList<>();

    private ConsoleResult() {
    }

    public static ConsoleResult createConsoleResult(String project, String version) throws IOException {
        ConsoleResult consoleResult = new ConsoleResult();
        consoleResult.createOutputTextFileStream(project, version);
        return consoleResult;
    }

    public void createOutputTextFileStream(String project, String version) throws IOException {
        String date = getDateAsString();
        File parentDir = new File(OUTDIR + File.separator + project + File.separator + version + File.separator + date);
        parentDir.mkdirs();
        File outputTextFile = new File(parentDir.getAbsolutePath() + File.separator + FILE_NAME);
        System.out.println(outputTextFile.getAbsolutePath());
        outputTextFile.createNewFile();
        fileOutputStream = new FileOutputStream(outputTextFile, false);
    }

    String getDateAsString() {
        return new SimpleDateFormat(TIME_PATTERN).format(new Date());
    }

    public void addLineToFile(String lineToAdd) {
        processQueue.add(lineToAdd);
        try {
            if (lineToAdd != null && !lineToAdd.isEmpty()) {
                String writeLine = lineToAdd + "\n";
                fileOutputStream.write(writeLine.getBytes());
                LOGGER.debug(lineToAdd);
            } else {
                LOGGER.error("Do I get information here [" + lineToAdd + "]");
            }
        } catch (IOException ioe) {
// Do nothing. Push information to the Queue
        }
    }

    public String nextResultString() {
        return processQueue.poll();
    }

    boolean streamExist() {
        return fileOutputStream != null;
    }

    public void closeFileOutputStream() {
        if (streamExist()) {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException ioe) {
                LOGGER.error("There is some kinds of IO problem", ioe);
            }
        }
    }
}