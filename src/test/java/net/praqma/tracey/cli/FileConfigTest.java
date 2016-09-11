package net.praqma.tracey.cli;

import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FileConfigTest {

    @Test
    public void testConfig() {
        File file = new File("src/test/resources/test-config.config");
        Map<String, Object> map = FileUtil.readFromFile(file);
        assertEquals(map.get("broker.rabbitmq.server.host"), "server1");
        assertEquals(map.get("broker.rabbitmq.server.username"), "name");
        assertEquals(map.get("broker.rabbitmq.server.password"), "password");
        assertEquals(map.get("broker.rabbitmq.server.port"), 4444);
        assertEquals(map.get("broker.rabbitmq.exchange.name"), "exchange");
        assertEquals(map.get("broker.rabbitmq.exchange.type"), "TOPIC");
        assertEquals(map.get("broker.rabbitmq.exchange.durable"), false);
        assertEquals(map.get("broker.rabbitmq.properties.deliverymode"), 1);
        assertEquals(map.get("broker.rabbitmq.properties.routingkey"), "key1");
    }

    @Test
    public void testJSONHeader() {
        File file = new File("src/test/resources/headers.json");
        Map<String, Object> map = FileUtil.readFromFile(file);
        Map<String, Object> message = (Map<String, Object>) map.get("message");
        assertEquals(message.get("type"), "file");
        assertEquals(message.get("content"), "text");
    }

    @Test
    public void testConfigHeader() {
        File file = new File("src/test/resources/headers.config");
        Map<String, Object> map = FileUtil.readFromFile(file);
        assertEquals(map.get("message.type"), "file");
        assertEquals(map.get("message.content"), "text");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBrokenSlurper() throws Exception {
        File file = new File("src/test/resources/headers.broken-config");
        FileUtil.readFromFile(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBrokenJson() throws Exception {
        File file = new File("src/test/resources/headers-broken.json");
        FileUtil.readFromFile(file);
    }
}
