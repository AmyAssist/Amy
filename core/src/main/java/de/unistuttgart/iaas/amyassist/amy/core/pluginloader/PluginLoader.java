/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;

/**
 * The class responsible for loading plugins.
 * 
 * @author Tim Neumann
 */
public class PluginLoader {

	private final Logger logger = LoggerFactory.getLogger(PluginLoader.class);

	private HashMap<String, Plugin> plugins = new HashMap<>();

	/**
	 * Loads the plugin found at the uri
	 * 
	 * @param uri
	 *            The location of the plugin
	 * @return Whether loading was successful
	 * @throws IllegalArgumentException
	 *             When the given location is not a valid plugin file
	 */
	public boolean loadPlugin(URI uri) {
		this.logger.debug("try to load plugin from {}", uri);
		File file = new File(uri);

		if (!file.exists() || file.isDirectory())
			throw new IllegalArgumentException("Invalid file");

		Plugin plugin = new Plugin();
		plugin.setFile(file);

		try (JarFile jar = new JarFile(file)) {
			Enumeration<JarEntry> jarEntries = jar.entries();
			URL[] urls = { file.toURI().toURL() };

			// We need that classLoader to stay open. TODO:Make sure it get's
			// closed eventually.
			@SuppressWarnings("resource")
			URLClassLoader childLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

			Manifest mf = jar.getManifest();
			if (mf == null) {
				this.logger.error("Can't find manifest for plugin {}", uri);
			}

			ArrayList<Class<?>> classes = new ArrayList<>();

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
				}
			}
			// Don't close the loader, so the references of the loaded classes
			// can find there references
			// childLoader.close();

			plugin.setClassLoader(childLoader);
			plugin.setManifest(mf);
			plugin.setClasses(classes);

		} catch (Exception e) {
			this.logger.error("Exception while loading plugin {}", uri, e);
			return false;
		}
		this.addPlugin(plugin);
		return true;
	}

	/**
	 * Loads a plugin, that is in the classpath of the project by packageName
	 * 
	 * @param packageName
	 *            The package name of the plugin to load
	 * @param name
	 *            The name of the plugin
	 * @param version
	 *            The version of the plugin
	 * 
	 * @return Whether it worked.
	 * 
	 */
	@Deprecated
	public boolean loadPlugin(String packageName, String name, String version) {
		String packagePath = packageName.replace(".", "/");
		URL packageURL = Thread.currentThread().getContextClassLoader().getResource(packagePath);
		if (packageURL == null)
			return false;
		File packageFile;
		try {
			packageFile = new File(URLDecoder.decode(packageURL.getFile(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			this.logger.error("converting URL to File", e);
			return false;
		}
		ArrayList<Class<?>> classes = this.findClassesInPackage(packageFile,
				packageName.substring(0, packageName.lastIndexOf(".")));
		if (classes == null)
			return false;
		Plugin p = new Plugin();
		p.setClasses(classes);
		p.setClassLoader(Thread.currentThread().getContextClassLoader());
		p.setFile(null);
		p.setFakeName(name);
		p.setFakeVersion(version);
		this.addPlugin(p);
		return true;
	}

	private void addPlugin(Plugin plugin) {
		if (plugin.getClasses().isEmpty()) {
			this.logger.warn("Plugin contains no class: {}", plugin.getUniqueName());
		}
		if (plugin.getUniqueName().equals("")) {
			this.logger.warn("Can't get name of plugin {}", plugin.getUniqueName());
		}
		if (plugin.getVersion().equals("")) {
			this.logger.warn("Can't get version of plugin {}", plugin.getUniqueName());
		}

		this.logger.info("loaded plugin {} with {} classes", plugin.getUniqueName(), plugin.getClasses().size());
		this.plugins.put(plugin.getUniqueName(), plugin);
	}

	@Deprecated
	private ArrayList<Class<?>> findClassesInPackage(File packageFile, String parentPackageName) {
		ArrayList<Class<?>> classes = new ArrayList<>();
		if (packageFile.isDirectory()) {
			for (File child : packageFile.listFiles()) {
				ArrayList<Class<?>> newClasses = this.findClassesInPackage(child,
						parentPackageName + "." + packageFile.getName());
				if (newClasses == null)
					return null;
				classes.addAll(newClasses);
			}
		} else if (packageFile.getName().endsWith(".class")) {
			String className = parentPackageName + "." + StringUtils.removeEnd(packageFile.getName(), ".class");
			try {
				classes.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				this.logger.error("try to get class {}", className, e);
				return null;
			}
		}
		return classes;
	}

	/**
	 * Get a plugin
	 * 
	 * @param name
	 *            The name of the plugin to get
	 * @return The plugin with the given name or null, if no Plugin with this
	 *         name is loaded.
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
	public List<Plugin> getPlugins() {
		return new ArrayList<>(this.plugins.values());
	}
}
