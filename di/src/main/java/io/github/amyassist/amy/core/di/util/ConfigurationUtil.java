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

package io.github.amyassist.amy.core.di.util;

import javax.annotation.Nonnull;

import io.github.amyassist.amy.core.di.Configuration;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.di.exception.ClassIsNotAServiceException;
import io.github.amyassist.amy.core.di.provider.ClassServiceProvider;

/**
 * Utility for the registration of classes with the ClassServiceProvider.
 * 
 * @author Leon Kiefer
 */
public class ConfigurationUtil {

	private ConfigurationUtil() {
		// hide constructor
	}

	/**
	 * 
	 * @param cls
	 *            the implementation class for which the ClassServiceProvider should be created
	 * @return the ClassServiceProvider which provides Services of the given class
	 * @throws ClassIsNotAServiceException
	 *             if the @Service annotation is not present on the given class
	 * @see Configuration#register(Class)
	 */
	public static ClassServiceProvider<?> getClassServiceProvider(@Nonnull Class<?> cls) {
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
		return registerClass(cls, serviceType);
	}

	private static <T, X> ClassServiceProvider<T> registerClass(@Nonnull Class<X> cls, @Nonnull Class<T> serviceType) {
		if (!serviceType.isAssignableFrom(cls)) {
			throw new IllegalArgumentException(
					"The specified service type " + serviceType.getName() + " is not assignable from " + cls.getName());
		}
		@SuppressWarnings("unchecked")
		Class<? extends T> implementationClass = (Class<? extends T>) cls;
		return new ClassServiceProvider<>(serviceType, implementationClass);
	}
}
