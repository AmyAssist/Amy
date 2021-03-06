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

package io.github.amyassist.amy.core.configuration;

import java.util.Properties;

import javax.annotation.Nonnull;

/**
 * The interface of a configuration loader
 * 
 * @author Tim Neumann
 */
public interface ConfigurationLoader {

	/**
	 * 
	 * @param configurationName
	 *            the name of the config file, without the .properties
	 * @return the loaded Properties or empty Properties if no such file was found
	 */
	@Nonnull
	Properties load(String configurationName);

	/**
	 * Load the Properties with default Properties given.
	 * 
	 * @param configurationName
	 *            the name of the config file, without the .properties
	 * @param defaults
	 *            the defaults used when no properties are given in the properties files
	 * @return the loaded properties with the given defaults or the default properties if no properties file exists
	 */
	@Nonnull
	Properties load(String configurationName, @Nonnull Properties defaults);

	/**
	 * 
	 * @param configurationName
	 *            the name of the config file, without the .properties
	 * @param properties
	 *            the Properties to be saved
	 */
	void store(String configurationName, @Nonnull Properties properties);

}
