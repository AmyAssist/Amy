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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
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

	private Set<InjectionPoint> injectionPoints = new HashSet<>();
	private final NTuple<String> contextType;
	private final NTuple<ContextInjectionPoint> contextInjectionPoints;
	private Map<NTuple<?>, ServiceHandle<T>> serviceInstances = new HashMap<>();

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
			this.injectionPoints.add(new InjectionPoint(field));
		}

		Field[] contextFields = FieldUtils.getFieldsWithAnnotation(cls, Context.class);
		this.contextType = new NTuple<>(contextFields.length);
		this.contextInjectionPoints = new NTuple<>(contextFields.length);
		int i = 0;
		for (Field field : contextFields) {
			ContextInjectionPoint injectionPoint = new ContextInjectionPoint(field);
			this.contextType.set(i, injectionPoint.getContextIdentifier());
			this.contextInjectionPoints.set(i, injectionPoint);
			i++;
		}
	}

	private NTuple<?> getContextTuple(ServiceLocator locator, ServiceConsumer<T> consumer) {
		return this.contextType.map(locator::getContextProvider)
				.map(provider -> provider.getContext(consumer.getConsumerClass()));
	}

	@Override
	public ServiceHandle<T> getService(ServiceLocator locator, ServiceConsumer<T> consumer) {

		NTuple<?> contextTuple = this.getContextTuple(locator, consumer);
		if (this.serviceInstances.containsKey(contextTuple)) {
			return this.serviceInstances.get(contextTuple);
		}

		ServiceHandle<T> serviceData = new ServiceHandleImpl<>(this.createService(locator, contextTuple));
		this.serviceInstances.put(contextTuple, serviceData);
		return serviceData;
	}

	/**
	 * Create a new Service instance with the given resolved dependencies and context
	 * 
	 * @param locator
	 * @param contextTuple
	 *            the context
	 * @return a newly created service instance
	 */
	private T createService(ServiceLocator locator, NTuple<?> contextTuple) {
		T serviceInstance = this.createService();
		for (InjectionPoint injectionPoint : this.injectionPoints) {
			ServiceConsumer<?> serviceConsumer = injectionPoint.getServiceConsumer();
			ServiceHandle<?> serviceHandle = locator.getService(serviceConsumer);
			injectionPoint.inject(serviceInstance, serviceHandle.getService());
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
	public void dispose(ServiceHandle<T> service) {
		if (this.serviceInstances.containsValue(service)) {
			for (Entry<NTuple<?>, ServiceHandle<T>> e : this.serviceInstances.entrySet()) {
				ServiceHandle<T> serviceData = e.getValue();
				if (serviceData == service) {
					this.destroy(e.getKey());
					return;
				}
			}
		}
		throw new IllegalArgumentException();
	}

	private void destroy(NTuple<?> contextTuple) {
		ServiceHandle<T> remove = this.serviceInstances.remove(contextTuple);
		Util.preDestroy(remove);
	}
}
