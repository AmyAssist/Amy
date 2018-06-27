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

import javax.persistence.EntityManager;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * Implementation of IStorage using JPA
 * 
 * @author Leon Kiefer
 */
@Service
public class DatabaseStorage implements IStorage {
	@Reference
	private Persistence persistence;

	private EntityManager entityManager;

	@PostConstruct
	private void setup() {
		this.entityManager = this.persistence.getEntityManager("");
	}

	@Override
	public void put(String key, String value) {
		SimpleData simpleData = new SimpleData(key, value);
		this.entityManager.merge(simpleData);
	}

	@Override
	public String get(String key) {
		return this.entityManager.find(SimpleData.class, key).getValue();
	}

	@Override
	public boolean has(String key) {
		SimpleData find = this.entityManager.find(SimpleData.class, key);
		return find != null;
	}

	@Override
	public void delete(String key) {
		SimpleData find = this.entityManager.find(SimpleData.class, key);
		this.entityManager.remove(find);
	}

}
