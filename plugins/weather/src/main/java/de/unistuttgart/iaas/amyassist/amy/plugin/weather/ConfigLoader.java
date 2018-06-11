package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class ConfigLoader {

    private Properties p;

    public ConfigLoader() {
        p = new Properties();
        try {
            p.load(new FileReader("apikeys/weather_config.properties"));
        } catch (IOException e) {
            System.err.println("Error loading config file for weather plugin");
            e.printStackTrace();
        }
    }

    public String get(String s) {
        return p.getProperty(s);
    }
}
