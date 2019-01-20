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

import io.github.amyassist.amy.core.di.*;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;
import io.github.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;
import io.github.amyassist.amy.core.di.runtime.ServiceInstantiationDescriptionImpl;

/**
 * Creates the ConfigurationImpl service.
 * 
 * @author Leon Kiefer
 */
public class ConfigurationProvider implements ServiceProvider<Configuration> {

	private final InternalServiceLocator internalServiceLocator;

	/**
	 * @param internalServiceLocator
	 */
	public ConfigurationProvider(InternalServiceLocator internalServiceLocator) {
		this.internalServiceLocator = internalServiceLocator;
	}

	@Override
	@Nonnull
	public ServiceDescription<Configuration> getServiceDescription() {
		return new ServiceDescriptionImpl<>(Configuration.class);
	}

	@Override
	public ServiceInstantiationDescription<Configuration> getServiceInstantiationDescription(
			@Nonnull ContextLocator locator, @Nonnull ServiceConsumer<Configuration> serviceConsumer) {
		return new ServiceInstantiationDescriptionImpl<>(this.getServiceDescription(), ConfigurationImpl.class);
	}

	@Override
	@Nonnull
	public Configuration createService(@Nonnull SimpleServiceLocator locator,
			@Nonnull ServiceInstantiationDescription<Configuration> serviceInstantiationDescription) {
		return new ConfigurationImpl(this.internalServiceLocator);
	}

	@Override
	public void dispose(@Nonnull Configuration service,
			@Nonnull ServiceInstantiationDescription<Configuration> serviceInstantiationDescription) {
		// nothing to do here
	}

}
