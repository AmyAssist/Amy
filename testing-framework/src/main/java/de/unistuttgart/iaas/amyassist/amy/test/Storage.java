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

package de.unistuttgart.iaas.amyassist.amy.test;

import java.util.HashMap;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * A basic implementation of IStorage for tests.
 */
public class Storage implements IStorage {

	private Map<String, String> map = new HashMap<>();

	@Override
	public void put(String key, String value) {
		this.map.put(key, value);
	}

	@Override
	public String get(String key) {
		return this.map.get(key);
	}

	@Override
	public boolean has(String key) {
		return this.map.containsKey(key);
	}

	@Override
	public void delete(String key) {
		this.map.remove(key);
	}

}
