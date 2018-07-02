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

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;

/**
 * Implementation of PersistenceUnitInfo to provide deployment information without persistence.xml
 * 
 * @author Leon Kiefer
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
	private final String persistenceUnitName;

	private PersistenceUnitTransactionType transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;

	private final List<String> managedClassNames;

	private final Properties properties;

	private DataSource jtaDataSource = null;

	private DataSource nonJtaDataSource = null;

	private final ClassLoader classLoader;

	/**
	 * 
	 * @param persistenceUnitName
	 *            the name of the Persistence Unit
	 * @param managedClassNames
	 *            Entity classes
	 * @param classLoader
	 *            The class loader used to load the Entity classes from name
	 * @param properties
	 *            the configuration properties
	 */
	public PersistenceUnitInfoImpl(String persistenceUnitName, List<String> managedClassNames, ClassLoader classLoader,
			Properties properties) {
		this.persistenceUnitName = persistenceUnitName;
		this.managedClassNames = managedClassNames;
		this.classLoader = classLoader;
		this.properties = properties;
	}

	@Override
	public String getPersistenceUnitName() {
		return this.persistenceUnitName;
	}

	@Override
	public String getPersistenceProviderClassName() {
		return HibernatePersistenceProvider.class.getName();
	}

	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		return this.transactionType;
	}

	@Override
	public DataSource getJtaDataSource() {
		return this.jtaDataSource;
	}

	@Override
	public DataSource getNonJtaDataSource() {
		return this.nonJtaDataSource;
	}

	@Override
	public List<String> getMappingFileNames() {
		return Collections.emptyList();
	}

	@Override
	public List<URL> getJarFileUrls() {
		return Collections.emptyList();
	}

	@Override
	public URL getPersistenceUnitRootUrl() {
		return null;
	}

	@Override
	public List<String> getManagedClassNames() {
		return this.managedClassNames;
	}

	@Override
	public boolean excludeUnlistedClasses() {
		return false;
	}

	@Override
	public SharedCacheMode getSharedCacheMode() {
		return SharedCacheMode.UNSPECIFIED;
	}

	@Override
	public ValidationMode getValidationMode() {
		return ValidationMode.AUTO;
	}

	@Override
	public Properties getProperties() {
		return this.properties;
	}

	@Override
	public String getPersistenceXMLSchemaVersion() {
		return "2.2";
	}

	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	@Override
	public void addTransformer(ClassTransformer transformer) {
		// no transformer needed
	}

	@Override
	public ClassLoader getNewTempClassLoader() {
		return null;
	}
}
