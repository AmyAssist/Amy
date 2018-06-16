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

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.ClassProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ClassServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.SingeltonServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

/**
 * Dependency Injection Used to manage dependencies and Service instantiation at
 * runtime. A Service that relies on DI is completely passive when it comes to
 * its runtime dependencies. There is no code in the Service that creates,
 * instantiates or gets the dependencies. The dependencies are injected into the
 * Service before the Service is executed. This reversal of responsibility to
 * instantiate (or ask for instantiate of) a dependency is called Inversion of
 * Control (IoC). This leads to loose coupling, because the Service doesn't need
 * to know about how the dependency is implemented.
 * 
 * @author Leon Kiefer, Tim Neumann
 */
public class DependencyInjection implements ServiceLocator {

	/**
	 * The logger of the DI
	 */
	protected final Logger logger = LoggerFactory.getLogger(DependencyInjection.class);

	/**
	 * A register which maps a class to it's service provider.
	 */
	protected Map<Class<?>, ServiceFunction<?>> register;

	protected Map<Class<?>, StaticProvider<?>> staticProvider;

	/**
	 * Creates a new Dependency Injection
	 */
	public DependencyInjection() {
		this.register = new HashMap<>();
		this.staticProvider = new HashMap<>();

		this.registerContextProvider(ClassProvider.class, new ClassProvider());
		this.addExternalService(ServiceLocator.class, this);
	}

	/**
	 * Registers a service
	 * 
	 * @param cls
	 *            The service to register.
	 */
	public synchronized void register(Class<?> cls) {
		Service annotation = cls.getAnnotation(Service.class);
		if (annotation == null)
			throw new ClassIsNotAServiceException(cls);
		Class<?>[] serviceTypes = annotation.value();
		if (serviceTypes.length == 0) {
			serviceTypes = cls.getInterfaces();
		}
		if (serviceTypes.length == 0) {
			serviceTypes = new Class[1];
			serviceTypes[0] = cls;
		}

		for (Class<?> serviceType : serviceTypes) {
			if (this.hasServiceOfType(serviceType)) {
				throw new DuplicateServiceException();
			}
			if (!serviceType.isAssignableFrom(cls)) {
				throw new IllegalArgumentException();
			}
		}

		for (Class<?> serviceType : serviceTypes) {
			ServiceFunction<?> p = new ClassServiceProvider<>(cls);
			this.register.put(serviceType, p);
		}
	}

	public synchronized void registerContextProvider(Class<?> key, StaticProvider<?> staticProvider) {
		this.staticProvider.put(key, staticProvider);
	}

	/**
	 * Adds an external Service instance to the DI. The DI does not manage the
	 * dependencies of the external Service, but the DI can inject the external
	 * Service as dependency into other managed services.
	 * 
	 * @param serviceType
	 *            The type of this service
	 * @param externalService
	 *            The instance of this service
	 */
	public synchronized void addExternalService(Class<?> serviceType, Object externalService) {
		if (this.hasServiceOfType(serviceType))
			throw new DuplicateServiceException();
		this.register.put(serviceType, new SingeltonServiceProvider<>(externalService));
	}

	private boolean hasServiceOfType(Class<?> serviceType) {
		return this.register.containsKey(serviceType);
	}

	@Override
	public <T> T getService(Class<T> serviceType) {
		ServiceFunction<T> provider = this.getServiceProvider(serviceType);
		ServiceFactory<T> factory = this.resolve(provider);
		return factory.build();
	}

	/**
	 * Resolve the dependencies of the ServiceProvider and creates a factory for
	 * the Service.
	 * 
	 * @param <T>
	 *            The type of the service
	 * @param serviceProvider
	 *            The Service Provider.
	 * @return The factory for a Service of the ServiceProvider.
	 */
	private <T> ServiceFactory<T> resolve(ServiceFunction<T> serviceProvider) {
		return this.resolve(serviceProvider, new ArrayDeque<>(), null);
	}

	/**
	 * Resolve the dependencies of the ServiceProvider and creates a factory for
	 * the Service. considering the stack of dependencies, for which this
	 * ServiceProvider is needed.
	 * 
	 * @param <T>
	 *            The type of the service
	 * @param serviceProvider
	 *            The Service Provider.
	 * @param stack
	 *            The stack of classes, for which this class is needed.
	 * @return The factory for a Service of the ServiceProvider.
	 */
	private <T> ServiceFactory<T> resolve(ServiceFunction<T> serviceProvider, Deque<ServiceProvider<?>> stack,
			ServiceConsumer consumer) {
		if (stack.contains(serviceProvider)) {
			throw new IllegalStateException("circular dependencies");
		} else {
			stack.push(serviceProvider);
		}
		ServiceProviderServiceFactory<T> serviceProviderServiceFactory = new ServiceProviderServiceFactory<>(
				serviceProvider);
		for (Class<?> dependency : serviceProvider.getDependencies()) {
			ServiceFunction<?> provider = this.getServiceProvider(dependency);
			ServiceFactory<?> dependencyFactory = this.resolve(provider, stack, serviceProvider);
			serviceProviderServiceFactory.resolved(dependency, dependencyFactory);
		}

		for (Class<?> requiredContextProviderType : serviceProvider.getRequiredContextProviderTypes()) {
			StaticProvider<?> contextProvider = this.getContextProvider(requiredContextProviderType);
			serviceProviderServiceFactory.setContextProvider(requiredContextProviderType, contextProvider);
		}
		serviceProviderServiceFactory.setConsumer(consumer);
		stack.pop();
		return serviceProviderServiceFactory;

	}

	@Override
	public void inject(Object instance) {
		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(instance.getClass(), Reference.class);
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			Object object = this.getService(dependency);
			Util.inject(instance, object, field);
		}
	}

	@Override
	public void postConstruct(Object instance) {
		Util.postConstruct(instance);
	}

	@SuppressWarnings("unchecked")
	private <T> ServiceFunction<T> getServiceProvider(Class<T> serviceType) {
		if (!this.register.containsKey(serviceType))
			throw new ServiceNotFoundException(serviceType);
		return (ServiceFunction<T>) this.register.get(serviceType);
	}

	private StaticProvider<?> getContextProvider(Class<?> contextProviderType) {
		if (!this.staticProvider.containsKey(contextProviderType))
			throw new NoSuchElementException(contextProviderType.getName());
		return this.staticProvider.get(contextProviderType);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator#create(java.lang.Class)
	 */
	@Override
	public <T> T create(Class<T> serviceClass) {
		Deque<ServiceProvider<?>> stack = new ArrayDeque<>();
		ServiceFunction<T> provider = new ClassServiceProvider<>(serviceClass);
		ServiceFactory<T> serviceFactory = this.resolve(provider, stack, null);

		return serviceFactory.build();
	}
}
