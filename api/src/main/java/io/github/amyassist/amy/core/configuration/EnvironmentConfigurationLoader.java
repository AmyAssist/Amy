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
 * A configuration loader that will load configurations from the environment variables. Changes can't be stored with
 * this ConfigurationLoader.
 * <p>
 * The name schema for the environment variables is {@code AMY_<config name>_<property key>}.
 * <p>
 * All '.'s in the config name are replaced with '_'. The matching is done case insensitive.
 * <p>
 * A environment variable must match {@value #ALLOWED_ENV_VARS_PATTERN}, so {@code <config name>_<property key>} must as
 * well for a configuration to be read from the environment.
 * 
 * @author Tim Neumann
 */
public interface EnvironmentConfigurationLoader {
	/** The patter for allowed environment variables. */
	public static final String ALLOWED_ENV_VARS_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*";

	/**
	 * Load all properties set in the given original from the environment variables.
	 * <p>
	 * This method will only load these properties that are also set in the original or its defaults recursivly.
	 * 
	 * @param configurationName
	 *            the name of the config.
	 * @param original
	 *            A property file containing settings.
	 * @throws IllegalArgumentException
	 *             When the configuration name is not a legal environment variable name. See
	 *             {@link EnvironmentConfigurationLoader}.
	 * @return A new Properties object with the original Properties set as the defaults and the properties from the
	 *         environment.
	 */
	@Nonnull
	Properties load(String configurationName, @Nonnull Properties original);

	/**
	 * Checks whether the given string is an allowed name for configurations to load from the environment.
	 * 
	 * @param name
	 *            The name to check.
	 * @return Whether the name is allowed.
	 */
	boolean isAllowedName(String name);
}
