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

package de.unistuttgart.iaas.amyassist.amy.core.di;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;

/**
 * A implementation of the {@link ContextLocator} used to register and find static context provider for the
 * {@link ServiceProvider}.
 * 
 * @author Leon Kiefer
 */
class ContextLocatorImpl implements ContextLocator {

	private final Map<String, StaticProvider<?>> staticProviders = new ConcurrentHashMap<>();

	/**
	 * @see Configuration#registerContextProvider(String, StaticProvider)
	 */
	public void registerContextProvider(String key, StaticProvider<?> staticProvider) {
		this.staticProviders.put(key, staticProvider);
	}

	@Override
	public StaticProvider<?> getContextProvider(String contextProviderType) {
		if (!this.staticProviders.containsKey(contextProviderType))
			throw new NoSuchElementException(contextProviderType);
		return this.staticProviders.get(contextProviderType);
	}

}
