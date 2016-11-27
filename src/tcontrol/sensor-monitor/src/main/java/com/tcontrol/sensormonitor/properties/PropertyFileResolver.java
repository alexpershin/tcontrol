package com.tcontrol.sensormonitor.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton
public class PropertyFileResolver {

    private static final Logger LOGGER = Logger.getLogger(PropertyFileResolver.class.getName());

    private final Map<String, String> properties = new HashMap<>();

    @PostConstruct
    private void init() {

        //matches the property name as defined in the system-properties element in WildFly
        final String propertyFile = "application.properties";
        File file = new File(propertyFile);
        Properties props = new Properties();

        try {
            props.load(new FileInputStream(file));
            LOGGER.log(Level.INFO, "Properties loaded.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to load properties file '" + propertyFile + "': " + e);
        }

        HashMap hashMap = new HashMap<>(props);
        this.properties.putAll(hashMap);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }
}
