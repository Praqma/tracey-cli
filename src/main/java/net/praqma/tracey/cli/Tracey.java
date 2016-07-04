package net.praqma.tracey.cli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQBrokerImpl;
import net.praqma.tracey.broker.TraceyBroker;
import net.praqma.tracey.broker.TraceyIOError;
import net.praqma.tracey.broker.TraceyValidatorError;
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQReceiverBuilder;
import org.apache.commons.cli.HelpFormatter;

public class Tracey {

    private static List<String> cmd = Arrays.asList("say", "listen");

    static TraceyBroker broker;

    //Say something
    public static String say(String json, String destination) throws TraceyIllegalEventException, TraceyValidatorError, TraceyIOError {
        return broker.send(json, destination);
    }

    //Listen to events
    public static void listen(String source) throws TraceyValidatorError, TraceyIOError {
        broker.receive(source);
    }

    public static CommandLine parse(String[] args, Options opts) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        opts.addOption("c", true, "Configuration file");
        opts.addOption("f" , false, "Specify this if the input is a file");
        opts.addOption("h", "help", false, "Prints help");
        CommandLine cmd = parser.parse(opts, args);
        return cmd;
    }


    public static File parseConfigFile(CommandLine cli) {
        if(cli.hasOption("c")) {
            String path = cli.getOptionValue("c");
            System.out.println("Parsing config file: "+path);
            File f = new File(path);
            return f;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Options opts = new Options();
        CommandLine cli = parse(args, opts);
        File f = parseConfigFile(cli);
        if(f != null) {
            broker = new TraceyRabbitMQBrokerImpl(f);
        } else {
            broker = new TraceyRabbitMQBrokerImpl();
        }

        if(cli.hasOption("h") || !cmd.contains(cli.getArgList().get(0))) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("tracey [say | listen] (options)\noptions:\n\n", opts);
        } else if(cli.getArgList().get(0).equals("say")) {
            //For say the 2nd parameter is optional (can be defined in config file)
            String destination = cli.getArgList().size() >= 2 ? cli.getArgList().get(2) : null;
            destination = TraceyRabbitMQReceiverBuilder.expand(destination);
            String msg = cli.getArgList().get(1);
            if(cli.hasOption("f")) {
                msg = new String(Files.readAllBytes(Paths.get(cli.getArgList().get(1))));
                say(msg, destination);
            } else {
                say(msg, destination);
            }
        } else {
            String source = cli.getArgList().size() >= 2 ? cli.getArgList().get(1) : null;
            listen(source);
        }
    }
}
