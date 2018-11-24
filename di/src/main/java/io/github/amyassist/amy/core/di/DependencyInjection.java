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

import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.ParametersAreNullableByDefault;

import io.github.amyassist.amy.core.di.consumer.ServiceConsumerImpl;
import io.github.amyassist.amy.core.di.extension.Extension;
import io.github.amyassist.amy.core.di.provider.ClassServiceProvider;
import io.github.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;

/**
 * Dependency Injection Used to manage dependencies and Service instantiation at runtime. A Service that relies on DI is
 * completely passive when it comes to its runtime dependencies. There is no code in the Service that creates,
 * instantiates or gets the dependencies. The dependencies are injected into the Service before the Service is executed.
 * This reversal of responsibility to instantiate (or ask for instantiate of) a dependency is called Inversion of
 * Control (IoC). This leads to loose coupling, because the Service doesn't need to know about how the dependency is
 * implemented.
 * 
 * @author Leon Kiefer, Tim Neumann
 */
@ParametersAreNullableByDefault
public class DependencyInjection {
	private final Set<Extension> extensions;

	private final InternalServiceLocator internalServiceLocator;

	/**
	 * Creates a new Dependency Injection
	 * 
	 * @param extensions
	 *            for the dependency injection
	 */
	public DependencyInjection(Extension... extensions) {
		this.internalServiceLocator = new InternalServiceLocator(this::onRegister);

		this.extensions = new HashSet<>(Arrays.asList(extensions));
		this.extensions.forEach(ext -> ext.postConstruct(this));
	}

	private <T> void onRegister(ClassServiceProvider<T> classServiceProvider) {
		this.extensions.forEach(ext -> ext.onRegister(classServiceProvider.getServiceDescription(),
				classServiceProvider.getImplementationClass()));
	}

	/**
	 * Loads Services using the provider configuration file
	 * META-INF/services/io.github.amyassist.amy.core.di.ServiceProviderLoader and the
	 * {@link ServiceProviderLoader}
	 * 
	 * @see java.util.ServiceLoader
	 */
	public void loadServices() {
		ServiceLoader.load(ServiceProviderLoader.class).forEach(s -> s.load(this.getConfiguration()));
	}

	/**
	 * @return the instance of the Configuration owned by the DependencyInjection
	 */
	public Configuration getConfiguration() {
		return this.internalServiceLocator
				.getService(
						new ServiceConsumerImpl<>(this.getClass(), new ServiceDescriptionImpl<>(Configuration.class)))
				.getService();
	}

	/**
	 * The ServiceLocator of the DependencyInjection
	 * 
	 * @return the instance of the ServiceLocator owned by the DependencyInjection
	 */
	public ServiceLocator getServiceLocator() {
		return this.internalServiceLocator
				.getService(
						new ServiceConsumerImpl<>(this.getClass(), new ServiceDescriptionImpl<>(ServiceLocator.class)))
				.getService();
	}

}
