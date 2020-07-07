package se.effectivecode.tools.deploymentserver.io.docker;

import java.nio.charset.Charset;

public class NIXShellCommand extends ShellCommand {
    private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    @Override
    protected Charset getEncoding() {
        return CHARSET_UTF8;
    }

    @Override
    protected String getCommandShell() {
        return "sh";
    }

    @Override
    protected String argCommand() {
        return "-c";
    }
}
