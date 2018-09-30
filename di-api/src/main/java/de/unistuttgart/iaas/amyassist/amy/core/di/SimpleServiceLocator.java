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

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandle;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;

/**
 * SimpleServiceLocator is used in {@link ServiceProvider} to get dependencies. The normal extended
 * {@link ServiceLocator} interface should not be used in ServiceProviders.
 * 
 * @author Leon Kiefer
 */
public interface SimpleServiceLocator {
	/**
	 * Get a service for the given service consumer. This method track the caller to prevent the service get created
	 * twice and to run in a dependency circle.
	 * 
	 * @param serviceConsumer
	 *            the service consumer of the dependency
	 * @return a service handle for the given service consumer if a service provider is found
	 * @param <T>
	 *            the type of the service
	 */
	<T> ServiceHandle<T> getService(@Nonnull ServiceConsumer<T> serviceConsumer);

}
