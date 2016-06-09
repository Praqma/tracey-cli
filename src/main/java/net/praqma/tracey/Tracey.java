package net.praqma.tracey;

import net.praqma.tracey.core.TraceyIllegalEventException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Tracey {

    public enum Eiffel {
        EiffelSourceChangeCreatedEvent
    }

    public final String[] cmd = {"say", "listen"};

    //Say something
    public static String say(String type, String json) throws TraceyIllegalEventException {
        Eiffel eif = null;
        try {
            eif = Eiffel.valueOf(type);
        } catch (IllegalArgumentException iex) {
            throw new TraceyIllegalEventException(type, iex);
        }
        if (eif.equals(Eiffel.EiffelSourceChangeCreatedEvent)) {
            System.out.println("EiffelSourceChangeEvent generated");
        }
        return null;
    }

    //Listen to events
    public static void listen(String eventType) {

    }

    public static CommandLine parse(String[] args) throws ParseException {
        Options opts = new Options();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(opts, args);
        return cmd;
    }

    /**
     *
     * @param args
     * <ul>
     * </ul>
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        CommandLine cli = parse(args);
    }
}
