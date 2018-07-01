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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.NTuple;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

/**
 * A ClassServiceProvider which provides service instances for a class
 * 
 * @author Leon Kiefer
 * @param <T>
 *            type of the provided service
 */
public class ClassServiceProvider<T> extends ClassServiceProviderWithoutDependencies<T> {
	private final Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

	/**
	 * A register which contains all dependencies
	 */
	private Collection<ServiceDescription<?>> dependencies = new HashSet<>();
	private Collection<InjetionPoint> injetionPoints = new HashSet<>();
	private Collection<String> requiredContextIdentifiers = new HashSet<>();
	private final NTuple<String> contextType;
	private final NTuple<ContextInjectionPoint> contextInjectionPoints;
	private Map<NTuple<?>, T> serviceInstances = new HashMap<>();

	@Override
	public Collection<String> getRequiredContextIdentifiers() {
		return Collections.unmodifiableCollection(this.requiredContextIdentifiers);
	}

	@Override
	public Collection<ServiceDescription<?>> getDependencies() {
		return Collections.unmodifiableCollection(this.dependencies);
	}

	/**
	 * 
	 * @param cls
	 *            the service implementation class
	 */
	public ClassServiceProvider(@Nonnull Class<? extends T> cls) {
		super(cls);
		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(cls, Reference.class);
		for (Field field : dependencyFields) {
			InjetionPoint injetionPoint = new InjetionPoint(field);
			this.injetionPoints.add(injetionPoint);
			ServiceDescription<?> dependency = injetionPoint.getServiceDescription();
			if (this.dependencies.contains(dependency)) {
				this.logger.warn("The Service {} have a duplicate dependeny on {}", cls.getName(),
						dependency.getServiceType().getName());
			} else {
				this.dependencies.add(dependency);
			}
		}

		Field[] contextFields = FieldUtils.getFieldsWithAnnotation(cls, Context.class);
		this.contextType = new NTuple<>(contextFields.length);
		this.contextInjectionPoints = new NTuple<>(contextFields.length);
		int i = 0;
		for (Field field : contextFields) {
			ContextInjectionPoint injetionPoint = new ContextInjectionPoint(field);
			this.requiredContextIdentifiers.add(injetionPoint.getContextIdentifier());
			this.contextType.set(i, injetionPoint.getContextIdentifier());
			this.contextInjectionPoints.set(i, injetionPoint);
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
	public T getService(Map<ServiceDescription<?>, ServiceFactory<?>> resolvedDependencies, Map<String, ?> context) {
		NTuple<?> contextTuple = this.getContextTuple(context);
		if (this.serviceInstances.containsKey(contextTuple)) {
			return this.serviceInstances.get(contextTuple);
		}

		T createdService = this.createService(resolvedDependencies, contextTuple);
		this.serviceInstances.put(contextTuple, createdService);
		return createdService;
	}

	/**
	 * Create a new Service instance with the given resolved dependencies and context
	 * 
	 * @param resolvedDependencies
	 * @param contextTuple
	 *            the context
	 * @return a newly created service instance
	 */
	private T createService(Map<ServiceDescription<?>, ServiceFactory<?>> resolvedDependencies,
			NTuple<?> contextTuple) {
		T serviceInstance = this.createService();
		for (InjetionPoint injetionPoint : this.injetionPoints) {
			ServiceFactory<?> serviceFactory = resolvedDependencies.get(injetionPoint.getServiceDescription());
			injetionPoint.inject(serviceInstance, serviceFactory.build());
		}
		for (int i = 0; i < this.contextInjectionPoints.n; i++) {
			ContextInjectionPoint contextInjectionPoint = this.contextInjectionPoints.get(i);
			contextInjectionPoint.inject(serviceInstance, contextTuple.get(i));
		}

		Util.postConstruct(serviceInstance);
		return serviceInstance;
	}

	@Override
	public Class<?> getConsumerClass() {
		return this.cls;
	}
}
