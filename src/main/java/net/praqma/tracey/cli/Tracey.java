package net.praqma.tracey.cli;

import net.praqma.tracey.broker.impl.rabbitmq.RabbitMQDefaults;
import net.praqma.tracey.broker.impl.rabbitmq.RabbitMQRoutingInfo;
import net.praqma.tracey.broker.impl.rabbitmq.TraceyRabbitMQBrokerImpl;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Tracey {

    private static TraceyRabbitMQBrokerImpl broker;
    private static RabbitMQRoutingInfo routingInfo;
    private static final Logger LOG = Logger.getLogger(Tracey.class.getName());

    public static String readFileToString(String path) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        return content;
    }

    public static void main(String[] args) throws Exception {
        final String description = String.format(
                "Send or receive messages with using RabbitMQ\n\n" +
                "Run with the following positional arguments:\n" +
                "'say -h' to see how to send messages\n" +
                "'listen -h' to see how to receive messages\n\n" +
                "Tips\nYou can use environment variable names for user name and password to avoid storing them in plain text.\n" +
                "Use the following format - %%SOMETEXT%%, ${SOMETEXT}, $SOMETEXT, $SOMETEXT$.\n\n" +
                "Notice command line options will override values provided through the configuration file.\n" +
                "If a certain option not provided neither through configuration file or command line argument then\n" +
                "the following default value will be used:\n" +
                "host: %s \nport: %s \nuser: %s \npassword: %s \nexchange name: %s \nexchange type: %s \ndelivery mode: %s \nheaders: %s",
                RabbitMQDefaults.HOST,
                RabbitMQDefaults.PORT,
                RabbitMQDefaults.USERNAME,
                RabbitMQDefaults.PASSWORD,
                RabbitMQDefaults.EXCHANGE_NAME,
                RabbitMQDefaults.EXCHANGE_TYPE,
                RabbitMQDefaults.DELEIVERY_MODE,
                RabbitMQDefaults.HEADERS.toString());
        ArgumentParser parser = ArgumentParsers.newArgumentParser("tracey")
        .description(description);
        Subparsers subparsers = parser.addSubparsers();
        Subparser sayParser = subparsers.addParser("say");
        sayParser.addArgument("message");
        sayParser.addArgument("-n", "--node").dest("node").help("The URL of the RabbitMQ server");
        sayParser.addArgument("-e", "--exchange").help("Exhange name");
        sayParser.addArgument("-u", "--user").dest("user").help("Username");
        sayParser.addArgument("-s", "--secret").dest("secret").help("Password");
        sayParser.addArgument("-p", "--port").dest("port").type(Integer.class).help("Port");
        sayParser.addArgument("-c", "--configure").dest("config").help("Point to a config file");
        sayParser.addArgument("-f", "--file-payload").dest("payload").action(Arguments.storeTrue()).help("This message is a file. Read the contents");

        Subparser listenParser = subparsers.addParser("listen");
        listenParser.addArgument("-n", "--node").dest("node").help("The URL of the RabbitMQ server");
        listenParser.addArgument("-e", "--exchange").dest("exchange").help("Exhange name");
        listenParser.addArgument("-u", "--user").dest("user").help("Username");
        listenParser.addArgument("-s", "--secret").dest("secret").help("Password");
        listenParser.addArgument("-p", "--port").dest("port").type(Integer.class).help("Port");
        listenParser.addArgument("-c", "--configure").dest("config").help("Point to a config file");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        broker = new TraceyRabbitMQBrokerImpl();
        routingInfo = new RabbitMQRoutingInfo();

        if(ns.getString("config") != null) {
            File config = new File(ns.getString("config"));
            broker = new TraceyRabbitMQBrokerImpl(config);
            routingInfo = RabbitMQRoutingInfo.buildFromConfigFile(config);
        }

        if(ns.getString("node") != null) {
            broker.getReceiver().getConnection().setHost(ns.getString("node"));
            broker.getSender().getConnection().setHost(ns.getString("node"));
        }

        if(ns.getString("secret") != null) {
            broker.getReceiver().getConnection().setPassword(ns.getString("secret"));
            broker.getSender().getConnection().setPassword(ns.getString("secret"));
        }

        if(ns.getString("user") != null) {
            broker.getReceiver().getConnection().setUserName(ns.getString("user"));
            broker.getSender().getConnection().setUserName(ns.getString("user"));
        }

        if(ns.getInt("port") != null) {
            broker.getReceiver().getConnection().setPort(ns.getInt("port"));
            broker.getSender().getConnection().setPort(ns.getInt("port"));
        }

        if(ns.getString("type") != null) {
            routingInfo.setExchangeType(ns.getString("type"));
        }

        if(ns.getString("exchange") != null) {
            routingInfo.setExchangeName(ns.getString("exchange"));
        }

        String message = ns.getString("message");

        if(message == null) {
            broker.receive(routingInfo);

        } else {

            String actualMessage = message;

            if(ns.getBoolean("payload")) {
                actualMessage = readFileToString(message);
            }
            broker.send(actualMessage, routingInfo);
            LOG.info(String.format("Message sent succesfully\nhost: %s \nport: %s \nexchange name: %s \nexchange type: %s \ndelivery mode: %s \nheaders: %s",
                    broker.getSender().getConnection().getHost(),
                    broker.getSender().getConnection().getPort(),
                    routingInfo.getExchangeName(),
                    routingInfo.getExchangeType(),
                    routingInfo.getDeliveryMode(),
                    routingInfo.getHeaders().toString()));
        }
        System.exit(0);
    }
}
