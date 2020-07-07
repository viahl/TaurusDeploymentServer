package se.effectivecode.tools.deploymentserver.io.docker;

import java.nio.charset.Charset;

public class WindowShellCommand extends ShellCommand {
    private final static Charset CHARSET_850 = Charset.forName("CP850");

    @Override
    protected Charset getEncoding() {
        return CHARSET_850;
    }

    @Override
    protected String getCommandShell() {
        return "cmd.exe";
    }

    @Override
    protected String argCommand() {
        return "/c";
    }
}
