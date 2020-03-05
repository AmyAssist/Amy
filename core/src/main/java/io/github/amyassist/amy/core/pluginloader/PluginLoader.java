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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.PreDestroy;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * The class responsible for loading plugins.
 * 
 * @author Tim Neumann, Leon Kiefer
 */
@Service
public class PluginLoader {
	@Reference
	private Logger logger;

	private final List<Plugin> plugins = new ArrayList<>();

	/**
	 * Loads the plugin found at the uri
	 * 
	 * @param path
	 *                 The location of the plugin
	 * @return Whether loading was successful
	 * @throws IllegalArgumentException
	 *                                      When the given location is not a valid plugin file
	 */
	public boolean loadPlugin(Path path) {
		this.logger.debug("try to load plugin from {}", path);

		if (!Files.exists(path) || Files.isDirectory(path))
			throw new IllegalArgumentException("Invalid file");

		Plugin plugin;
		try (JarFile jar = new JarFile(path.toFile())) {
			URL[] urls = { path.toUri().toURL() };

			// We need that classLoader to stay open.
			@SuppressWarnings("resource")//NOSONAR
			URLClassLoader childLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());//NOSONAR

			Manifest mf = jar.getManifest();
			if (mf == null) {
				this.logger.error("Can't find manifest for plugin {}", path);
			}

			plugin = new Plugin(path, childLoader, mf);
		} catch (IOException e) {
			this.logger.error("Exception while loading plugin {}", path, e);
			return false;
		}
		this.addPlugin(plugin);
		return true;
	}

	private void addPlugin(Plugin plugin) {
		String name = plugin.getDisplayName();
		if (plugin.getDisplayName().isEmpty()) {
			name = plugin.getUniqueName();
			this.logger.warn("Can't get display name of plugin {}", name);
		}

		if (plugin.getVersion().isEmpty()) {
			this.logger.warn("Can't get version of plugin {}", name);
		}

		this.logger.info("loaded plugin {}", name);
		this.plugins.add(plugin);
	}

	/**
	 * Returns a List of all Plugins.
	 * 
	 * @return the unmodifiable list of plugins.
	 */
	public List<IPlugin> getPlugins() {
		return Collections.unmodifiableList(this.plugins);
	}

	@PreDestroy
	private void close() {
		for (IPlugin p : this.getPlugins()) {
			try {
				((URLClassLoader) p.getClassLoader()).close();
			} catch (IOException e) {
				this.logger.error("Can not close URLClassLoader of plugin " + p.getUniqueName(), e);
			}
		}
	}
}
