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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The class responsible for loading plugins.
 * 
 * @author Tim Neumann
 */
public class PluginLoader {

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
	public boolean loadPlugin(URI uri) throws IllegalArgumentException {
		File file = new File(uri);

		if (!file.exists() || file.isDirectory())
			throw new IllegalArgumentException("Invalid file");

		Plugin plugin = new Plugin();
		plugin.setFile(file);

		try (JarFile jar = new JarFile(file)) {
			Enumeration<JarEntry> jarEntries = jar.entries();
			URL[] urls = { file.toURI().toURL() };

			URLClassLoader childLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

			HashMap<String, PomParser> foundPoms = new HashMap<>();

			ArrayList<Class<?>> classes = new ArrayList<>();

			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if (jarEntry.getName().endsWith("pom.xml")) {
					String path = "jar:" + uri.toString() + "!/" + jarEntry.getName();
					URI pomUri = new URI(path);
					PomParser pomParser = new PomParser(pomUri.toURL().openStream());
					foundPoms.put(pomParser.getFullId(), pomParser);
				} else if (jarEntry.getName().endsWith(".class")) {
					String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
					className = className.replace("/", ".");
					Class<?> c = childLoader.loadClass(className);
					classes.add(c);
				}
			}

			childLoader.close();

			ArrayList<String> foundToBeLibaries = new ArrayList<>();

			for (PomParser pom : foundPoms.values()) {
				foundToBeLibaries.addAll(Arrays.asList(pom.getDependencyIds()));
			}

			for (String libary : foundToBeLibaries) {
				if (foundPoms.containsKey(libary)) {
					foundPoms.remove(libary);
				}
			}

			if (foundPoms.size() > 1)
				throw new IllegalStateException("More then one non-libary artifact found in jar!");
			if (foundPoms.size() < 1)
				throw new IllegalStateException("No artifact found in jar!");

			PomParser pom = foundPoms.values().iterator().next();

			plugin.setClassLoader(childLoader);
			plugin.setMavenArtifactId(pom.getId());
			plugin.setMavenGroupId(pom.getGroupId());
			plugin.setMavenVersion(pom.getVersion());
			plugin.setClasses(classes);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		this.plugins.put(plugin.getUniqueName(), plugin);
		return true;
	}

	/**
	 * Loads a plugin, that is in the classpath of the project by packageName
	 * 
	 * @param packageName
	 *            The package name of the plugin to load
	 * @param mavenArtifactId
	 *            The maven artifactId of the plugin
	 * @param mavenGroupId
	 *            The maven groupId of the plugin
	 * @param mavenVersion
	 *            The maven version of the plugin
	 * @return Whether it worked.
	 * 
	 */
	public boolean loadPlugin(String packageName, String mavenArtifactId, String mavenGroupId, String mavenVersion) {
		String packagePath = packageName.replace(".", "/");
		URL packageURL = Thread.currentThread().getContextClassLoader().getResource(packagePath);
		if (packageURL == null)
			return false;
		File packageFile;
		try {
			packageFile = new File(URLDecoder.decode(packageURL.getFile(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
		p.setMavenArtifactId(mavenArtifactId);
		p.setMavenGroupId(mavenGroupId);
		p.setMavenVersion(mavenVersion);
		this.plugins.put(p.getUniqueName(), p);
		return true;
	}

	private ArrayList<Class<?>> findClassesInPackage(File packageFile, String parent_packageName) {
		ArrayList<Class<?>> classes = new ArrayList<>();
		if (packageFile.isDirectory()) {
			for (File child : packageFile.listFiles()) {
				ArrayList<Class<?>> newClasses = this.findClassesInPackage(child,
						parent_packageName + "." + packageFile.getName());
				if (newClasses == null)
					return null;
				classes.addAll(newClasses);
			}
		} else if (packageFile.getName().endsWith(".class")) {
			String className = parent_packageName + "."
					+ packageFile.getName().substring(0, packageFile.getName().length() - 6);
			try {
				classes.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
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
	public Plugin getPlugin(String name) {
		return this.plugins.get(name);
	}

	/**
	 * @return a list of plugin names
	 */
	public Set<String> getPluginNames() {
		return this.plugins.keySet();
	}

	public List<Plugin> getPlugins() {
		return new ArrayList<>(this.plugins.values());
	}
}
