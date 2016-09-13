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

public class Tracey {

    private static TraceyRabbitMQBrokerImpl broker;
    private static RabbitMQRoutingInfo routingInfo;

    public static String readFileToString(String path) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        return content;
    }

    public static void main(String[] args) throws Exception {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("tracey")
        .description("Send message with Tracey. Using RabbitMQ\n\nRun with arguments" +
                "'say -h' for help in sending messages\nRun with arguments 'listen -h' for help receiving messages.\n" +
                "You may use environment variable names for user name and password to avoid storing\n " +
                "them in plain text. Use the following format - %SOMETEXT%, ${SOMETEXT}, $SOMETEXT, $SOMETEXT$.\n" +
                "Please notice command line options will override values provided through the configuration file\n" +
                "If no arguments will be provided, a connection will be configured with default values:\n" +
                String.format("host: %s", RabbitMQDefaults.HOST) + "\n" +
                String.format("port: %s", RabbitMQDefaults.PORT) + "\n" +
                String.format("user: %s", RabbitMQDefaults.USERNAME) + "\n" +
                String.format("password: %s", RabbitMQDefaults.PASSWORD) + "\n" +
                String.format("exchange type: %s", RabbitMQDefaults.EXCHANGE_TYPE) + "\n" +
                String.format("exchange name: %s", RabbitMQDefaults.EXCHANGE_NAME) + "\n" +
                String.format("delivery mode: %s", RabbitMQDefaults.DELEIVERY_MODE) + "\n" +
                String.format("headers: %s", RabbitMQDefaults.HEADERS) + "\n"

        );
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
        }
    }
}
