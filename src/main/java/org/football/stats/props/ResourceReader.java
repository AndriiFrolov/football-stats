package com.active.conversion.props;


import com.active.conversion.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;


public class ResourceReader {

    private ResourceReader() {
    }

    public static String getResourceAsString(final String pathWithinResFolder) {
        InputStream resourceStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(pathWithinResFolder);
        if (Objects.isNull(resourceStream)) {
            throw new ResourseReadFailedException("Resource not found: " + pathWithinResFolder);
        }
        try (Scanner scanner = new Scanner(resourceStream, StandardCharsets.UTF_8)) {
            Log.info(String.format("Reading resource: %s", pathWithinResFolder));
            return scanner.useDelimiter("\\A").next();
        }
    }

    public static Properties getResourceAsProperties(String path) {
        try (final InputStream stream = ResourceReader.class.getClassLoader().getResourceAsStream(path)) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            throw new ResourseReadFailedException("Problem occurred during reading properties file", e);
        }
    }


}
