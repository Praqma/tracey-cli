package net.praqma.tracey.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.ConfigSlurper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * if we suppose to open connection to every message we send,
 * it's make sense to keep headers and delivery information in one config file
 * Class to parse *.config file. We suppose it will come ConfigSluper :
 * ```broker{
 *      rabbitmq{
 *          server{
 *              host = ''
 *              port = ''
 *              username = ''
 *              password = ''
 *          }
 *          exchange{
 *              name = ''
 *              type = ' '
 *              durable = ''
 *          }
 *          properties{
 *              ???headers = ['':'', ]???
 *              deliverymode = 1
 *              routingkey = ''
 *          }
 *      }
 * }```

 */
public class FileUtil {

    private FileUtil() {
    }

    public static Map<String, Object> readFromFile(File file) {
        Map<String, Object> result = tryConfigSlurper(file);
        if (result != null) {
            return result;
        }

        result = tryConfigJson(file);
        if (result != null) {
            return result;
        }

        throw new IllegalArgumentException(file + " has unsupported type");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> tryConfigSlurper(File file) {
        try {
            return new ConfigSlurper()
                    .parse(file.toURI().toURL())
                    .flatten();
        } catch (Exception ignored) {
        }

        return null;
    }

    private static Map<String, Object> tryConfigJson(File file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {});
        } catch (IOException ignored) {
        }

        return null;
    }
}
