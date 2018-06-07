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
import java.util.ArrayList;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

/**
 * A representation of loaded plugin
 * 
 * @author Tim Neumann
 */
public class Plugin {
	/**
	 * The file of the jar of the plugin.
	 */
	private File file;

	/**
	 * The class loader that loads this jar.
	 */
	private ClassLoader classLoader;

	private Manifest manifest;

	private ArrayList<Class<?>> classes = new ArrayList<>();

	private String fakeName = "";
	private String fakeVersion = "";

	/**
	 * Get's {@link #file file}
	 * 
	 * @return file
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Get's {@link #classLoader classLoader}
	 * 
	 * @return classLoader
	 */
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	/**
	 * Get's the unique name of the plugin
	 * 
	 * @return uniqueName
	 */
	public String getUniqueName() {
		if (this.manifest == null)
			return this.fakeName;
		return this.manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_TITLE);
	}

	/**
	 * Get's the version of this plugin
	 * 
	 * @return mavenVersion
	 */
	public String getVersion() {
		if (this.manifest == null)
			return this.fakeVersion;
		return this.manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_VERSION);
	}

	/**
	 * Get's {@link #classes classes}
	 * 
	 * @return classes
	 */
	public ArrayList<Class<?>> getClasses() {
		return new ArrayList<>(this.classes);
	}

	/**
	 * Get's {@link #manifest manifest}
	 * 
	 * @return manifest
	 */
	public Manifest getManifest() {
		return this.manifest;
	}

	/**
	 * Set's {@link #file file}
	 * 
	 * @param file
	 *            file
	 */
	protected void setFile(File file) {
		this.file = file;
	}

	/**
	 * Set's {@link #classLoader classLoader}
	 * 
	 * @param classLoader
	 *            classLoader
	 */
	protected void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Set's {@link #classes classes}
	 * 
	 * @param classes
	 *            classes
	 */
	protected void setClasses(ArrayList<Class<?>> classes) {
		this.classes = classes;
	}

	/**
	 * Set's {@link #manifest manifest}
	 * 
	 * @param manifest
	 *            manifest
	 */
	protected void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}

	/**
	 * Set's {@link #fakeName fakeName}
	 * 
	 * @param fakeName
	 *            fakeName
	 */
	protected void setFakeName(String fakeName) {
		this.fakeName = fakeName;
	}

	/**
	 * Set's {@link #fakeVersion fakeVersion}
	 * 
	 * @param fakeVersion
	 *            fakeVersion
	 */
	protected void setFakeVersion(String fakeVersion) {
		this.fakeVersion = fakeVersion;
	}

}
