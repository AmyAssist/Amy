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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * The class responsible for loading plugins.
 * 
 * @author Tim Neumann
 */
@Service
public class PluginLoader {
	@Reference
	private Logger logger;

	private Map<String, Plugin> plugins = new HashMap<>();

	/**
	 * Loads the plugin found at the uri
	 * 
	 * @param path
	 *            The location of the plugin
	 * @return Whether loading was successful
	 * @throws IllegalArgumentException
	 *             When the given location is not a valid plugin file
	 */
	public boolean loadPlugin(Path path) {
		this.logger.debug("try to load plugin from {}", path);

		if (!Files.exists(path) || Files.isDirectory(path))
			throw new IllegalArgumentException("Invalid file");

		Plugin plugin = new Plugin();
		plugin.setPath(path);

		try (JarFile jar = new JarFile(path.toFile())) {
			Enumeration<JarEntry> jarEntries = jar.entries();
			URL[] urls = { path.toUri().toURL() };

			// We need that classLoader to stay open.
			@SuppressWarnings("resource")
			URLClassLoader childLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
			
			Manifest mf = jar.getManifest();
			if (mf == null) {
				this.logger.error("Can't find manifest for plugin {}", path);
			}

			ArrayList<Class<?>> classes = new ArrayList<>();
			
			List<String> aimContent = new ArrayList<>();

			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if (jarEntry.getName().endsWith(".class")) {
					String className = StringUtils.removeEnd(jarEntry.getName(), ".class");
					className = className.replace("/", ".");
					if (className.contains("amy")) {
						this.logger.debug("load class {}", className);
						Class<?> c = Class.forName(className, true, childLoader);
						classes.add(c);
					}
				}else if (jarEntry.getName().endsWith(".aim.xml")){
					InputStream stream = childLoader.getResourceAsStream(jarEntry.getName());
					
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
						aimContent.add(reader.lines().collect(Collectors.joining()));
						reader.close();
					}
				}
			}

			plugin.setAIMContent(aimContent);
			plugin.setClassLoader(childLoader);
			plugin.setManifest(mf);
			plugin.setClasses(classes);

		} catch (IOException | ClassNotFoundException e) {
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

		if (plugin.getClasses().isEmpty()) {
			this.logger.warn("Plugin contains no class: {}", name);
		}

		if (plugin.getVersion().isEmpty()) {
			this.logger.warn("Can't get version of plugin {}", name);
		}

		this.logger.info("loaded plugin {} with {} classes", name, plugin.getClasses().size());
		this.plugins.put(plugin.getUniqueName(), plugin);
	}

	/**
	 * Get a plugin
	 * 
	 * @param name
	 *            The name of the plugin to get
	 * @return The plugin with the given name or null, if no Plugin with this name is loaded.
	 */
	public IPlugin getPlugin(String name) {
		return this.plugins.get(name);
	}

	/**
	 * @return a list of plugin names
	 */
	public Set<String> getPluginNames() {
		return this.plugins.keySet();
	}

	/**
	 * Returns a List of all Plugins.
	 * 
	 * @return the list of plugins.
	 */
	public List<IPlugin> getPlugins() {
		return new ArrayList<>(this.plugins.values());
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
