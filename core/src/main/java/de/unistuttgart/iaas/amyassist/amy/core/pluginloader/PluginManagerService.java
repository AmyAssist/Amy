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

package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import javax.persistence.Entity;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.Configuration;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.Persistence;

/**
 * Manages the plugin integration.
 * 
 * For an explanation of the config see <a
 * href=https://github.com/AmyAssist/Amy/wiki/PluginManager#config>https://github.com/AmyAssist/Amy/wiki/PluginManager#config</a>.
 * 
 * @author Leon Kiefer, Tim Neumann
 */
@Service
public class PluginManagerService implements PluginManager {
	private static final String CONFIG_NAME = "plugin.config";
	private static final String PROPERTY_MODE = "mode";
	private static final String PROPERTY_MODE_DEV = "dev";
	private static final String PROPERTY_MODE_DOCKER = "docker";
	private static final String PROPERTY_MODE_MANUAL = "manual";
	private static final String PROPERTY_PLUGINS = "plugins";
	private static final String PROPERTY_PLUGIN_DIR = "pluginDir";

	@Reference
	private Logger logger;

	@Reference
	private PluginLoader pluginLoader;

	@Reference
	private Persistence persistence;
	@Reference
	private NLProcessingManager nlProcessingManager;
	@Reference
	private ConfigurationManager configurationManager;
	@Reference
	private Configuration di;
	@Reference
	private Environment environment;

	private Properties config;

	private Path workingDir;

	private boolean loaded = false;

	@PostConstruct
	private void setup() {
		this.workingDir = this.environment.getWorkingDirectory();
		try {
			loadAndCheckConfig();
		} catch (IllegalStateException e) {
			this.logger.error("Error loading config.", e);
		}
	}

	private void loadAndCheckConfig() {
		this.config = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		checkProperty(PROPERTY_MODE);
		String mode = this.config.getProperty(PROPERTY_MODE);
		switch (mode) {
		case PROPERTY_MODE_DEV:
			checkProperty(PROPERTY_PLUGINS);
			checkProperty(PROPERTY_PLUGIN_DIR);
			break;
		case PROPERTY_MODE_DOCKER:
			checkProperty(PROPERTY_PLUGIN_DIR);
			break;
		case PROPERTY_MODE_MANUAL:
			checkProperty(PROPERTY_PLUGINS);
			break;
		default:
			throw new IllegalStateException("Unknown mode: " + mode);
		}
	}

	private void checkProperty(String key) {
		if (this.config.getProperty(key) == null)
			throw new IllegalStateException("The property " + key + " is not set.");
	}

	private List<String> getPluginListFromConfig() {
		String[] plugins = this.config.getProperty(PROPERTY_PLUGINS).split(",");
		List<String> pluginList = new ArrayList<>(Arrays.asList(plugins));
		pluginList.removeIf(s -> s.isEmpty());
		return pluginList;
	}

	private Path getPluginDirFromConfig() throws FileNotFoundException {
		Path pluginDir = this.workingDir.resolve(this.config.getProperty(PROPERTY_PLUGIN_DIR));
		if (!Files.exists(pluginDir))
			throw new FileNotFoundException("Plugin directory does not exist.");
		return pluginDir;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager#loadPlugins()
	 */
	@Override
	public synchronized void loadPlugins() {
		if (this.loaded)
			throw new IllegalStateException("the plugins are loaded");

		this.logger.debug("workingDir: {}", this.workingDir);

		if (!Files.exists(this.workingDir)) {
			this.logger.error("Working directory does not exist.");
			return;
		}

		String mode = this.config.getProperty(PROPERTY_MODE);

		try {
			switch (mode) {
			case PROPERTY_MODE_DEV:
				for (String pluginID : this.getPluginListFromConfig()) {
					this.tryLoadPluginFromTarget(getPluginDirFromConfig(), pluginID);
				}
				break;
			case PROPERTY_MODE_DOCKER:
				try (Stream<Path> childs = Files.list(getPluginDirFromConfig())) {
					childs.forEach(p -> this.pluginLoader.loadPlugin(p));
				} catch (IOException e) {
					this.logger.error("Failed loading plugins", e);
					return;
				}
				break;
			case PROPERTY_MODE_MANUAL:
				for (String pluginPathString : this.getPluginListFromConfig()) {
					Path pluginPath = this.workingDir.resolve(pluginPathString);
					if (!Files.isRegularFile(pluginPath)) {
						this.logger.error("Plugin {} is not a regular file and is therefore not loaded.",
								pluginPathString);
					}
					this.pluginLoader.loadPlugin(pluginPath);
				}
				break;
			default:
				this.logger.error("Unknown plugin mode: {}", mode);
				return;
			}
		} catch (FileNotFoundException e) {
			this.logger.error("File or Directory not found. Is your working directory correct?", e);
			return;
		}

		for (IPlugin p : this.pluginLoader.getPlugins()) {
			this.processPlugin(p);
		}

		this.loaded = true;
	}

	private boolean tryLoadPluginFromTarget(Path pluginDir, String pluginID) {
		this.logger.debug("try load plugin {}", pluginID);
		Path p = pluginDir.resolve(pluginID);

		if (!Files.exists(p)) {
			this.logger.warn("The plugin {} does not exist in the plugin directory.", p);
			return false;
		}
		Path target = p.resolve("target");
		if (!Files.exists(target)) {
			this.logger.warn("Plugin {} has no target directory. Did you run mvn install?", p);
			return false;
		}

		try (Stream<Path> childs = Files.list(target)) {
			Optional<Path> jar = childs.filter(j -> j.getFileName().toString().endsWith("with-dependencies.jar"))
					.findFirst();
			if (!jar.isPresent()) {
				this.logger.warn("The jar with dependencies is missing for plugin {}", pluginID);
				return false;
			}

			return this.pluginLoader.loadPlugin(jar.get());
		} catch (IOException e) {
			this.logger.error("Failed to load plugin", e);
		}
		return false;
	}

	/**
	 * Process the plugin components and register them at the right Services
	 *
	 * @param plugin
	 *            the plugin to process
	 */
	private void processPlugin(IPlugin plugin) {
		for (Class<?> cls : plugin.getClasses()) {
			if (cls.isAnnotationPresent(SpeechCommand.class)) {
				this.nlProcessingManager.register(cls);
			}
			if (cls.isAnnotationPresent(Service.class)) {
				this.di.register(cls);
			}
			if (cls.isAnnotationPresent(Entity.class)) {
				this.persistence.register(cls);
			}
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager#getPlugins()
	 */
	@Override
	public List<IPlugin> getPlugins() {
		return Collections.unmodifiableList(this.pluginLoader.getPlugins());
	}
}
