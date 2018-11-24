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

package io.github.amyassist.amy.core.di;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.context.provider.StaticProvider;
import io.github.amyassist.amy.core.di.provider.ServiceProvider;

/**
 * Configuration of the Service Locator
 * 
 * @author Leon Kiefer
 */
public interface Configuration {

	/**
	 * Register a StaticContextProvider to be used later by ServiceProviders
	 * 
	 * @param key
	 *            the string key which is used to later find that staticProvider
	 * @param staticProvider
	 *            the StaticProvider that should be registered
	 */
	void registerContextProvider(String key, StaticProvider<?> staticProvider);

	/**
	 * Registers a service implementation
	 * 
	 * @param cls
	 *            The service to register.
	 */
	void register(@Nonnull Class<?> cls);

	/**
	 * Registers a service provider
	 * 
	 * @param serviceProvider
	 *            The instance of the service provider
	 * @param <T>
	 *            the type of the Service this ServiceProvider provides
	 */
	<T> void register(@Nonnull ServiceProvider<T> serviceProvider);

}
