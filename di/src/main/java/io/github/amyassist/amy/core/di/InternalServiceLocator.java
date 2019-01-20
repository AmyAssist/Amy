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

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;
import io.github.amyassist.amy.core.di.context.provider.ClassProvider;
import io.github.amyassist.amy.core.di.context.provider.StaticProvider;
import io.github.amyassist.amy.core.di.exception.DuplicateServiceException;
import io.github.amyassist.amy.core.di.exception.ServiceNotFoundException;
import io.github.amyassist.amy.core.di.provider.*;

/**
 * This is the core of the dependency injection. It contains the maps with ServiceProviders and instantiated Services.
 * It is responsible for managing the services and creating new Services by need. All Operations are Thread safe and be
 * mostly processed concurrently.
 * 
 * @author Leon Kiefer
 */
@ParametersAreNullableByDefault
public class InternalServiceLocator implements SimpleServiceLocator {
	/**
	 * A register which maps a service description to it's service provider.
	 */
	private final Map<ServiceKey<?>, ServiceProvider<?>> register;

	private final Map<ServicePoolKey<?>, InternalServiceHandle<?>> servicePool;

	private final Map<ServicePoolKey<?>, ServiceCreation<?>> serviceCreationInfos;

	@Nonnull
	private final ContextLocatorImpl contextLocator;

	private Consumer<ClassServiceProvider<?>> onRegister;

	/**
	 * 
	 * @param onRegister
	 */
	public InternalServiceLocator(Consumer<ClassServiceProvider<?>> onRegister) {
		this.onRegister = onRegister;
		this.register = new ConcurrentHashMap<>();
		this.servicePool = new ConcurrentHashMap<>();
		this.serviceCreationInfos = new ConcurrentHashMap<>();
		this.contextLocator = new ContextLocatorImpl();

		this.registerContextProvider("class", new ClassProvider());
		this.register(new ServiceLocatorProvider(this));
		this.register(new ConfigurationProvider(this));
	}

	public <T> void register(@Nonnull ServiceProvider<T> serviceProvider) {
		ServiceDescription<T> serviceDescription = serviceProvider.getServiceDescription();
		ServiceKey<T> serviceKey = new ServiceKey<>(serviceDescription);
		synchronized (this.register) {
			if (this.register.containsKey(serviceKey))
				throw new DuplicateServiceException(serviceDescription);
			this.register.put(serviceKey, serviceProvider);
		}
	}

	public <T> void onRegister(ClassServiceProvider<T> classServiceProvider) {
		this.onRegister.accept(classServiceProvider);
	}

	public void registerContextProvider(String key, StaticProvider<?> staticProvider) {
		this.contextLocator.registerContextProvider(key, staticProvider);
	}

	@Override
	public <T> ServiceHandle<T> getService(@Nonnull ServiceConsumer<T> serviceConsumer) {
		return this.getService(new ServiceCreation<>("[V]" + serviceConsumer.getConsumerClass().getName()), serviceConsumer);
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
		return this.claimService(dependentServiceCreation, provider, serviceInstantiationDescription);
	}

	@SuppressWarnings("unchecked")
	private <T> Future<ServiceHandle<T>> lookUpOrCreateService(@Nonnull ServiceCreation<?> dependentServiceCreation,
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
					InternalServiceHandle<T> serviceHandle = new InternalServiceHandle<>(service, serviceCreation);
					this.servicePool.put(key, serviceHandle);
					return new ServiceHandleImpl<>(service);
				});

				this.serviceCreationInfos.put(key, serviceCreation);
			}
			serviceCreation.addDependent(dependentServiceCreation);

			return serviceCreation.completableFuture;
		}
	}

	/**
	 * This modifies the state of the dependency graph.
	 * 
	 * @param dependentServiceCreationInfo
	 *            the ServiceCreation which claims the Service
	 * @param serviceProvider
	 *            the serviceProvider used to create the service if needed
	 * @param serviceInstantiationDescription
	 *            the service which should be claimed
	 * @return the claimed Service
	 * @param <T>
	 *            the type of the service
	 */
	private <T> ServiceHandle<T> claimService(@Nonnull ServiceCreation<?> dependentServiceCreationInfo,
			@Nonnull ServiceProvider<T> serviceProvider,
			@Nonnull ServiceInstantiationDescription<T> serviceInstantiationDescription) {
		Future<ServiceHandle<T>> createService = this.lookUpOrCreateService(dependentServiceCreationInfo, serviceProvider,
				serviceInstantiationDescription);
		try {
			return createService.get();
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
}
