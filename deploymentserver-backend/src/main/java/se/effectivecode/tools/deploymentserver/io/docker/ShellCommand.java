package se.effectivecode.tools.deploymentserver.io.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.effectivecode.tools.deploymentserver.io.logger.ConsoleResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public abstract class ShellCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShellCommand.class);
    private Process process = null;

    public void executeCommand(String command, ConsoleResult consoleResult) throws IOException {
        LOGGER.debug("Command to be executed [" + command + "]");
        ProcessBuilder builder = new ProcessBuilder(getCommandShell(), argCommand(), command);
        consoleResult.addLineToFile(command);
        executeCommand(builder, getEncoding(), consoleResult);
    }

    /**
     * The command windows encoding. For Windows it should be CP850, and for NIX it is probably UTF-8
     *
     * @return
     */
    protected abstract Charset getEncoding();

    /**
     * command is the command that should be run on the host.
     *
     * @param builder
     * @param encoding
     * @param consoleResult
     */
    protected void executeCommand(ProcessBuilder builder, Charset encoding, ConsoleResult consoleResult) {
        LOGGER.debug("ShellCommand.executeCommand: Start");
        try {
            builder.redirectErrorStream(true);
            process = builder.start();
            LOGGER.debug("process [" + process + "]");
            printStream(process.getInputStream(), consoleResult, encoding, "");
            printStream(process.getErrorStream(), consoleResult, encoding, "Error: ");
        } catch (IOException ioe) {
            LOGGER.debug("Wopps", ioe);
        }
        LOGGER.debug("ShellCommand.executeCommand: Exit");
    }

    private void printStream(InputStream inputStream, ConsoleResult consoleResult, Charset encoding, String type) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, encoding));
            String line;
            while ((line = br.readLine()) != null) {
                consoleResult.addLineToFile(type + line);
            }
        } catch (IOException ioe) {
            LOGGER.debug("IO problem", ioe);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void shutDownShellCommand() {
        if (process != null) {
            process.destroy();
        }
    }

    /**
     * Return the string that starts the up the command interface
     *
     * @return
     */
    protected abstract String getCommandShell();

    protected abstract String argCommand();

    public static ShellCommand getShellCommand() {
        ShellCommand command = new NIXShellCommand();
        String osVersion = System.getProperty("os.name");
        if (osVersion.contains("Windows")) {
            command = new WindowShellCommand();
        }
        return command;
    }

    public String name() {
        return this.getClass().getName();
    }
}