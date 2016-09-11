package net.praqma.tracey.cli;

import net.praqma.tracey.broker.rabbitmq.RabbitMQRoutingInfo;
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQBrokerImpl;
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQBrokerImpl.ExchangeType;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Tracey {

    //Sensible defaults:
    static String host = "localhost";
    static String exchange = "tracey";
    static String user = "guest";
    static String pw = "guest";
    static String routingkey = "";
    static int deliveryMode = 1;
    static int port = 5672;
    static ExchangeType type = TraceyRabbitMQBrokerImpl.ExchangeType.TOPIC;

    static TraceyRabbitMQBrokerImpl broker;
    static Map<String, Object> headers = new HashMap<String, Object>();

    public static String readFileToString(String path) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        return content;
    }

    public static void main(String[] args) throws Exception {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("tracey")
        .description("Send message with Tracey. Using RabbitMQ\n\nRun with arguments"
                + " 'say -h' for help in sending messages\nRun with arguments 'listen -h' for help receiving messages");

        Subparsers subparsers = parser.addSubparsers();
        Subparser sayParser = subparsers.addParser("say").defaultHelp(true);
        sayParser.addArgument("message");
        sayParser.addArgument("-n", "--node").dest("node").help("The URL of the RabbitMQ server");
        sayParser.addArgument("-e", "--exchange").setDefault("tracey").help("Exhange name");
        sayParser.addArgument("-u", "--user").dest("user").help("Username");
        sayParser.addArgument("-h", "--headers").dest("headers").help("Add attribute file to the message");
        sayParser.addArgument("-s", "--secret").dest("secret").help("Password");
        sayParser.addArgument("-p", "--port").dest("port").type(Integer.class).help("Port");
        sayParser.addArgument("-c", "--configure").dest("config").help("Point to a config file");
        sayParser.addArgument("-f", "--file-payload").dest("payload").action(Arguments.storeTrue()).help("This message is a file. Read the contents");

        Subparser listenParser = subparsers.addParser("listen");
        listenParser.addArgument("-n", "--node").dest("node").help("The URL of the RabbitMQ server");
        listenParser.addArgument("-e", "--exchange").dest("exchange").help("Exhange name");
        listenParser.addArgument("-u", "--user").dest("user").help("Username");
        listenParser.addArgument("-h", "--headers").dest("headers").help("Add attribute file to the message");
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

        broker = new TraceyRabbitMQBrokerImpl(host, user, user, type, exchange);

        if(ns.getString("config") != null) {
            File config = new File(ns.getString("config"));
            broker = new TraceyRabbitMQBrokerImpl(config);
            exchange = (String)FileUtil.readFromFile(config).get("broker.rabbitmq.exchange.name");
            deliveryMode = (int)FileUtil.readFromFile(config).get("broker.rabbitmq.properties.deliverymode");
            routingkey = (String)FileUtil.readFromFile(config).get("broker.rabbitmq.properties.routingkey");
        }

        if(ns.getString("node") != null) {
            broker.getReceiver().setHost(ns.getString("node"));
            broker.getSender().setHost(ns.getString("node"));
        }

        if(ns.getString("secret") != null) {
            broker.getReceiver().setPassword(ns.getString("secret"));
            broker.getSender().setPw(ns.getString("secret"));
        }

        if(ns.getString("user") != null) {
            broker.getReceiver().setUsername(ns.getString("user"));
            broker.getSender().setUsername(ns.getString("user"));
        }

        if(ns.getInt("port") != null) {
            broker.getReceiver().setPort(ns.getInt("port"));
            broker.getSender().setPort(ns.getInt("port"));
        }

        if(ns.getString("type") != null) {
            broker.getReceiver().setType(ExchangeType.valueOf(ns.getString("type")));
            broker.getSender().setType(ExchangeType.valueOf(ns.getString("type")));
        }

        if(ns.getString("exchange") != null) {
            broker.getReceiver().setExchange(ns.getString("exchange"));
        }

        broker.configure();

        String message = ns.getString("message");
        // Check if we have headers file
        if(ns.getString("headers") != null){
            File file = new File(ns.getString("headers"));
            headers = FileUtil.readFromFile(file);
        }
        RabbitMQRoutingInfo data = new RabbitMQRoutingInfo(headers, exchange, deliveryMode, routingkey);

        if(message == null) {
            broker.receive(data);

        } else {

            String actualMessage = message;

            if(ns.getBoolean("payload")) {
                actualMessage = readFileToString(message);
            }
            broker.send(actualMessage, data);
        }
    }
}
