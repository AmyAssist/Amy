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
import java.nio.file.Path;
import java.util.List;
import java.util.jar.Manifest;

/**
 * A representation of a plugin
 * 
 * @author Tim Neumann
 */
public interface IPlugin {

	/**
	 * Get's the file of this plugin. Same location as getPath() but as file.
	 * 
	 * @return file
	 * @deprecated In favor of {@link #getPath()}.
	 */
	@Deprecated
	File getFile();

	/**
	 * Get's the path to the jar of this plugin. Same location as getFile() but as a path.
	 * 
	 * @return the path to the jar.
	 */
	Path getPath();

	/**
	 * Get's the classloader which was used to load this plugin
	 * 
	 * @return classLoader
	 */
	ClassLoader getClassLoader();

	/**
	 * Get's the unique name of the plugin
	 * 
	 * @return uniqueName
	 */
	String getUniqueName();

	/**
	 * Get's the dispaly name of the plugin
	 * 
	 * @return displayName
	 */
	String getDisplayName();

	/**
	 * Get's the version of this plugin
	 * 
	 * @return mavenVersion
	 */
	String getVersion();

	/**
	 * Get's the description of this plugin
	 * 
	 * @return the description
	 */
	String getDescription();

	/**
	 * Get's a list of all classes of this plugin
	 * 
	 * @return classes
	 * @deprecated Because it will not be possible to get this list when we start to use multiple deployment
	 *             descriptors.
	 */
	@Deprecated
	List<Class<?>> getClasses();

	/**
	 * Get's the manifest of this plugin
	 * 
	 * @return manifest
	 */
	Manifest getManifest();

}
