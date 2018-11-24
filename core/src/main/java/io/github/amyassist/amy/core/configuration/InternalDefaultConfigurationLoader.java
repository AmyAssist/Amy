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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * This implementation used the ClassLoader and that must be passed as argument.
 * 
 * @author Leon Kiefer
 */
@Service
public class InternalDefaultConfigurationLoader {

	/**
	 * @param loader
	 *            the ClassLoader used to load the configuration from
	 * @param configurationName
	 *            the name of the configuration in the META-INF directory
	 * @return the loaded configuration
	 * @throws ConfigurationNotFoundException
	 *             if the configuration can't be loaded with the given ClassLoader.
	 */
	public @Nonnull Properties load(ClassLoader loader, String configurationName) {
		try (InputStream resourceAsStream = loader
				.getResourceAsStream("META-INF/" + configurationName + ".properties")) {
			if (resourceAsStream == null) {
				throw new ConfigurationNotFoundException("the configuration " + configurationName
						+ " could not be found with the ClassLoader " + loader);
			}
			Properties properties = new Properties();
			properties.load(resourceAsStream);
			return properties;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
