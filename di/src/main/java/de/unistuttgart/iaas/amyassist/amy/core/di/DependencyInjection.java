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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.ClassProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ClassServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ClassServiceProviderWithoutDependencies;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.SingeltonServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

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
public class DependencyInjection implements ServiceLocator {
	/**
	 * A register which maps a service description to it's service provider.
	 */
	protected Map<ServiceDescription<?>, ServiceFunction<?>> register;

	protected Map<String, StaticProvider<?>> staticProvider;

	/**
	 * Creates a new Dependency Injection
	 */
	public DependencyInjection() {
		this.register = new HashMap<>();
		this.staticProvider = new HashMap<>();

		this.registerContextProvider("class", new ClassProvider());
		this.addExternalService(ServiceLocator.class, this);
	}

	/**
	 * Registers a service provider
	 * 
	 * @param serviceType
	 *            The type of this service
	 * @param serviceFunction
	 *            The instance of the service provider
	 */
	public synchronized <T> void register(Class<T> serviceType, ServiceFunction<T> serviceFunction) {
		this.register(Util.serviceDescriptionFor(serviceType), serviceFunction);
	}

	/**
	 * Registers a service provider
	 * 
	 * @param serviceDescription
	 *            The description of service
	 * @param serviceFunction
	 *            The instance of the service provider
	 */
	public synchronized <T> void register(ServiceDescription<T> serviceDescription,
			ServiceFunction<T> serviceFunction) {
		if (this.hasServiceOfType(serviceDescription))
			throw new DuplicateServiceException();
		this.register.put(serviceDescription, serviceFunction);
	}

	/**
	 * Registers a service
	 * 
	 * @param cls
	 *            The service to register.
	 */
	public synchronized void register(@Nonnull Class<?> cls) {
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
			if (this.hasServiceOfType(Util.serviceDescriptionFor(serviceType))) {
				throw new DuplicateServiceException();
			}
			if (!serviceType.isAssignableFrom(cls)) {
				throw new IllegalArgumentException();
			}
		}

		for (Class<?> serviceType : serviceTypes) {
			ServiceFunction<?> p = new ClassServiceProvider<>(cls);
			this.register.put(Util.serviceDescriptionFor(serviceType), p);
		}
	}

	public synchronized void registerContextProvider(String key, StaticProvider<?> staticProvider) {
		this.staticProvider.put(key, staticProvider);
	}

	/**
	 * Adds an external Service instance to the DI. The DI does not manage the dependencies of the external Service, but
	 * the DI can inject the external Service as dependency into other managed services.
	 * 
	 * @param serviceType
	 *            The type of this service
	 * @param externalService
	 *            The instance of this service
	 */
	public synchronized <T> void addExternalService(@Nonnull Class<T> serviceType, @Nonnull T externalService) {
		this.register(serviceType, new SingeltonServiceProvider<>(externalService));
	}

	private boolean hasServiceOfType(ServiceDescription<?> serviceDescription) {
		return this.register.containsKey(serviceDescription);
	}

	@Override
	public <T> T getService(Class<T> serviceType) {
		return this.getService(Util.serviceDescriptionFor(serviceType));
	}

	/**
	 * @see ServiceLocator#getService(Class)
	 */
	public <T> T getService(Class<T> serviceType, @Nullable ServiceConsumer consumer) {
		return this.getService(Util.serviceDescriptionFor(serviceType), consumer);
	}

	/**
	 * @see ServiceLocator#getService(ServiceDescription)
	 */
	public <T> T getService(ServiceDescription<T> serviceDescription, @Nullable ServiceConsumer consumer) {
		ServiceFunction<T> provider = this.getServiceProvider(serviceDescription);
		ServiceFactory<T> factory = this.resolve(provider, consumer);
		return factory.build();
	}

	@Override
	public <T> T getService(ServiceDescription<T> serviceDescription) {
		return this.getService(serviceDescription, null);
	}

	/**
	 * Resolve the dependencies of the ServiceProvider and creates a factory for the Service.
	 * 
	 * @param <T>
	 *            The type of the service
	 * @param serviceProvider
	 *            The Service Provider.
	 * @return The factory for a Service of the ServiceProvider.
	 */
	private <T> ServiceFactory<T> resolve(@Nonnull ServiceFunction<T> serviceProvider) {
		return this.resolve(serviceProvider, null);
	}

	/**
	 * Resolve the dependencies of the ServiceProvider and creates a factory for the Service.
	 * 
	 * @param <T>
	 *            The type of the service
	 * @param serviceProvider
	 *            The Service Provider.
	 * @param consumer
	 *            the consumer of the resolved service
	 * @return The factory for a Service of the ServiceProvider.
	 */
	private <T> ServiceFactory<T> resolve(@Nonnull ServiceFunction<T> serviceProvider,
			@Nullable ServiceConsumer consumer) {
		return this.resolve(serviceProvider, new ArrayDeque<>(), consumer);
	}

	/**
	 * Resolve the dependencies of the ServiceProvider and creates a factory for the Service. considering the stack of
	 * dependencies, for which this ServiceProvider is needed.
	 * 
	 * @param <T>
	 *            The type of the service
	 * @param serviceProvider
	 *            The Service Provider.
	 * @param stack
	 *            The stack of classes, for which this class is needed.
	 * @param consumer
	 *            the consumer of the resolved service
	 * @return The factory for a Service of the ServiceProvider.
	 */
	private <T> ServiceFactory<T> resolve(@Nonnull ServiceFunction<T> serviceProvider,
			@Nonnull Deque<ServiceProvider<?>> stack, @Nullable ServiceConsumer consumer) {
		if (stack.contains(serviceProvider)) {
			throw new IllegalStateException("circular dependencies");
		} else {
			stack.push(serviceProvider);
		}
		ServiceProviderServiceFactory<T> serviceProviderServiceFactory = new ServiceProviderServiceFactory<>(
				serviceProvider);
		for (ServiceDescription<?> dependency : serviceProvider.getDependencies()) {
			ServiceFunction<?> provider = this.getServiceProvider(dependency);
			ServiceFactory<?> dependencyFactory = this.resolve(provider, stack, serviceProvider);
			serviceProviderServiceFactory.resolved(dependency, dependencyFactory);
		}

		for (String requiredContextProviderType : serviceProvider.getRequiredContextIdentifiers()) {
			StaticProvider<?> contextProvider = this.getContextProvider(requiredContextProviderType);
			serviceProviderServiceFactory.setContextProvider(requiredContextProviderType, contextProvider);
		}
		serviceProviderServiceFactory.setConsumer(consumer);
		stack.pop();
		return serviceProviderServiceFactory;

	}

	@Override
	public void inject(@Nonnull Object instance) {
		Class<? extends Object> classOfInstance = instance.getClass();
		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(classOfInstance, Reference.class);
		for (Field field : dependencyFields) {
			Class<?> dependency = field.getType();
			Object object = this.getService(dependency, new ServiceConsumer() {
				@Override
				public Class<?> getConsumerClass() {
					return classOfInstance;
				}
			});
			Util.inject(instance, object, field);
		}
	}

	@Override
	public void postConstruct(@Nonnull Object postConstructMe) {
		Util.postConstruct(postConstructMe);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	private <T> ServiceFunction<T> getServiceProvider(ServiceDescription<T> serviceDescription) {
		if (!this.register.containsKey(serviceDescription))
			throw new ServiceNotFoundException(serviceDescription);
		return (ServiceFunction<T>) this.register.get(serviceDescription);
	}

	private StaticProvider<?> getContextProvider(@Nonnull String contextProviderType) {
		if (!this.staticProvider.containsKey(contextProviderType))
			throw new NoSuchElementException(contextProviderType);
		return this.staticProvider.get(contextProviderType);
	}

	@Override
	public <T> T create(@Nonnull Class<T> serviceClass) {
		ServiceFunction<T> provider = new ClassServiceProviderWithoutDependencies<>(serviceClass);
		ServiceFactory<T> serviceFactory = this.resolve(provider);

		return serviceFactory.build();
	}

	@Override
	public <T> T createAndInitialize(@Nonnull Class<T> serviceClass) {
		T service = this.create(serviceClass);
		this.inject(service);
		this.postConstruct(service);
		return service;
	}

	@Override
	public void preDestroy(@Nonnull Object destroyMe) {
		Util.preDestroy(destroyMe);
	}

	@Override
	public void shutdown() {
		// TODO manage the lifecycle
	}
}
