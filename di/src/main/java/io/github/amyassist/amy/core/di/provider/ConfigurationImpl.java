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

package io.github.amyassist.amy.core.di.provider;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.Configuration;
import io.github.amyassist.amy.core.di.InternalServiceLocator;
import io.github.amyassist.amy.core.di.context.provider.StaticProvider;
import io.github.amyassist.amy.core.di.util.ConfigurationUtil;

/**
 * Implementation of Configuration using the InternalServiceLocator.
 * 
 * @author Leon Kiefer
 */
public class ConfigurationImpl implements Configuration {

	private InternalServiceLocator internalServiceLocator;

	/**
	 * @param internalServiceLocator
	 */
	public ConfigurationImpl(InternalServiceLocator internalServiceLocator) {
		this.internalServiceLocator = internalServiceLocator;
	}

	@Override
	public void registerContextProvider(String key, StaticProvider<?> staticProvider) {
		this.internalServiceLocator.registerContextProvider(key, staticProvider);
	}

	@Override
	public void register(@Nonnull Class<?> cls) {
		ClassServiceProvider<?> classServiceProvider = ConfigurationUtil.getClassServiceProvider(cls);

		this.register(classServiceProvider);
		this.internalServiceLocator.onRegister(classServiceProvider);
	}

	@Override
	public <T> void register(@Nonnull ServiceProvider<T> serviceProvider) {
		this.internalServiceLocator.register(serviceProvider);
	}

}
