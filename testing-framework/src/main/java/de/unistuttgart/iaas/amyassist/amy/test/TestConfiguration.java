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

package de.unistuttgart.iaas.amyassist.amy.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Simple in-memory ConfigurationLoader implementation for tests
 * 
 * @author Leon Kiefer
 */
@Service
public class TestConfiguration implements ConfigurationLoader {

	private Map<String, Properties> map = new HashMap<>();

	@Override
	public Properties load(String configurationName) {
		return this.map.get(configurationName);
	}

	@Override
	public Properties load(String configurationName, Properties defaults) {
		Properties properties = new Properties(defaults);
		properties.putAll(this.load(configurationName));
		return properties;
	}

	@Override
	public void store(String configurationName, Properties properties) {
		this.map.put(configurationName, properties);
	}

}
