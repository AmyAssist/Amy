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
public class ConfigurationLoader {

	@Reference
	private Logger logger;
	@Reference
	private Environment environment;

	@Reference
	private CommandLineArgumentHandler cmaHandler;

	private static final String DEFAULT_CONFIG_DIR = "config";
	private Path configDir;

	@PostConstruct
	private void setup() {
		String configDirS = DEFAULT_CONFIG_DIR;

		String cmaConfigPath = this.cmaHandler.getConfigPath();
		if (cmaConfigPath != null) {
			configDirS = cmaConfigPath;
		}

		this.configDir = this.environment.getWorkingDirectory().resolve(configDirS);
		if (!this.configDir.toFile().isDirectory()) {
			this.logger.error("the configuration directory {} does not exists", this.configDir.toAbsolutePath());
		}
	}

	/**
	 * 
	 * @param configurationName
	 *            the name of the config file, without the .properties
	 * @return the loaded Properties
	 */
	public Properties load(String configurationName) {
		Properties properties = new Properties();
		Path path = this.configDir.resolve(configurationName + ".properties");

		try (InputStream reader = Files.newInputStream(path)) {
			properties.load(reader);
		} catch (IOException e) {
			this.logger.error("Error loading config file", e);
		}
		return properties;
	}

	/**
	 * 
	 * @param configurationName
	 *            the name of the config file, without the .properties
	 * @param properties
	 *            the Properties to be saved
	 */
	public void store(String configurationName, Properties properties) {
		Path path = this.configDir.resolve(configurationName + ".properties");

		try (OutputStream outputStream = Files.newOutputStream(path)) {
			properties.store(outputStream, null);
		} catch (IOException e) {
			this.logger.error("Error saving config file", e);
		}
	}
}
