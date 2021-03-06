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

package io.github.amyassist.amy.httpserver.di;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;

import io.github.amyassist.amy.core.di.ServiceDescription;
import io.github.amyassist.amy.core.di.ServiceLocator;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;

/**
 * Add Support for @Reference dependency declarations in REST Resources.
 * 
 * @author Leon Kiefer
 */
@Singleton
public class ServiceInjectionResolver implements InjectionResolver<Reference> {

	private ServiceLocator serviceLocator;

	/**
	 * Create a new hk2 InjectionResolver
	 * 
	 * @param serviceLocator
	 *            the ServiceLocator of the di to use for Service lookups
	 */
	ServiceInjectionResolver(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#isConstructorParameterIndicator()
	 */
	@Override
	public boolean isConstructorParameterIndicator() {
		return false;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#isMethodParameterIndicator()
	 */
	@Override
	public boolean isMethodParameterIndicator() {
		return false;
	}

	/**
	 * @see org.glassfish.hk2.api.InjectionResolver#resolve(org.glassfish.hk2.api.Injectee,
	 *      org.glassfish.hk2.api.ServiceHandle)
	 */
	@Override
	public Object resolve(Injectee arg0, ServiceHandle<?> arg1) {
		Type requiredType = arg0.getRequiredType();
		return this.serviceLocator.getService(
				this.getServiceCosumer((Class<?>) requiredType, arg0.getInjecteeClass(), arg0.getRequiredQualifiers()))
				.getService();
	}

	private <T> ServiceConsumer<T> getServiceCosumer(Class<T> serviceType, Class<?> consumerClass,
			Set<Annotation> qualifiers) {
		return new ServiceConsumer<T>() {

			@Override
			public Class<?> getConsumerClass() {
				return consumerClass;
			}

			@Override
			public ServiceDescription<T> getServiceDescription() {
				return new ServiceDescription<T>() {

					@Override
					public Class<T> getServiceType() {
						return serviceType;
					}

					@Override
					public Set<Annotation> getAnnotations() {
						return qualifiers;
					}
				};
			}
		};
	}

}
