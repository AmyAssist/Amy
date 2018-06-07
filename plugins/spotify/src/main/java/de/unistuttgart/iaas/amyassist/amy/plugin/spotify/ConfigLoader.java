package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

	private Properties p;

	public ConfigLoader() {
		p = new Properties();

	}

	public String get(String s) {
		try {
			if (getClass().getResourceAsStream("/spotify_config.properties") != null) {
				p.load(getClass().getResourceAsStream("/spotify_config.properties"));
				return p.getProperty(s);
			}
			return null;
		} catch (IOException e) {
			System.err.println("Error loading config file for spotify plugin");
			e.printStackTrace();
			return null;
		}

	}

	public void set(String key, String value) {
		p.setProperty(key, value);
		try {
			p.store(new FileWriter("target/classes/spotify_config.properties"), "Spotify Plugin");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
