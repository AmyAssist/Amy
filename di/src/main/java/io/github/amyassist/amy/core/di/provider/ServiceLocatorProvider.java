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

import java.util.Collections;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.*;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;
import io.github.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;
import io.github.amyassist.amy.core.di.runtime.ServiceInstantiationDescriptionImpl;

/**
 * Provider for the ServiceLocator in the DI with the correct consumer class.
 * 
 * @author Leon Kiefer
 */
public class ServiceLocatorProvider implements ServiceProvider<ServiceLocator> {

	private SimpleServiceLocator simpleServiceLocator;

	/**
	 * @param simpleServiceLocator
	 *            used by the created ServiceLocators to resolve Services
	 */
	public ServiceLocatorProvider(SimpleServiceLocator simpleServiceLocator) {
		this.simpleServiceLocator = simpleServiceLocator;
	}

	@Override
	@Nonnull
	public ServiceDescription<ServiceLocator> getServiceDescription() {
		return new ServiceDescriptionImpl<>(ServiceLocator.class);
	}

	@Override
	public ServiceInstantiationDescription<ServiceLocator> getServiceInstantiationDescription(
			@Nonnull ContextLocator locator, @Nonnull ServiceConsumer<ServiceLocator> serviceConsumer) {
		return new ServiceInstantiationDescriptionImpl<>(this.getServiceDescription(),
				Collections.singletonMap("consumerClass",
						locator.getContextProvider("class").getContext(serviceConsumer)),
				InjectableServiceLocator.class);
	}

	@Override
	@Nonnull
	public ServiceLocator createService(@Nonnull SimpleServiceLocator locator,
			@Nonnull ServiceInstantiationDescription<ServiceLocator> serviceInstantiationDescription) {
		return new InjectableServiceLocator(this.simpleServiceLocator,
				(Class<?>) serviceInstantiationDescription.getContext().get("consumerClass"));
	}

	@Override
	public void dispose(@Nonnull ServiceLocator service,
			@Nonnull ServiceInstantiationDescription<ServiceLocator> serviceInstantiationDescription) {
		// nothing to do here
	}

}
