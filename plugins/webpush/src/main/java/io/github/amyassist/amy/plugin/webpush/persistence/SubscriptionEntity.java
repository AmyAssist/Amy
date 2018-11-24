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

package io.github.amyassist.amy.plugin.webpush.persistence;

import javax.persistence.*;

import io.github.amyassist.amy.registry.RegistryEntity;
import io.github.amyassist.amy.registry.Taggable;

/**
 * Database entity class.
 * 
 * @author Leon Kiefer
 */
@Entity
@PersistenceUnit(unitName = "push-subscriptions")
public class SubscriptionEntity implements RegistryEntity, Taggable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false, nullable = false)
	private int id;
	private String tag;
	private String endpoint;
	private String auth;
	private String key;

	public SubscriptionEntity() {
		// empty default constructor
	}

	public SubscriptionEntity(String endpoint, String auth, String key) {
		this.endpoint = endpoint;
		this.auth = auth;
		this.key = key;
	}

	public int getPersistentId() {
		return this.id;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getAuth() {
		return this.auth;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

	@Override
	public String getTag() {
		return this.tag;
	}

	@Override
	public void setTag(String tag) {
		this.tag = tag;
	}

}
