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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;

/**
 * A ServiceFactory for building Services for ServiceProviders
 * 
 * @author Leon Kiefer
 */
public class ServiceProviderServiceFactory<T> implements ServiceFactory<T> {

	private ServiceProvider<T> serviceProvider;

	private Map<Class<?>, ServiceFactory<?>> resolvedDependencies = new HashMap<>();
	private Map<String, StaticProvider<?>> contextProviders = new HashMap<>();
	@Nullable
	private ServiceConsumer consumerClass;

	private T buildedInstance;

	public ServiceProviderServiceFactory(ServiceProvider<T> serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@Override
	public T build() {
		if (this.buildedInstance == null) {
			this.buildedInstance = this.serviceProvider.getService(this.resolvedDependencies, this.contextProviders,
					this.consumerClass);
		}
		return this.buildedInstance;
	}

	public void resolved(Class<?> dependency, ServiceFactory<?> dependencyFactory) {
		this.resolvedDependencies.put(dependency, dependencyFactory);
	}

	public void setContextProvider(String requiredContextIdentifier, StaticProvider<?> contextProvider) {
		this.contextProviders.put(requiredContextIdentifier, contextProvider);
	}

	public void setConsumer(@Nullable ServiceConsumer consumer) {
		this.consumerClass = consumer;
	}
}
