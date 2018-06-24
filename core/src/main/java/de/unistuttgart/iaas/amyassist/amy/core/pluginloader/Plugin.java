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
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;

/**
 * A representation of loaded plugin.
 * 
 * @author Tim Neumann
 */
public class Plugin implements IPlugin {

	private static final String uniqueNameKey = "PluginID";

	private final Logger logger = LoggerFactory.getLogger(Plugin.class);

	/**
	 * The file of the jar of the plugin.
	 */
	private File file;

	/**
	 * The class loader that loads this jar.
	 */
	private ClassLoader classLoader;

	/**
	 * The manifest file of the loaded jar.
	 */
	private Manifest manifest;

	/**
	 * The list of all classes of this plugin
	 */
	private List<Class<?>> classes = new ArrayList<>();

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getFile()
	 */
	@Override
	public File getFile() {
		return this.file;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getUniqueName()
	 */
	@Override
	public String getUniqueName() {
		if (this.manifest == null) {
			this.logger.error("Plugin manifest is null. Falling back to file name for unique name!: {}",
					this.file.getName());
			return this.file.getName();
		}
		return this.manifest.getMainAttributes().getValue(uniqueNameKey);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_TITLE);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getVersion()
	 */
	@Override
	public String getVersion() {
		if (this.manifest == null) {
			this.logger.error("Plugin manifest is null, returning \"NaN\" for the version");
			return ("NaN");
		}
		return this.manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_VERSION);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getClasses()
	 */
	@Override
	public List<Class<?>> getClasses() {
		return new ArrayList<>(this.classes);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.IPlugin#getManifest()
	 */
	@Override
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

}
