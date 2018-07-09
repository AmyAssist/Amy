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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.Entity;
import javax.ws.rs.Path;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.Persistence;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;

/**
 * Manages the plugin integration
 * 
 * @author Leon Kiefer
 */
@Service
public class PluginManagerService implements PluginManager {
	private static final String CONFIG_NAME = "plugin.config";
	private static final String CONFIG_PLUGINS = "plugins";
	private static final String PROPERTY_PLUGINS_DIR = "pluginDir";

	@Reference
	private Logger logger;

	@Reference
	private PluginLoader pluginLoader;

	@Reference
	private Server server;
	@Reference
	private Persistence persistence;
	@Reference
	private NLProcessingManager nlProcessingManager;
	@Reference
	private ConfigurationLoader configurationLoader;
	@Reference
	private DependencyInjection di;

	private Properties config;

	@PostConstruct
	private void setup() {
		this.config = this.configurationLoader.load(CONFIG_NAME);
	}

	/**
	 * The project directory.
	 */
	public static final File projectDir = new File(".").getAbsoluteFile().getParentFile();

	private List<String> getPluginListFromConfig() {
		String[] plugins = this.config.getProperty(CONFIG_PLUGINS).split(",");
		return Arrays.asList(plugins);
	}

	private boolean loaded = false;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager#loadPlugins()
	 */
	@Override
	public synchronized void loadPlugins() {
		if (this.loaded) {
			throw new IllegalStateException("the plugins are loaded");
		}

		this.logger.debug("projectDir: {}", projectDir);

		if (!projectDir.exists()) {
			this.logger.error("Project directory does not exist.");
			return;
		}

		File pluginDir = new File(projectDir, this.config.getProperty(PROPERTY_PLUGINS_DIR));

		if (!pluginDir.exists()) {
			this.logger
					.error("Plugin directory does not exist. Is the project path correct for your working directory?");
			return;
		}

		for (String pluginID : this.getPluginListFromConfig()) {
			if (!pluginID.isEmpty()) {
				this.tryLoadPlugin(pluginDir, pluginID);
			}
		}

		for (IPlugin p : this.pluginLoader.getPlugins()) {
			this.processPlugin(p);
		}

		this.loaded = true;
	}

	private boolean tryLoadPlugin(File pluginDir, String pluginID) {
		this.logger.debug("try load plugin {}", pluginID);
		File p = new File(pluginDir, pluginID);

		if (!p.exists()) {
			this.logger.warn("The plugin {} does not exist in the plugin directory.", p.getName());
			return false;
		}
		File target = new File(p, "target");
		if (!target.exists()) {
			this.logger.warn("Plugin {} has no target directory. Did you run mvn install?", p.getName());
			return false;
		}

		for (File child : target.listFiles()) {
			if (child.getName().endsWith("with-dependencies.jar")) {
				return this.pluginLoader.loadPlugin(child.toURI());
			}
		}
		this.logger.warn("The jar with dependencies is missing for plugin {}", pluginID);
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
			if (cls.isAnnotationPresent(Path.class)) {
				this.server.register(cls);
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
