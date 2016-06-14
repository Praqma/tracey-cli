package net.praqma.tracey.cli;

public class TraceyUnknowCommandException extends TraceyCLIException {

    static String[] cmds = { "say", "listen" };

    public TraceyUnknowCommandException(String cmd, Throwable cause) {
        super(generateMessage(cmd), cause);
    }

    private static String generateMessage(String cmd) {
        return String.format("Illegal command specified: %s. Must be one of: [%s]", cmd, String.join(" ", cmds));
    }
    
}
