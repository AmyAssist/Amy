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

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.io.Environment;

/**
 * Implementation of {@link EnvironmentConfigurationLoader}
 * 
 * @author Tim Neumann
 */
@Service
public class EnvironmentConfigurationLoaderImpl implements EnvironmentConfigurationLoader {

	@Reference
	private Environment env;

	private Map<String, String> envVars;

	private Pattern allowedEnvVars;

	@PostConstruct
	private void init() {
		this.envVars = this.env.getEnvironmentVariables();
		this.allowedEnvVars = Pattern.compile(ALLOWED_ENV_VARS_PATTERN);
	}

	@Override
	public @Nonnull Properties load(String configurationName, @Nonnull Properties original) {
		if (!isAllowedName(configurationName))
			throw new IllegalArgumentException("Illegal configuration name for loading from the environment.");
		Properties ret = new Properties(original);
		String configNameForEnv = "AMY_" + configurationName.replace('.', '_');

		for (String envVarName : this.envVars.keySet()) {
			if (envVarName.toLowerCase().startsWith(configNameForEnv.toLowerCase())) {
				String key = envVarName.substring(configNameForEnv.length() + 1);
				overwriteProperties(ret, key, this.envVars.get(envVarName));
			}
		}

		return ret;
	}

	@Override
	public boolean isAllowedName(String name) {
		String configNameForEnv = name.replace('.', '_');
		return this.allowedEnvVars.matcher(configNameForEnv).matches();
	}

	/**
	 * Overwrites any properties in the given properties object, that case insensitively match the key with any '.'
	 * replaced by '_'
	 * 
	 * @param p
	 *            The properties object.
	 * @param key
	 *            The key to search
	 * @param value
	 *            The value to write
	 */
	private void overwriteProperties(Properties p, String key, String value) {
		Enumeration<?> names = p.propertyNames();

		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();

			if (name.replace('.', '_').equalsIgnoreCase(key)) {
				p.setProperty(name, value);
			}
		}
	}

}
