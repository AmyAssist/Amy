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

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.CommandLineArgumentInfo;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * Loads config files from config directory
 * 
 * @author Leon Kiefer
 */
@Service
public class ConfigurationLoaderImpl implements ConfigurationLoader {

	private static final String FILE_ENDING = ".properties";

	@Reference
	private Logger logger;
	@Reference
	private Environment environment;

	@Reference
	private CommandLineArgumentInfo cmaInfo;

	private static final String DEFAULT_CONFIG_DIR = "config";
	private List<Path> configDirs;

	@PostConstruct
	private void setup() {
		this.configDirs = new ArrayList<>();
		this.configDirs.add(this.environment.getWorkingDirectory().resolve(DEFAULT_CONFIG_DIR));

		for (String path : this.cmaInfo.getConfigPaths()) {
			this.configDirs.add(this.environment.getWorkingDirectory().resolve(path));
		}

		boolean found = false;
		for (Path p : this.configDirs) {
			if (p.toFile().isDirectory()) {
				found = true;
			} else {
				this.logger.warn("Configuration directory {} does not exist", p);
			}
		}
		if (!found) {
			this.logger.error("No valid configuration directory found.");
		}
	}

	/**
	 * Finds the configuration file for the given name in all config directories. The config dirs are checked from last
	 * to first.
	 * 
	 * @param configurationName
	 *            the name of the configuration to search
	 * @return The first path that matches the name or null if no such file was found.
	 */
	private Path findPropertiesFile(String configurationName) {
		for (int i = this.configDirs.size() - 1; i >= 0; i--) {
			Path p = this.configDirs.get(i).resolve(configurationName + FILE_ENDING);
			if (p.toFile().exists())
				return p;
		}
		return null;
	}

	@Nonnull
	@Override
	public Properties load(String configurationName) {
		Properties emptyDefault = new Properties();
		Properties load = this.load(configurationName, emptyDefault);
		// the references must be compared to check if a new properties is loaded. The equals method don't work because
		// empty properties can be loaded
		if (load == emptyDefault) {// NOSONAR
			this.logger.error("Could not load the configuration {}, because it was not found in any config dir.",
					configurationName);
		}
		return load;
	}

	@Nonnull
	@Override
	public Properties load(String configurationName, Properties defaults) {
		return this.loadAll(configurationName, defaults);
	}

	private Properties loadAll(String configurationName, Properties properties) {
		Properties loaded = properties;
		for (Path path : this.configDirs) {
			path = path.resolve(configurationName + FILE_ENDING);
			if (path.toFile().exists()) {
				loaded = new Properties(loaded);
				try (InputStream reader = Files.newInputStream(path)) {
					loaded.load(reader);
				} catch (IOException e) {
					this.logger.error("Error loading config file", e);
				}
			}
		}
		return loaded;
	}

	@Override
	public void store(String configurationName, Properties properties) {
		Path path = findPropertiesFile(configurationName);

		// No property file with this name was found, so create one in the highest priority config folder.
		if (path == null) {
			path = this.configDirs.get(this.configDirs.size() - 1).resolve(configurationName + FILE_ENDING);
		}

		try (OutputStream outputStream = Files.newOutputStream(path)) {
			properties.store(outputStream, null);
		} catch (IOException e) {
			this.logger.error("Error saving config file", e);
		}
	}
}
