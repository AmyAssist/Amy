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

package de.unistuttgart.iaas.amyassist.amy.core.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.jpa.HibernatePersistenceProvider;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationLoader;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * TODO: Description
 * 
 * @author Leon Kiefer
 */
@Service
public class PersistenceService implements Persistence {
	private static final String PERSISTENCE_CONFIG = "persistence";

	@Reference
	private ConfigurationLoader configurationLoader;

	private EntityManager entityManager;

	@PostConstruct
	private void init() {
		Properties properties = this.configurationLoader.load(PERSISTENCE_CONFIG);

		List<String> classes = new ArrayList<>();
		classes.add(SimpleData.class.getName());
		PersistenceUnitInfo persistenceUnitInfo = new PersistenceUnitInfoImpl("test", classes, properties);
		PersistenceProvider persistenceProvider = new HibernatePersistenceProvider();
		EntityManagerFactory entityManagerFactory = persistenceProvider
				.createContainerEntityManagerFactory(persistenceUnitInfo, null);
		this.entityManager = entityManagerFactory.createEntityManager();
	}

	@Override
	public EntityManager getEntityManager(String name) {
		return this.entityManager;
	}
}
