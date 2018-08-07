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

package de.unistuttgart.iaas.amyassist.amy.core.configuration;

import java.util.Properties;

/**
 * High level abstraction over different ConfigurationLoaders.
 * 
 * @author Leon Kiefer
 */
public interface ConfigurationManager {
	/**
	 * Get the configuration from the config dirs and don't use defaults.
	 * 
	 * @param configurationName
	 *            the name of the configuration without the file ending .properties
	 * @return the properties
	 */
	Properties getConfiguration(String configurationName);

	/**
	 * Get the configuration from the config dirs but fallback to the defaults.
	 * 
	 * @param configurationName
	 *            the name of the configuration without the file ending .properties
	 * @return the configuration with the defaults from the default configuration
	 * @throws ConfigurationNotFoundException
	 *             if the default configuration cloud not be found
	 */
	Properties getConfigurationWithDefaults(String configurationName);

}