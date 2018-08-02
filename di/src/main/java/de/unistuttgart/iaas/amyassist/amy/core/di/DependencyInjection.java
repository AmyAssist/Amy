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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ConsumerFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.ClassProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ClassServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceHandle;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.ServiceProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.provider.SingletonServiceProvider;
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
public class DependencyInjection implements ServiceLocator, Configuration {
	/**
	 * A register which maps a service description to it's service provider.
	 */
	protected final Map<ServiceDescription<?>, ServiceProvider<?>> register;

	private final Map<ServicePoolKey<?>, ServiceHandle<?>> servicePool;

	protected Map<String, StaticProvider<?>> staticProviders;

	/**
	 * Creates a new Dependency Injection
	 */
	public DependencyInjection() {
		this.register = new HashMap<>();
		this.servicePool = new HashMap<>();
		this.staticProviders = new HashMap<>();

		this.registerContextProvider("class", new ClassProvider());
		this.addExternalService(ServiceLocator.class, this);
		this.addExternalService(Configuration.class, this);
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
	public synchronized <T> void register(Class<T> serviceType, ServiceProvider<T> serviceProvider) {
		this.register(Util.serviceDescriptionFor(serviceType), serviceProvider);
	}

	@Override
	public synchronized <T> void register(ServiceDescription<T> serviceDescription,
			ServiceProvider<T> serviceProvider) {
		if (this.hasServiceOfType(serviceDescription))
			throw new DuplicateServiceException();
		this.register.put(serviceDescription, serviceProvider);
	}

	@Override
	public synchronized void register(@Nonnull Class<?> cls) {
		Service annotation = cls.getAnnotation(Service.class);
		if (annotation == null)
			throw new ClassIsNotAServiceException(cls);
		Class<?> serviceType = annotation.value();
		if (serviceType.equals(Void.class)) {
			Class<?>[] interfaces = cls.getInterfaces();
			if (interfaces.length == 1) {
				serviceType = interfaces[0];
			} else if (interfaces.length == 0) {
				serviceType = cls;
			} else {
				throw new IllegalArgumentException("The type of the service implementation " + cls.getName()
						+ " is ambiguous, because the type is not given by the annotation and multiple interfaces are implemented."
						+ " Please specify which type this service should have.");
			}
		}

		if (this.hasServiceOfType(Util.serviceDescriptionFor(serviceType))) {
			throw new DuplicateServiceException();
		}
		if (!serviceType.isAssignableFrom(cls)) {
			throw new IllegalArgumentException(
					"The specified service type " + serviceType.getName() + " is not assignable from " + cls.getName());
		}

		ServiceProvider<?> p = new ClassServiceProvider<>(cls);
		this.register.put(Util.serviceDescriptionFor(serviceType), p);
	}

	@Override
	public synchronized void registerContextProvider(String key, StaticProvider<?> staticProvider) {
		this.staticProviders.put(key, staticProvider);
	}

	@Override
	public synchronized <T> void addExternalService(@Nonnull Class<T> serviceType, @Nonnull T externalService) {
		this.register(serviceType, new SingletonServiceProvider<>(externalService));
	}

	private boolean hasServiceOfType(ServiceDescription<?> serviceDescription) {
		return this.register.containsKey(serviceDescription);
	}

	@Override
	public <T> T getService(Class<T> serviceType) {
		return this.getService(Util.serviceDescriptionFor(serviceType)).getService();
	}

	@Override
	public <T> ServiceHandle<T> getService(ServiceDescription<T> serviceDescription) {
		ServiceProvider<T> provider = this.getServiceProvider(serviceDescription);
		ServiceImplementationDescription<T> serviceImplementationDescription = provider
				.getServiceImplementationDescription(this, new ServiceConsumer<T>() {

					@Override
					public Class<?> getConsumerClass() {
						return null;
					}

					@Override
					public ServiceDescription<T> getServiceDescription() {
						return serviceDescription;
					}
				});
		return this.lookUpOrCreateService(provider, serviceImplementationDescription);
	}

	@Override
	public <T> ServiceHandle<T> getService(@Nonnull ServiceConsumer<T> serviceConsumer) {
		ServiceProvider<T> provider = this.getServiceProvider(serviceConsumer.getServiceDescription());
		ServiceImplementationDescription<T> serviceImplementationDescription = provider
				.getServiceImplementationDescription(this, serviceConsumer);
		return this.lookUpOrCreateService(provider, serviceImplementationDescription);
	}

	@CheckForNull
	private <T> ServiceHandle<T> lookUpService(@Nonnull ServiceProvider<T> serviceProvider,
			@Nonnull ServiceImplementationDescription<T> serviceImplementationDescription) {
		Map<String, Object> context = serviceImplementationDescription.getContext();

		ServicePoolKey<?> servicePoolKey = new ServicePoolKey<>(serviceProvider, new HashMap<>(context));

		if (!this.servicePool.containsKey(servicePoolKey)) {
			return null;
		}
		return (ServiceHandle<T>) this.servicePool.get(servicePoolKey);
	}

	private <T> ServiceHandle<T> createService(@Nonnull ServiceProvider<T> serviceProvider,
			@Nonnull ServiceImplementationDescription<T> serviceImplementationDescription) {
		ServiceHandle<T> service = serviceProvider.getService(this, serviceImplementationDescription);
		ServicePoolKey<T> key = new ServicePoolKey<>(serviceProvider, serviceImplementationDescription.getContext());
		this.servicePool.put(key, service);
		return service;
	}

	private <T> ServiceHandle<T> lookUpOrCreateService(@Nonnull ServiceProvider<T> serviceProvider,
			@Nonnull ServiceImplementationDescription<T> serviceImplementationDescription) {
		ServiceHandle<T> lookUpService = this.lookUpService(serviceProvider, serviceImplementationDescription);
		if (lookUpService == null) {
			lookUpService = this.createService(serviceProvider, serviceImplementationDescription);
		}
		return lookUpService;
	}

	@Override
	public void inject(@Nonnull Object instance) {
		Class<? extends Object> classOfInstance = instance.getClass();
		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(classOfInstance, Reference.class);
		for (Field field : dependencyFields) {
			ServiceDescription<?> serviceDescription = Util.serviceDescriptionFor(field);
			Class<?> declaredClass = field.getDeclaringClass();
			Object object = this.getService(ConsumerFactory.build(declaredClass, serviceDescription)).getService();
			Util.inject(instance, object, field);
		}
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
	private <T> ServiceProvider<T> getServiceProvider(ServiceDescription<T> serviceDescription) {
		if (!this.register.containsKey(serviceDescription))
			throw new ServiceNotFoundException(serviceDescription);
		return (ServiceProvider<T>) this.register.get(serviceDescription);
	}

	@Override
	public StaticProvider<?> getContextProvider(@Nonnull String contextProviderType) {
		if (!this.staticProviders.containsKey(contextProviderType))
			throw new NoSuchElementException(contextProviderType);
		return this.staticProviders.get(contextProviderType);
	}

	@Override
	public <T> T createAndInitialize(@Nonnull Class<T> serviceClass) {
		if (!Util.isValidServiceClass(serviceClass)) {
			throw new IllegalArgumentException(
					"There is a problem with the class " + serviceClass.getName() + ". It can't be used as a Service");
		}
		T newInstance;
		try {
			newInstance = serviceClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(
					"The constructor of " + serviceClass.getName() + " should have been checked", e);
		}
		this.inject(newInstance);
		this.postConstruct(newInstance);

		return newInstance;
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
