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

package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.NTuple;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

/**
 * A ClassServiceProvider which provides service instances for a class
 * 
 * @author Leon Kiefer
 * @param <T>
 *            type of the provided service
 */
public class ClassServiceProvider<T> implements ServiceProvider<T> {
	private Class<? extends T> cls;
	/**
	 * A register which contains all dependencies
	 */
	private Set<ServiceConsumer<?>> dependencies = new HashSet<>();
	private Set<InjectionPoint> injectionPoints = new HashSet<>();
	private Set<String> requiredContextIdentifiers = new HashSet<>();
	private final NTuple<String> contextType;
	private final NTuple<ContextInjectionPoint> contextInjectionPoints;
	private Map<NTuple<?>, ServiceHandle<T>> serviceInstances = new HashMap<>();

	/**
	 * The container of Services to be used to pass Services around the DI.
	 * 
	 * @author Leon Kiefer
	 * @param <T>
	 *            the type of the service
	 */
	static class ServiceHandle<T> {
		@Nonnull
		private T service;

		public ServiceHandle(T service) {
			this.service = service;
		}

		/**
		 * Get the service
		 * 
		 * @return the service of this service handle
		 */
		@Nonnull
		T getService() {
			return this.service;
		}

		int refcount = 0;
	}

	/**
	 * 
	 * @param cls
	 *            the service implementation class
	 */
	public ClassServiceProvider(@Nonnull Class<? extends T> cls) {
		if (!Util.isValidServiceClass(cls))
			throw new IllegalArgumentException(
					"There is a problem with the class " + cls.getName() + ". It can't be used as a Service");
		this.cls = cls;

		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(cls, Reference.class);
		for (Field field : dependencyFields) {
			InjectionPoint injectionPoint = new InjectionPoint(field);
			this.injectionPoints.add(injectionPoint);

			ServiceConsumer<?> serviceConsumer = injectionPoint.getServiceConsumer();
			this.dependencies.add(serviceConsumer);
		}

		Field[] contextFields = FieldUtils.getFieldsWithAnnotation(cls, Context.class);
		this.contextType = new NTuple<>(contextFields.length);
		this.contextInjectionPoints = new NTuple<>(contextFields.length);
		int i = 0;
		for (Field field : contextFields) {
			ContextInjectionPoint injectionPoint = new ContextInjectionPoint(field);
			this.requiredContextIdentifiers.add(injectionPoint.getContextIdentifier());
			this.contextType.set(i, injectionPoint.getContextIdentifier());
			this.contextInjectionPoints.set(i, injectionPoint);
			i++;
		}
	}

	private NTuple<?> getContextTuple(Map<String, ?> context) {
		if (context == null) {
			return new NTuple<>(this.contextType.n);
		}
		return this.contextType.map(context::get);
	}

	@Override
	public T getService(Map<ServiceConsumer<?>, ServiceFactory<?>> resolvedDependencies, Map<String, ?> context) {
		NTuple<?> contextTuple = this.getContextTuple(context);
		if (this.serviceInstances.containsKey(contextTuple)) {
			ClassServiceProvider.ServiceHandle<T> serviceData = this.serviceInstances.get(contextTuple);
			serviceData.refcount++;
			return serviceData.getService();
		}

		ServiceHandle<T> serviceData = new ServiceHandle<>(this.createService(resolvedDependencies, contextTuple));
		serviceData.refcount = 1;
		this.serviceInstances.put(contextTuple, serviceData);
		return serviceData.getService();
	}

	/**
	 * Create a new Service instance with the given resolved dependencies and context
	 * 
	 * @param resolvedDependencies
	 * @param contextTuple
	 *            the context
	 * @return a newly created service instance
	 */
	private T createService(Map<ServiceConsumer<?>, ServiceFactory<?>> resolvedDependencies, NTuple<?> contextTuple) {
		T serviceInstance = this.createService();
		for (InjectionPoint injectionPoint : this.injectionPoints) {
			ServiceFactory<?> serviceFactory = resolvedDependencies.get(injectionPoint.getServiceConsumer());
			injectionPoint.inject(serviceInstance, serviceFactory.build());
		}
		for (int i = 0; i < this.contextInjectionPoints.n; i++) {
			ContextInjectionPoint contextInjectionPoint = this.contextInjectionPoints.get(i);
			contextInjectionPoint.inject(serviceInstance, contextTuple.get(i));
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
	public Set<String> getRequiredContextIdentifiers() {
		return Collections.unmodifiableSet(this.requiredContextIdentifiers);
	}

	@Override
	public Set<ServiceConsumer<?>> getDependencies() {
		return Collections.unmodifiableSet(this.dependencies);
	}

	@Override
	public void dispose(T service) {
		for (Entry<NTuple<?>, ServiceHandle<T>> e : this.serviceInstances.entrySet()) {
			ServiceHandle<T> serviceData = e.getValue();
			if (serviceData.getService() == service) {
				serviceData.refcount--;
				if (serviceData.refcount == 0) {
					this.destroy(e.getKey());
				}
				return;
			}
		}
		throw new IllegalArgumentException();
	}

	private void destroy(NTuple<?> contextTuple) {
		ServiceHandle<T> remove = this.serviceInstances.remove(contextTuple);
		Util.preDestroy(remove);
	}
}
