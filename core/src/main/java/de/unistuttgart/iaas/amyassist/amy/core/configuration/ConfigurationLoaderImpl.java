/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.core.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * Loads config files from config directory
 * 
 * @author Leon Kiefer
 */
@Service
public class ConfigurationLoaderImpl implements ConfigurationLoader {

	@Reference
	private Logger logger;
	@Reference
	private Environment environment;

	@Reference
	private CommandLineArgumentHandler cmaHandler;

	private static final String DEFAULT_CONFIG_DIR = "config";
	private List<Path> configDirs;

	@PostConstruct
	private void setup() {
		this.configDirs = new ArrayList<>();
		this.configDirs.add(this.environment.getWorkingDirectory().resolve(DEFAULT_CONFIG_DIR));

		List<String> cmaConfigPaths = this.cmaHandler.getConfigPaths();
		if (cmaConfigPaths != null) {
			for (String path : cmaConfigPaths) {
				this.configDirs.add(this.environment.getWorkingDirectory().resolve(path));
			}
		}

		boolean found = false;
		for (Path p : this.configDirs) {
			if (Files.isDirectory(p)) {
				found = true;
			} else {
				this.logger.warn("Configuration directory {} does not exist", p.toString());
			}
		}
		if (!found) {
			this.logger.error("No valid configuration directory found.");
		}
	}

	/**
	 * Finds the configuration file for the given name in all config directorys. The config dirs are checked from last
	 * to first.
	 * 
	 * @param configurationName
	 *            the name of the configuration to search
	 * @return The first path that matches the name or null if no such file was found.
	 */
	private Path findPropertiesFile(String configurationName) {
		for (int i = this.configDirs.size() - 1; i >= 0; i--) {
			Path p = this.configDirs.get(i).resolve(configurationName + ".properties");
			if (Files.exists(p))
				return p;
		}
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader#load(java.lang.String)
	 */
	@Override
	public Properties load(String configurationName) {
		Properties properties = new Properties();
		Path path = findPropertiesFile(configurationName);

		if (path == null) {
			this.logger.error("Could not load the configuration {}, because it was not found in any config dir.",
					configurationName);
			return null;
		}

		try (InputStream reader = Files.newInputStream(path)) {
			properties.load(reader);
		} catch (IOException e) {
			this.logger.error("Error loading config file", e);
		}
		return properties;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader#store(java.lang.String,
	 *      java.util.Properties)
	 */
	@Override
	public void store(String configurationName, Properties properties) {
		Path path = findPropertiesFile(configurationName);

		if (path == null) {
			path = this.configDirs.get(this.configDirs.size() - 1).resolve(configurationName + ".properties");
		}

		try (OutputStream outputStream = Files.newOutputStream(path)) {
			properties.store(outputStream, null);
		} catch (IOException e) {
			this.logger.error("Error saving config file", e);
		}
	}
}
