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

package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.di.ContextLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescriptionImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceImplementationDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.SimpleServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;

/**
 * A ServiceProvider which provides only a single existing instance
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the type of the singleton
 */
public class SingletonServiceProvider<T> implements ServiceProvider<T> {

	private final T instance;
	private final Class<T> serviceType;

	/**
	 * Create a new Singleton Service provider which provides the given service instance as service of the given type.
	 * 
	 * @param serviceType
	 *            the type of the singleton
	 * @param instance
	 *            The instance of this service
	 */
	public SingletonServiceProvider(@Nonnull Class<T> serviceType, @Nonnull T instance) {
		this.serviceType = serviceType;
		this.instance = instance;
	}

	@Override
	public @Nonnull ServiceDescription<T> getServiceDescription() {
		return new ServiceDescriptionImpl<>(this.serviceType);
	}

	@Override
	public ServiceImplementationDescription<T> getServiceImplementationDescription(@Nonnull ContextLocator locator,
			@Nonnull ServiceConsumer<T> serviceConsumer) {
		return new ServiceImplementationDescriptionImpl<>(serviceConsumer.getServiceDescription(),
				this.instance.getClass());
	}

	@Override
	public @Nonnull ServiceHandle<T> createService(@Nonnull SimpleServiceLocator locator,
			@Nonnull ServiceImplementationDescription<T> serviceImplementationDescription) {
		return new ServiceHandleImpl<>(this.instance);
	}

	@Override
	public void dispose(ServiceHandle<T> service) {
		// singleton can not be disposed
	}

}
