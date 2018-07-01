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

package de.unistuttgart.iaas.amyassist.amy.core.persistence.storage;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;

import de.unistuttgart.iaas.amyassist.amy.core.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.Persistence;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * Implementation of IStorage for Plugins using JPA
 * 
 * @author Leon Kiefer
 */
@Service
public class DatabaseStorage implements IStorage {
	@Reference
	private Persistence persistence;

	@Context(de.unistuttgart.iaas.amyassist.amy.core.di.Context.PLUGIN)
	private IPlugin plugin;

	private EntityManager entityManager;

	private String prefix;

	@PostConstruct
	private void setup() {
		this.prefix = this.plugin.getUniqueName() + ":";
		this.persistence.register(SimpleData.class);
		this.entityManager = this.persistence.getEntityManager("DatabaseStorage");
	}

	@Override
	public void put(String key, String value) {
		SimpleData simpleData = new SimpleData(this.prefix + key, value);
		this.entityManager.getTransaction().begin();
		this.entityManager.merge(simpleData);
		this.entityManager.getTransaction().commit();
	}

	@Override
	public String get(String key) {
		return this.entityManager.find(SimpleData.class, this.prefix + key).getValue();
	}

	@Override
	public boolean has(String key) {
		SimpleData find = this.entityManager.find(SimpleData.class, this.prefix + key);
		return find != null;
	}

	@Override
	public void delete(String key) {
		SimpleData find = this.entityManager.find(SimpleData.class, this.prefix + key);
		this.entityManager.remove(find);
	}

	@PreDestroy
	private void destroy() {
		this.entityManager.close();
	}

}
