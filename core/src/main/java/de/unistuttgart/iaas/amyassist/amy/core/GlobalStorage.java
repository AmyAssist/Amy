/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Global storage placeholder until a database or similar mechanisms are
 * implemented
 * 
 * @author Felix Burk
 */
public class GlobalStorage {
	/**
	 * The map in which everything is stored.
	 */
	protected Map<String, String> store;

	/**
	 * Creates a new Global Storage.
	 * This should only be called once.
	 */
	public GlobalStorage() {
		Map<String, String> tempMap = new HashMap<>();
		
		//creates a thread save HashMap
		//it's not perfect for performance but ensures consistency
		//ConcurrentHashMap might be an alternative
		this.store = Collections.synchronizedMap(tempMap);
	}

	/**
	 * returns global storage HashMap
	 * 
	 * @return store
	 */
	public Map<String, String> getStore() {
		return this.store;
	}

}
