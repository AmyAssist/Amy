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

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * The class used to store information of the plugins.
 * 
 * @author Felix Burk
 */
public class Storage implements IStorage {
	private String prefix;
	private Map<String, String> globStorage;

	/**
	 * Creates a new Storage for the given prefix using the given global storage
	 * 
	 * @param prefix
	 *            The prefix to use
	 * @param globalStorage
	 *            The global storage to use
	 */
	Storage(String prefix, GlobalStorage globalStorage) {
		//TODO: : is used for debugging purposes
		this.prefix = prefix + ":";
		this.globStorage = globalStorage.getStore();
	}

	/**
	 * puts a new value with corresponding plugin key
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#put(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void put(String key, String value) {
		this.globStorage.put(this.prefix + key, value);
	}

	/**
	 * gets value of plugin key
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		return this.globStorage.get(this.prefix + key);
	}

	/**
	 * checks if plugin key has a value
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#has(java.lang.String)
	 */
	@Override
	public boolean has(String key) {
		return this.globStorage.containsKey(this.prefix + key);
	}

	/**
	 * removes entry
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage#delete(java.lang.String)
	 */
	@Override
	public void delete(String key) {
		this.globStorage.remove(this.prefix + key);

	}

}
