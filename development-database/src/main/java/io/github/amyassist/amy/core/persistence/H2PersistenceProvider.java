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

package io.github.amyassist.amy.core.persistence;

import java.util.Properties;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.io.Environment;
import io.github.amyassist.amy.core.persistence.provider.PersistenceProvider;

/**
 * Persistence Provider implementation for local H2 database. This Provider generates the Properties to create and
 * connect to a local embedden h2 database. The location of the database file can be changed with the
 * <i>persistence.properties</i>.
 * 
 * @author Leon Kiefer
 */
@Service
public class H2PersistenceProvider implements PersistenceProvider {
	private static final String PERSISTENCE_CONFIG = "persistence";
	private static final String PERSISTENCE_DATA_PROPERTY = "dataDir";
	private static final String JAVAX_PERSISTENCE_CONFIG = "javax.persistence";

	@Reference
	private ConfigurationManager configurationManager;

	@Reference
	private Environment environment;

	private Properties javaxProperties;
	private String dataDir;

	@PostConstruct
	private void init() {
		this.javaxProperties = this.configurationManager.getConfigurationWithDefaults(JAVAX_PERSISTENCE_CONFIG);
		Properties config = this.configurationManager.getConfigurationWithDefaults(PERSISTENCE_CONFIG);
		this.dataDir = config.getProperty(PERSISTENCE_DATA_PROPERTY);
	}

	@Override
	public Properties getProperties(String name) {
		String string = this.environment.getWorkingDirectory().resolve(this.dataDir).resolve(name).toAbsolutePath()
				.toString();
		Properties properties = new Properties(this.javaxProperties);
		properties.put("javax.persistence.jdbc.url", "jdbc:h2:" + string);
		return properties;
	}
}
