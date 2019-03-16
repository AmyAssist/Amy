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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.persistence.spi.PersistenceUnitInfo;

import com.google.common.collect.Lists;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.io.Environment;

/**
 * The Persistence Service implementation
 * 
 * @author Leon Kiefer
 */
@Service
public class PersistenceService implements Persistence {
	private static final String PERSISTENCE_CONFIG = "persistence";
	private static final String PERSISTENCE_DATA_PROPERTY = "dataDir";
	private static final String JAVAX_PERSISTENCE_CONFIG = "javax.persistence";

	private Map<String, List<Class<?>>> persistenceUnits = new HashMap<>();

	@Reference
	private Environment environment;

	@Reference
	private ConfigurationManager configurationManager;

	private Properties hibernateFix;
	private PersistenceProvider persistenceProvider;
	private String dataDir;

	@PostConstruct
	private void init() {
		Properties javaxProperties = this.configurationManager.getConfigurationWithDefaults(JAVAX_PERSISTENCE_CONFIG);

		this.hibernateFix = new Properties();
		for (String propertyName : javaxProperties.stringPropertyNames()) {
			this.hibernateFix.setProperty(propertyName, javaxProperties.getProperty(propertyName));
		}

		Properties config = this.configurationManager.getConfigurationWithDefaults(PERSISTENCE_CONFIG);
		this.dataDir = config.getProperty(PERSISTENCE_DATA_PROPERTY);

		this.persistenceProvider = PersistenceProviderResolverHolder.getPersistenceProviderResolver()
				.getPersistenceProviders().get(0);
	}

	@Override
	public @Nonnull EntityManager getEntityManager(@Nonnull String name) {
		if (name.isEmpty()) {
			throw new IllegalArgumentException("The persistence unit name should not be empty");
		}
		if (!this.persistenceUnits.containsKey(name)) {
			throw new NoSuchElementException();
		}
		List<Class<?>> entities = this.persistenceUnits.get(name);
		List<String> entitiesNames = Lists.transform(entities, Class::getName);

		PersistenceUnitInfo persistenceUnitInfo = new PersistenceUnitInfoImpl(name, entitiesNames,
				entities.get(0).getClassLoader(), this.hibernateFix);

		Map<String, String> properties = new HashMap<>();
		String string = this.environment.getWorkingDirectory().resolve(this.dataDir).resolve(name).toAbsolutePath()
				.toString();
		properties.put("javax.persistence.jdbc.url", "jdbc:h2:" + string);

		EntityManagerFactory entityManagerFactory = this.persistenceProvider
				.createContainerEntityManagerFactory(persistenceUnitInfo, properties);
		return entityManagerFactory.createEntityManager();
	}

	@Override
	public void register(@Nonnull Class<?> entity) {
		if (!entity.isAnnotationPresent(Entity.class)) {
			throw new IllegalArgumentException("@Entity annotation missing!");
		}
		if (!entity.isAnnotationPresent(PersistenceUnit.class)) {
			throw new IllegalArgumentException("Persistence Unit must be specified");
		}

		PersistenceUnit annotation = entity.getAnnotation(PersistenceUnit.class);
		String unitName = annotation.unitName();

		List<Class<?>> set;
		if (this.persistenceUnits.containsKey(unitName)) {
			set = this.persistenceUnits.get(unitName);
			ClassLoader persistenceUnitClassLoader = set.get(0).getClassLoader();
			if (!persistenceUnitClassLoader.equals(entity.getClassLoader())) {
				throw new IllegalArgumentException(
						"The classLoader of Entities in a Persistence Unit must be the same");
			}
		} else {
			set = new ArrayList<>();
			this.persistenceUnits.put(unitName, set);
		}

		set.add(entity);
	}
}