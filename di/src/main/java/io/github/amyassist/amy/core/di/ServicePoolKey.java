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

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;

/**
 * The Key in the Service Pool of the {@link DependencyInjection} implementation.
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the type of the service
 */
final class ServicePoolKey<T> {

	@Nonnull
	final ServiceProvider<T> serviceProvider;
	@Nonnull
	final Map<String, Object> context;
	@Nonnull
	final Class<T> serviceType;
	@Nonnull
	final Class<?> implementationClass;

	/**
	 * Create a new Key for a service from the given ServiceProvider with the given context. Keys with the same
	 * ServiceProvider and context are equal. Also ServiceType and implementation class are required.
	 * 
	 * @param serviceProvider
	 *            the service provider of the service this key is for
	 * @param context
	 * @param serviceType
	 * @param implementationClass
	 */
	public ServicePoolKey(@Nonnull ServiceProvider<T> serviceProvider, Map<String, Object> context,
			@Nonnull Class<T> serviceType, @Nonnull Class<?> implementationClass) {
		this.serviceProvider = serviceProvider;
		this.context = new HashMap<>(context);
		this.serviceType = serviceType;
		this.implementationClass = implementationClass;
	}

	/**
	 * Create a Key from a provider and a ServiceImplementation
	 * 
	 * @param serviceProvider
	 * @param serviceInstantiationDescription
	 */
	public ServicePoolKey(@Nonnull ServiceProvider<T> serviceProvider,
			ServiceInstantiationDescription<T> serviceInstantiationDescription) {
		this(serviceProvider, serviceInstantiationDescription.getContext(),
				serviceInstantiationDescription.getServiceDescription().getServiceType(),
				serviceInstantiationDescription.getImplementationClass());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.serviceProvider.hashCode();
		result = prime * result + this.context.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		ServicePoolKey<?> other = (ServicePoolKey<?>) obj;
		if (!this.serviceProvider.equals(other.serviceProvider))
			return false;
		if (!this.context.equals(other.context))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Service type: " + this.serviceType.getName() + "\nServiceProvider: " + this.serviceProvider
				+ "\nImplementation class: " + this.implementationClass.getName() + "\nContext: " + this.context;
	}

}
