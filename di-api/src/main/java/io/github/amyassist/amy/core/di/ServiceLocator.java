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

import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;
import io.github.amyassist.amy.core.di.provider.ServiceHandle;

/**
 * ServiceLocator is the registry for Services.
 * 
 * @author Leon Kiefer
 */
public interface ServiceLocator {

	/**
	 * Instantiate the given class if it can, inject dependencies and post-constructs the object. The object created in
	 * this way will not be managed by the DI.
	 * 
	 * @param serviceClass
	 *            the implementation of a service
	 * @return the created instance of the given class
	 * @param <T>
	 *            the type of the implementation class
	 */
	<T> T createAndInitialize(@Nonnull Class<T> serviceClass);

	/**
	 * Get the service of the given type. This method lookup the service provider registered for the the given service
	 * type and use it to return a object of the given type.
	 * 
	 * @param serviceType
	 *            the type of the service, not the class which implements the service
	 * @return an instance of the given type if a service provider is found
	 * @param <T>
	 *            the type of the service
	 */
	<T> T getService(Class<T> serviceType);

	/**
	 * Get the service with the given description. This method lookup the service provider registered for the given
	 * service description and use it to return a service handle for the Service of the given type.
	 * 
	 * In this case the service consumer is unknown.
	 * 
	 * @param serviceDescription
	 *            the Description of the service
	 * @return a service handle for the described service if a service provider is found
	 * @param <T>
	 *            the type of the service
	 */
	<T> ServiceHandle<T> getService(ServiceDescription<T> serviceDescription);

	/**
	 * Get the service for the given service consumer. First the service Description for the service consumer is looked
	 * up and then, this method lookup the service provider registered for the service description and use it to return
	 * a service handle for the service consumer.
	 * 
	 * @param serviceConsumer
	 *            a service consumer for which we need to find the service
	 * @return a service handle for the given service consumer if a service provider is found
	 * @param <T>
	 *            the type of the service
	 */
	<T> ServiceHandle<T> getService(@Nonnull ServiceConsumer<T> serviceConsumer);

	/**
	 * This will analyze the given object and inject into its fields. The object given will not be managed by the DI.
	 * 
	 * @param injectMe
	 */
	void inject(@Nonnull Object injectMe);

	/**
	 * This will analyze the given object and call the postConstruct method. The object given will not be managed by the
	 * DI.
	 * 
	 * @param postConstructMe
	 */
	void postConstruct(@Nonnull Object postConstructMe);

	/**
	 * This will analyze the given object and call the preDestroy method. The object given will not be managed by the
	 * DI.
	 * 
	 * @param destroyMe
	 */
	void preDestroy(@Nonnull Object destroyMe);

	/**
	 * This method will shutdown every service associated with this ServiceLocator. Those services that have a
	 * preDestroy shall have their preDestroy called.
	 */
	void shutdown();

}
