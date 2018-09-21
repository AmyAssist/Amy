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

package de.unistuttgart.iaas.amyassist.amy.plugin.tosca.configurations;

import javax.persistence.*;

import de.unistuttgart.iaas.amyassist.amy.registry.RegistryEntity;
import de.unistuttgart.iaas.amyassist.amy.registry.Taggable;

/**
 * The class representing a configuration entry
 * 
 * @author Tim Neumann
 */
@Entity
@PersistenceUnit(unitName = "ConfigurationRegistry")
public class ConfigurationEntry implements RegistryEntity, Taggable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false, nullable = false)
	private int persistentId;

	/** The key of the entry. */
	private String key;
	/** The value of the entry. */
	private String value;
	/** The tag of the entry. */
	private String tag;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.registry.RegistryEntity#getPersistentId()
	 */
	@Override
	public int getPersistentId() {
		return this.persistentId;
	}

	/**
	 * Get's {@link #key key}
	 * 
	 * @return key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Set's {@link #key key}
	 * 
	 * @param key
	 *            key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Get's {@link #value value}
	 * 
	 * @return value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Set's {@link #value value}
	 * 
	 * @param value
	 *            value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get's {@link #tag tag}
	 * 
	 * @return tag
	 */
	@Override
	public String getTag() {
		return this.tag;
	}

	/**
	 * Set's {@link #tag tag}
	 * 
	 * @param tag
	 *            tag
	 */
	@Override
	public void setTag(String tag) {
		this.tag = tag;
	}

}
