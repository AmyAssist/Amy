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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.ContextLocator;
import io.github.amyassist.amy.core.di.ServiceDescription;
import io.github.amyassist.amy.core.di.ServiceInstantiationDescription;
import io.github.amyassist.amy.core.di.SimpleServiceLocator;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;

/**
 * A Service Provider is responsible for Providing Services of a type. The Service Provider manages the dependencies and
 * the Services he has provided. If the does not exist it creates a new one and is responsible for disposing this
 * service.
 * 
 * @author Leon Kiefer
 *
 * @param <T>
 *            service type
 */
public interface ServiceProvider<T> {
	/**
	 * The description of services this Service Provider can provide. This is used to find this ServiceProvider when a
	 * service is requested.
	 * 
	 * @return the service description
	 */
	@Nonnull
	ServiceDescription<T> getServiceDescription();

	/**
	 * Refine the ServiceDescription of a ServiceConsumer. The ServiceProvider can decide if it can provide the
	 * requested service. If this ServiceProvider can provide the service, it returns the
	 * ServiceInstantiationDescription for the Service. The ServiceInstantiationDescription contains all information
	 * needed to create the requested Service for the ServiceConsumer.
	 * 
	 * @param locator
	 *            the ContextLocator which can be used to lookup ContextProvider
	 * @param serviceConsumer
	 *            the consumer of the Service. This can be used to extract context information.
	 * @return the ServiceInstantiationDescription for the Service this Provider can provide or null if this provider
	 *         can not provide the requested Service.
	 */
	@CheckForNull
	ServiceInstantiationDescription<T> getServiceInstantiationDescription(@Nonnull ContextLocator locator,
			@Nonnull ServiceConsumer<T> serviceConsumer);

	/**
	 * Create a new Service from the given ServiceInstantiationDescription. Using the ServiceLocator to lookup
	 * dependencies.
	 * 
	 * @param locator
	 *            the ServiceLocator to lookup services, don't pass this reference to any created Service
	 * @param serviceInstantiationDescription
	 *            the description of the Service which must be created
	 * 
	 * @return the created service of this ServiceProvider for the given ServiceInstantiationDescription
	 */
	@Nonnull
	T createService(@Nonnull SimpleServiceLocator locator,
			@Nonnull ServiceInstantiationDescription<T> serviceInstantiationDescription);

	/**
	 * Dispose a Service that was provided by this ServiceProvider.
	 * 
	 * @param service
	 *            the Service that should be disposed
	 * @param serviceInstantiationDescription
	 *            the description of the Service which should be disposed
	 * @throws IllegalArgumentException
	 *             if the given Service was not provided by this ServiceProvider.
	 */
	void dispose(@Nonnull T service, @Nonnull ServiceInstantiationDescription<T> serviceInstantiationDescription);

}
