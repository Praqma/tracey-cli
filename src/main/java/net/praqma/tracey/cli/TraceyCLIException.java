package net.praqma.tracey.cli;

public class TraceyCLIException extends Exception {
    protected final String input;

    public TraceyCLIException(String eventType, Throwable source) {
        super(source);
        this.input = eventType;
    }
}
