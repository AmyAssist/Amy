package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private Properties p;

    public ConfigLoader() {
        p = new Properties();
        try {
            p.load(new FileReader("spotify_config.properties"));
        } catch (IOException e) {
            System.err.println("Error loading config file for spotify plugin");
            e.printStackTrace();
        }
    }

    public String get(String s) {
        return p.getProperty(s);
    }
    
    public void set(String key, String value) {
    	p.setProperty(key, value);
    	try {
			p.store(new FileWriter("spotify_config.properties"), "Spotify Plugin");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
}
