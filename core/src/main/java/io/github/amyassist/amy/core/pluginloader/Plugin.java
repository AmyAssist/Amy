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

import java.nio.file.Path;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of loaded plugin.
 * 
 * @author Tim Neumann
 */
class Plugin implements IPlugin {

	private static final String UNIQUE_NAME_KEY = "PluginID";
	private static final String DESCRIPTION_KEY = "PluginDescription";

	private final Logger logger = LoggerFactory.getLogger(Plugin.class);

	/**
	 * The path to the jar of the plugin.
	 */
	private final Path path;

	/**
	 * The class loader that loads this jar.
	 */
	private final ClassLoader classLoader;

	/**
	 * The manifest file of the loaded jar.
	 */
	private final Manifest manifest;

	public Plugin(Path path, ClassLoader classLoader, Manifest manifest) {
		this.path = path;
		this.classLoader = classLoader;
		this.manifest = manifest;
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.IPlugin#getPath()
	 */
	@Override
	public Path getPath() {
		return this.path;
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.IPlugin#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.IPlugin#getUniqueName()
	 */
	@Override
	public String getUniqueName() {
		if (this.manifest == null) {
			this.logger.error("Plugin manifest is null. Falling back to file name for unique name!: {}", this.path);
			return this.path.toString();
		}
		return this.manifest.getMainAttributes().getValue(UNIQUE_NAME_KEY);
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.IPlugin#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_TITLE);
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.IPlugin#getVersion()
	 */
	@Override
	public String getVersion() {
		if (this.manifest == null) {
			this.logger.error("Plugin manifest is null, using empty String for the version");
			return ("");
		}
		return this.manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_VERSION);
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.IPlugin#getDescription()
	 */
	@Override
	public String getDescription() {
		if (this.manifest == null) {
			this.logger.warn("Plugin manifest is null, using empty String for the description");
			return ("");
		}
		return this.manifest.getMainAttributes().getValue(DESCRIPTION_KEY);
	}

	/**
	 * @see io.github.amyassist.amy.core.pluginloader.IPlugin#getManifest()
	 */
	@Override
	public Manifest getManifest() {
		return this.manifest;
	}

}
