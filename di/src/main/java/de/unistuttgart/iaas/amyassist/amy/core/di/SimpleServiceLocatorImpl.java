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

/**
 * A Facade for the ServiceLocator to track the creation process of services and dependent services
 * 
 * @author Leon Kiefer
 */
class SimpleServiceLocatorImpl implements SimpleServiceLocator {

	private DependencyInjection dependencyInjection;
	private ServiceCreation<?> serviceCreationInfo;

	/**
	 * @param dependencyInjection
	 * @param serviceCreationInfo
	 */
	public SimpleServiceLocatorImpl(DependencyInjection dependencyInjection, ServiceCreation<?> serviceCreationInfo) {
		this.dependencyInjection = dependencyInjection;
		this.serviceCreationInfo = serviceCreationInfo;
	}

	@Override
	public <T> ServiceHandle<T> getService(@Nonnull ServiceConsumer<T> serviceConsumer) {
		return this.dependencyInjection.getService(this.serviceCreationInfo, serviceConsumer);
	}

	/**
	 * destroy the references
	 */
	public void destroy() {
		this.dependencyInjection = null;
		this.serviceCreationInfo = null;
	}
}
