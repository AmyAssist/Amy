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

package io.github.amyassist.amy.core.pluginloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.Configuration;
import io.github.amyassist.amy.core.di.Services;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.persistence.Persistence;

/**
 * Manages the plugin integration.
 * 
 * For an explanation of the config see <a href=https://github.com/AmyAssist/Amy/wiki/PluginManager#config>
 * https://github.com/AmyAssist/Amy/wiki/PluginManager#config</a>.
 * 
 * @author Leon Kiefer, Tim Neumann
 */
@Service
public class PluginManagerService implements PluginManager {
	private static final String CONFIG_NAME = "plugin.config";
	private static final String PROPERTY_PLUGINS = "plugins";
	private static final String PROPERTY_PLUGIN_DIR = "pluginDir";

	@Reference
	private Logger logger;

	@Reference
	private PluginLoader pluginLoader;

	@Reference
	private Persistence persistence;
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
		loadAndCheckConfig();
	}

	private void loadAndCheckConfig() {
		this.config = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		checkProperty(PROPERTY_PLUGINS);
		checkProperty(PROPERTY_PLUGIN_DIR);
	}

	private void checkProperty(String key) {
		if (this.config.getProperty(key) == null)
			throw new IllegalStateException("The property " + key + " is not set.");
	}

	private List<String> getPluginListFromConfig() {
		String[] plugins = this.config.getProperty(PROPERTY_PLUGINS).split(",");
		List<String> pluginList = new ArrayList<>(Arrays.asList(plugins));
		pluginList.removeIf(String::isEmpty);
		return pluginList;
	}

	private Path getPluginDirFromConfig() throws FileNotFoundException {
		Path pluginDir = this.workingDir.resolve(this.config.getProperty(PROPERTY_PLUGIN_DIR));
		if (!Files.exists(pluginDir))
			throw new FileNotFoundException("Plugin directory does not exist.");
		return pluginDir;
	}

	private boolean isConfiguredToLoadAll() {
		List<String> pluginList = getPluginListFromConfig();
		return (pluginList.size() == 1 && pluginList.get(0).equals("all"));
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.PluginManager#loadPlugins()
	 */
	@Override
	public synchronized void loadPlugins() throws IOException {
		if (this.loaded)
			throw new IllegalStateException("the plugins are loaded");

		this.logger.debug("workingDir: {}", this.workingDir);

		if (!Files.exists(this.workingDir))
			throw new FileNotFoundException("Working directory does not exist.");

		Path pluginDir = getPluginDirFromConfig();

		if (isConfiguredToLoadAll()) {
			tryLoadAllPluginsFromDir(pluginDir);
		} else {
			for (String plugin : this.getPluginListFromConfig()) {
				Path pluginJar = pluginDir.resolve(plugin + ".jar");
				if (!Files.isRegularFile(pluginJar)) {
					this.logger.warn("The Plugin {} is missing its jar file {} and is therefore not loaded.", plugin,
							pluginJar);
				}
				this.pluginLoader.loadPlugin(pluginJar);
			}
		}

		for (IPlugin p : this.pluginLoader.getPlugins()) {
			this.processPlugin(p);
		}

		this.loaded = true;
	}

	private void tryLoadAllPluginsFromDir(Path dir) throws IOException {
		try (Stream<Path> childs = Files.list(dir)) {
			childs.filter(p -> p.getFileName().toString().endsWith(".jar"))
					.forEach(p -> this.pluginLoader.loadPlugin(p));
		}
	}

	/**
	 * Process the plugin components and register them at the right Services
	 *
	 * @param plugin
	 *            the plugin to process
	 */
	private void processPlugin(IPlugin plugin) {
		ClassLoader classLoader = plugin.getClassLoader();
		Set<Class<?>> loadServices = new Services().loadServices(classLoader);
		loadServices.removeIf(cls -> cls.getClassLoader() != classLoader);
		loadServices.forEach(this.di::register);
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.PluginManager#getPlugins()
	 */
	@Override
	public List<IPlugin> getPlugins() {
		return Collections.unmodifiableList(this.pluginLoader.getPlugins());
	}
}
