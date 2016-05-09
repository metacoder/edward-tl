package de.metacoder.edwardthreadlocal;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by becker on 5/9/16.
 */
public class AgentProperties {

    private static final Properties properties = new Properties();

    private static final String PROPERTY_FILE_NAME = "/edward-tl.properties";

    static {
        try {
            properties.load(AgentProperties.class.getResourceAsStream(PROPERTY_FILE_NAME));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load agent properties " + PROPERTY_FILE_NAME);
        }

        assert properties.get("foo") != null;
    }

}
