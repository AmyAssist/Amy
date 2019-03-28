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
import io.github.amyassist.amy.core.persistence.provider.PersistenceProvider;

/**
 * Persistence Provider implementation for JDBC drivers. This Provider use the configuration form the
 * <i>javax.persistence.properties</i>. The property <i>javax.persistence.jdbc.url</i> should contain
 * <i>{databasename}</i> as placeholder for the database name.
 * 
 * @author Leon Kiefer
 */
@Service
public class GenericJDBCPersistenceProvider implements PersistenceProvider {
	private static final String JAVAX_PERSISTENCE_CONFIG = "javax.persistence";
	private static final String JAVAX_PERSISTENCE_URL = "javax.persistence.jdbc.url";

	@Reference
	private ConfigurationManager configurationManager;

	private Properties javaxProperties;

	@PostConstruct
	private void init() {
		this.javaxProperties = this.configurationManager.getConfigurationWithDefaults(JAVAX_PERSISTENCE_CONFIG);
	}

	@Override
	public Properties getProperties(String name) {
		Properties properties = new Properties(this.javaxProperties);
		String url = properties.getProperty(JAVAX_PERSISTENCE_URL);
		url = url.replace("{databasename}", name);
		properties.setProperty(JAVAX_PERSISTENCE_URL, url);
		return properties;
	}

}
