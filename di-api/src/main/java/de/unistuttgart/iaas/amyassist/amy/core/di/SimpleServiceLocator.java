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

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandle;

/**
 * TODO: Description
 * @author Leon Kiefer
 */
public interface SimpleServiceLocator {
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
	<T> ServiceHandle<T> getService(ServiceConsumer<T> serviceConsumer);

	/**
	 * Getter for the Context provider for the given identifier
	 * 
	 * @param contextProviderType
	 *            the context identifier
	 * @return the static ContextProvider
	 * @throws NoSuchElementException
	 *             if there is no ContextProvider for the given identifier
	 */
	StaticProvider<?> getContextProvider(@Nonnull String contextProviderType);
}
