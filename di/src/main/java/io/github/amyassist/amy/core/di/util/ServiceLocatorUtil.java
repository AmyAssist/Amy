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

package de.unistuttgart.iaas.amyassist.amy.core.di.util;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.SimpleServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ConsumerFactory;

/**
 * Implementation of operations using the SimpleServiceLocator.
 * 
 * @author Leon Kiefer
 */
public class ServiceLocatorUtil {
	private ServiceLocatorUtil() {
		// hide constructor
	}

	/**
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @param locator
	 * @return the created instance of the given class
	 * @see ServiceLocator#createAndInitialize(Class)
	 */
	public static <T> T createAndInitialize(@Nonnull Class<T> serviceClass, @Nonnull SimpleServiceLocator locator) {
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
		inject(newInstance, locator);
		Util.postConstruct(newInstance);

		return newInstance;
	}

	/**
	 * @param instance
	 * @param locator
	 * @see ServiceLocator#inject(Object)
	 */
	public static void inject(@Nonnull Object instance, @Nonnull SimpleServiceLocator locator) {
		Class<? extends Object> classOfInstance = instance.getClass();
		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(classOfInstance, Reference.class);
		for (Field field : dependencyFields) {
			ServiceDescription<?> serviceDescription = Util.serviceDescriptionFor(field);
			serviceDescription.getAnnotations().removeIf(annotation -> annotation instanceof Reference);
			Class<?> declaredClass = field.getDeclaringClass();
			Object object = locator.getService(ConsumerFactory.build(declaredClass, serviceDescription)).getService();
			Util.inject(instance, object, field);
		}
	}
}
