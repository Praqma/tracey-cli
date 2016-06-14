package net.praqma.tracey.cli;

import java.util.ArrayList;
import java.util.List;

public class TraceyIllegalEventException extends TraceyCLIException {

    static List<String> names = new ArrayList<>();
    static {
        TraceyIllegalEventException.names.add("EiffelSourceChangeCreatedEvent");
    }

    public TraceyIllegalEventException(String eventType, Throwable source) {
        super(createMessage(eventType), source);
    }

    private static String createMessage(String eventType) {
        return String.format("Illegal event type declared. Declared value was %s, must be one of: [%s]", eventType, String.join(" ", TraceyIllegalEventException.names));
    }
}
