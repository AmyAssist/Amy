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

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Data Object for DatabaseStorage
 * 
 * @author Leon Kiefer
 */
@Entity
public class SimpleData {

	@Id
	private String key;

	private String value;

	private SimpleData() {

	}

	/**
	 * @param key
	 *            the primary key
	 * @param value
	 *            the value of the data
	 */
	public SimpleData(String key, String value) {
		this.key = key;
		this.value = value;
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
	 * Get's {@link #value value}
	 * 
	 * @return value
	 */
	public String getValue() {
		return this.value;
	}
}
