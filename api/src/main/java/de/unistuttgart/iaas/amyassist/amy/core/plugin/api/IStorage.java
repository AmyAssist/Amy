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

package de.unistuttgart.iaas.amyassist.amy.core.plugin.api;

/**
 * Interface that defines the storage provided by the core.
 * 
 * @author Leon Kiefer, Tim Neumann
 */
public interface IStorage {

	/**
	 * Stores a string value under a given key
	 * 
	 * @param key
	 *            The key for the data to store
	 * @param value
	 *            The value of the data to store
	 */
	void put(String key, String value);

	/**
	 * Returns the value for the given key
	 * 
	 * @param key
	 *            The key of the data to return
	 * @return the value of the data when the given key is not set
	 */
	String get(String key);

	/**
	 * Check if a given key is set
	 * 
	 * @param key
	 *            The key to check
	 * @return Whether the key is set
	 */
	boolean has(String key);

	/**
	 * delete value corresponding to key
	 * 
	 * @param key
	 *            The key of the data to delete
	 */
	void delete(String key);

}
