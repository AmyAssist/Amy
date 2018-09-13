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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumerImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.ClassProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.exception.ClassIsNotAServiceException;
import de.unistuttgart.iaas.amyassist.amy.core.di.exception.DuplicateServiceException;
import de.unistuttgart.iaas.amyassist.amy.core.di.exception.ServiceNotFoundException;
import de.unistuttgart.iaas.amyassist.amy.core.di.extension.Extension;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.*;
import de.unistuttgart.iaas.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.ServiceLocatorUtil;
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
public class DependencyInjection implements ServiceLocator, Configuration, SimpleServiceLocator {
	/**
	 * A register which maps a service description to it's service provider.
	 */
	private final Map<ServiceKey<?>, ServiceProvider<?>> register;

	private final Map<ServicePoolKey<?>, ServiceHandle<?>> servicePool;

	private final Map<ServicePoolKey<?>, ServiceCreation<?>> serviceCreationInfos;

	@Nonnull
	private final ContextLocatorImpl contextLocator;

	private final Set<Extension> extensions;

	/**
	 * Creates a new Dependency Injection
	 * 
	 * @param extensions
	 *            for the dependency injection
	 */
	public DependencyInjection(Extension... extensions) {
		this.register = new ConcurrentHashMap<>();
		this.servicePool = new ConcurrentHashMap<>();
		this.serviceCreationInfos = new ConcurrentHashMap<>();
		this.contextLocator = new ContextLocatorImpl();
		this.extensions = new HashSet<>(Arrays.asList(extensions));

		this.registerContextProvider("class", new ClassProvider());
		this.register(new ServiceLocatorProvider(this));
		this.register(new SingletonServiceProvider<>(Configuration.class, this));
		this.extensions.forEach(ext -> ext.postConstruct(this));
	}

	/**
	 * Loads Services using the provider configuration file
	 * META-INF/services/de.unistuttgart.iaas.amyassist.amy.core.di.ServiceProviderLoader and the
	 * {@link ServiceProviderLoader}
	 * 
	 * @see java.util.ServiceLoader
	 */
	public void loadServices() {
		ServiceLoader.load(ServiceProviderLoader.class).forEach(s -> s.load(this));
	}

	@Override
	public <T> void register(@Nonnull ServiceProvider<T> serviceProvider) {
		ServiceDescription<T> serviceDescription = serviceProvider.getServiceDescription();
		ServiceKey<T> serviceKey = new ServiceKey<>(serviceDescription);
		synchronized (this.register) {
			if (this.register.containsKey(serviceKey))
				throw new DuplicateServiceException(serviceDescription);
			this.register.put(serviceKey, serviceProvider);
		}
	}

	@Override
	public void register(@Nonnull Class<?> cls) {
		Service annotation = cls.getAnnotation(Service.class);
		if (annotation == null)
			throw new ClassIsNotAServiceException(cls);
		@Nonnull
		Class<?> serviceType = annotation.value();
		if (serviceType.equals(Void.class)) {
			Class<?>[] interfaces = cls.getInterfaces();
			if (interfaces.length == 1) {
				// annotations can not have null values
				serviceType = interfaces[0];
			} else if (interfaces.length == 0) {
				serviceType = cls;
			} else {
				throw new IllegalArgumentException("The type of the service implementation " + cls.getName()
						+ " is ambiguous, because the type is not given by the annotation and multiple interfaces are implemented."
						+ " Please specify which type this service should have.");
			}
		}
		this.registerClass(cls, serviceType);
	}

	private <T, X> void registerClass(@Nonnull Class<X> cls, @Nonnull Class<T> serviceType) {
		if (!serviceType.isAssignableFrom(cls)) {
			throw new IllegalArgumentException(
					"The specified service type " + serviceType.getName() + " is not assignable from " + cls.getName());
		}
		Class<? extends T> implementationClass = (Class<? extends T>) cls;
		ClassServiceProvider<T> classServiceProvider = new ClassServiceProvider<>(serviceType, implementationClass);
		this.register(classServiceProvider);
		this.extensions
				.forEach(ext -> ext.onRegister(classServiceProvider.getServiceDescription(), implementationClass));
	}

	@Override
	public void registerContextProvider(String key, StaticProvider<?> staticProvider) {
		this.contextLocator.registerContextProvider(key, staticProvider);
	}

	@Override
	public <T> T getService(Class<T> serviceType) {
		return this.getService(new ServiceDescriptionImpl<>(serviceType)).getService();
	}

	@Override
	public <T> ServiceHandle<T> getService(ServiceDescription<T> serviceDescription) {
		return this.getService(new ServiceConsumerImpl<>(this.getClass(), serviceDescription));
	}

	@Override
	public <T> ServiceHandle<T> getService(@Nonnull ServiceConsumer<T> serviceConsumer) {
		return this.getService(new ServiceCreation<>(serviceConsumer.getConsumerClass().getName()), serviceConsumer);
	}

	/**
	 * Get the service with the tracking of the dependency hierarchy.
	 * 
	 * @param dependentServiceCreation
	 * @param serviceConsumer
	 * @param <T>
	 *            the type of the service
	 * @return the service handle of the service
	 */
	<T> ServiceHandle<T> getService(@Nonnull ServiceCreation<?> dependentServiceCreation,
			@Nonnull ServiceConsumer<T> serviceConsumer) {
		ServiceProvider<T> provider = this.getServiceProvider(serviceConsumer.getServiceDescription(),
				dependentServiceCreation);
		ServiceInstantiationDescription<T> serviceInstantiationDescription = provider
				.getServiceInstantiationDescription(this.contextLocator, serviceConsumer);
		if (serviceInstantiationDescription == null) {
			throw new ServiceNotFoundException(serviceConsumer.getServiceDescription(), dependentServiceCreation);
		}
		return this.lookUpOrCreateService(dependentServiceCreation, provider, serviceInstantiationDescription);
	}

	@SuppressWarnings("unchecked")
	@CheckForNull
	private <T> ServiceHandle<T> lookUpService(@Nonnull ServiceProvider<T> serviceProvider,
			@Nonnull ServiceInstantiationDescription<T> serviceInstantiationDescription) {

		ServicePoolKey<?> servicePoolKey = new ServicePoolKey<>(serviceProvider, serviceInstantiationDescription);

		if (!this.servicePool.containsKey(servicePoolKey)) {
			return null;
		}
		return (ServiceHandle<T>) this.servicePool.get(servicePoolKey);
	}

	@SuppressWarnings("unchecked")
	private <T> Future<ServiceHandle<T>> createService(@Nonnull ServiceCreation<?> dependentServiceCreation,
			@Nonnull ServiceProvider<T> serviceProvider,
			@Nonnull ServiceInstantiationDescription<T> serviceInstantiationDescription) {
		ServicePoolKey<T> key = new ServicePoolKey<>(serviceProvider, serviceInstantiationDescription);
		synchronized (this.servicePool) {
			ServiceCreation<T> serviceCreation;
			if (this.serviceCreationInfos.containsKey(key)) {
				serviceCreation = (ServiceCreation<T>) this.serviceCreationInfos.get(key);
			} else {
				serviceCreation = new ServiceCreation<>(
						serviceInstantiationDescription.getImplementationClass().getName());

				serviceCreation.completableFuture = CompletableFuture.supplyAsync(() -> {
					SimpleServiceLocatorImpl tempLocator = new SimpleServiceLocatorImpl(this, serviceCreation);
					T service = serviceProvider.createService(tempLocator, serviceInstantiationDescription);
					tempLocator.destroy();
					ServiceHandle<T> serviceHandle = new ServiceHandleImpl<>(service);
					this.servicePool.put(key, serviceHandle);
					return serviceHandle;
				});

				this.serviceCreationInfos.put(key, serviceCreation);
			}
			serviceCreation.addDependent(dependentServiceCreation);

			return serviceCreation.completableFuture;
		}
	}

	private <T> ServiceHandle<T> lookUpOrCreateService(@Nonnull ServiceCreation<?> dependentServiceCreationInfo,
			@Nonnull ServiceProvider<T> serviceProvider,
			@Nonnull ServiceInstantiationDescription<T> serviceInstantiationDescription) {
		ServiceHandle<T> lookUpService = this.lookUpService(serviceProvider, serviceInstantiationDescription);
		if (lookUpService == null) {
			Future<ServiceHandle<T>> createService = this.createService(dependentServiceCreationInfo, serviceProvider,
					serviceInstantiationDescription);
			try {
				lookUpService = createService.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException(e);
			} catch (ExecutionException e) {
				if (e.getCause() instanceof RuntimeException) {
					throw (RuntimeException) e.getCause();
				}
				throw new IllegalStateException("Checked exception thrown", e);
			}
		}
		return lookUpService;
	}

	@Override
	public void inject(@Nonnull Object instance) {
		ServiceLocatorUtil.inject(instance, this);
	}

	@Override
	public void postConstruct(@Nonnull Object postConstructMe) {
		Util.postConstruct(postConstructMe);
	}

	/**
	 * This is the only method that gets ServiceProvider from the register and casts them to the correct return type
	 * 
	 * @param <T>
	 *            the type of the service
	 * @param serviceDescription
	 *            the description of the wanted service
	 * @return the ServiceProvider that can provide matching services for the given ServiceDescription
	 * @throws ServiceNotFoundException
	 *             if no service is found for the given ServiceDescription
	 */
	@Nonnull
	@SuppressWarnings("unchecked")
	private <T> ServiceProvider<T> getServiceProvider(ServiceDescription<T> serviceDescription,
			ServiceCreation<?> serviceCreation) {
		ServiceKey<T> serviceKey = new ServiceKey<>(serviceDescription);
		synchronized (this.register) {
			if (!this.register.containsKey(serviceKey))
				throw new ServiceNotFoundException(serviceDescription, serviceCreation);
			return (ServiceProvider<T>) this.register.get(serviceKey);
		}
	}

	@Override
	public <T> T createAndInitialize(@Nonnull Class<T> serviceClass) {
		return ServiceLocatorUtil.createAndInitialize(serviceClass, this);
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
