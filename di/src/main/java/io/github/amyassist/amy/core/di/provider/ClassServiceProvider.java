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

package io.github.amyassist.amy.core.di.provider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.reflect.FieldUtils;

import io.github.amyassist.amy.core.di.ContextLocator;
import io.github.amyassist.amy.core.di.ServiceDescription;
import io.github.amyassist.amy.core.di.ServiceInstantiationDescription;
import io.github.amyassist.amy.core.di.SimpleServiceLocator;
import io.github.amyassist.amy.core.di.annotation.Context;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.consumer.ServiceConsumer;
import io.github.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;
import io.github.amyassist.amy.core.di.runtime.ServiceInstantiationDescriptionImpl;
import io.github.amyassist.amy.core.di.util.Util;

/**
 * A ClassServiceProvider which provides service instances for a class
 * 
 * @author Leon Kiefer
 * @param <T>
 *            type of the provided service
 */
public class ClassServiceProvider<T> implements ServiceProvider<T> {
	@Nonnull
	private final Class<? extends T> cls;
	@Nonnull
	private final ServiceDescription<T> serviceDescription;

	private final Set<InjectionPoint> injectionPoints = new HashSet<>();
	private final Set<ContextInjectionPoint> contextInjectionPoints = new HashSet<>();

	/**
	 * 
	 * @param serviceType
	 *            the type of the service that should be provided
	 * @param cls
	 *            the service implementation class
	 */
	public ClassServiceProvider(@Nonnull Class<T> serviceType, @Nonnull Class<? extends T> cls) {
		this(new ServiceDescriptionImpl<>(serviceType), cls);
	}

	/**
	 * The class which is the implementation of the Service.
	 * 
	 * @return the class
	 */
	@Nonnull
	public Class<? extends T> getImplementationClass() {
		return this.cls;
	}

	/**
	 * 
	 * @param serviceDescription
	 *            the description of the service that should be provided
	 * @param cls
	 *            the service implementation class
	 */
	public ClassServiceProvider(@Nonnull ServiceDescription<T> serviceDescription, @Nonnull Class<? extends T> cls) {
		this.serviceDescription = serviceDescription;
		if (!Util.isValidServiceClass(cls))
			throw new IllegalArgumentException(
					"There is a problem with the class " + cls.getName() + ". It can't be used as a Service");
		this.cls = cls;

		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(cls, Reference.class);
		for (Field field : dependencyFields) {
			this.injectionPoints.add(new InjectionPoint(field));
		}

		Field[] contextFields = FieldUtils.getFieldsWithAnnotation(cls, Context.class);
		for (Field field : contextFields) {
			this.contextInjectionPoints.add(new ContextInjectionPoint(field));
		}
	}

	@Override
	public @Nonnull ServiceDescription<T> getServiceDescription() {
		return this.serviceDescription;
	}

	@Override
	public ServiceInstantiationDescription<T> getServiceInstantiationDescription(@Nonnull ContextLocator locator,
			@Nonnull ServiceConsumer<T> serviceConsumer) {
		HashMap<String, Object> map = new HashMap<>();
		for (ContextInjectionPoint c : this.contextInjectionPoints) {
			String key = c.getContextIdentifier();
			map.put(key, locator.getContextProvider(key).getContext(serviceConsumer));
		}

		return new ServiceInstantiationDescriptionImpl<>(serviceConsumer.getServiceDescription(), map, this.cls);
	}

	@Override
	public @Nonnull T createService(@Nonnull SimpleServiceLocator locator,
			@Nonnull ServiceInstantiationDescription<T> serviceInstantiationDescription) {

		@Nonnull
		T serviceInstance = this.createService();
		for (InjectionPoint injectionPoint : this.injectionPoints) {
			ServiceConsumer<?> serviceConsumer = injectionPoint.getServiceConsumer();
			ServiceHandle<?> serviceHandle = locator.getService(serviceConsumer);
			injectionPoint.inject(serviceInstance, serviceHandle.getService());
		}

		Map<String, Object> context = serviceInstantiationDescription.getContext();
		for (ContextInjectionPoint contextInjectionPoint : this.contextInjectionPoints) {
			contextInjectionPoint.inject(serviceInstance, context.get(contextInjectionPoint.getContextIdentifier()));
		}

		Util.postConstruct(serviceInstance);
		return serviceInstance;
	}

	private T createService() {
		try {
			return this.cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("The constructor of " + this.cls.getName() + " should have been checked",
					e);
		}
	}

	@Override
	public void dispose(@Nonnull T service,
			@Nonnull ServiceInstantiationDescription<T> serviceInstantiationDescription) {
		Util.preDestroy(service);
	}

}
