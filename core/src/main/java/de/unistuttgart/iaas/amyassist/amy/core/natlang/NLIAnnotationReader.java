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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * This class is responsible to read the annotations of a given class
 *
 * @author Leon Kiefer, Felix Burk, Lars Buttgereit
 */
public class NLIAnnotationReader {

	private NLIAnnotationReader() {
		// hide constructor
	}

	/**
	 * Get's the methods annotated with {@link Intent}
	 *
	 * @param cls
	 *            The class of which to get the grammars
	 * @return a List of methods
	 * @throws IllegalArgumentException
	 *             if no {@link SpeechCommand} Annotation is present
	 * 
	 */
	public static Set<Method> getValidIntentMethods(Class<?> cls) {
		if (cls.isAnnotationPresent(SpeechCommand.class)) {
			Set<Method> validMethods = new HashSet<>();
			Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(cls, Intent.class);
			for (Method method : methodsWithAnnotation) {
				assertValid(method);
				validMethods.add(method);
			}
			return validMethods;
		}
		throw new IllegalArgumentException("class has no @SpeechCommand annotation");
	}

	/**
	 * returns fitting method
	 *
	 * @param cls
	 *            containing the method
	 * @param entityId
	 *            value inside annotation
	 * @return fitting method
	 */
	public static Method getValidEnityProviderMethod(Class<?> cls, String entityId) {
		Method[] methodsWithEntityProviderAnnotation = MethodUtils.getMethodsWithAnnotation(cls, EntityProvider.class);
		for (Method m : methodsWithEntityProviderAnnotation) {
			assertValidEntityProviderAnnotation(m);
			EntityProvider plch = m.getAnnotation(EntityProvider.class);
			if (plch.value().equals(entityId)) {
				return m;
			}
		}
		Method[] methodsWithEntityProvidersAnnotation = MethodUtils.getMethodsWithAnnotation(cls,
				EntityProviders.class);
		for (Method m : methodsWithEntityProvidersAnnotation) {
			assertValidEntityProviderAnnotation(m);
			EntityProviders plch = m.getAnnotation(EntityProviders.class);
			for (EntityProvider provider : plch.value()) {
				if (provider.value().equals(entityId)) {
					return m;
				}

			}
		}

		return null;
	}

	/**
	 * Check if method is a valid annotated method. Annotated methods must not throw Exceptions.
	 *
	 * @param method
	 *            the method that should be a NLIMethod
	 *
	 * @throws IllegalArgumentException
	 *             in case of wrong parameter types, the method throws exceptions or the return type is not a String
	 *
	 */
	public static void assertValid(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1 || !parameterTypes[0].equals(Map.class)) {
			throw new IllegalArgumentException("The method " + method.toString()
					+ " does not have the correct parameter type. It should be a Map.");
		}
		if (!method.getReturnType().equals(String.class)) {
			throw new IllegalArgumentException("The returntype of a method annotated with @Intent should be String.");
		}
		if (method.getExceptionTypes().length > 0) {
			throw new IllegalArgumentException("The method annotated with @Intent should should not throw exceptions.");
		}
	}

	/**
	 * calls the given method and handles possible exceptions
	 *
	 * @param method
	 *            the method to call
	 * @param instance
	 *            the instance of which to call the method
	 * @param arg
	 *            the arguments to the NLI method
	 * @return the result of the call to the NLI method
	 * @throws IllegalArgumentException
	 *             if the annotated methods not valid
	 */
	public static String callNLIMethod(@Nonnull Method method, @Nonnull Object instance, Object[] arg) {
		assertValid(method);
		try {
			method.setAccessible(true);
			return (String) method.invoke(instance, arg);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("tryed to invoke method " + method + " but got an error", e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();

			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new IllegalArgumentException("method " + method + " throw an exception", cause);
		}
	}

	/**
	 * calls the given method and handles possible exceptions
	 *
	 * @param method
	 *            the method to call
	 * @param instance
	 *            the instance of which to call the method
	 * @return a list with all custom grammars
	 * @throws IllegalArgumentException
	 *             if the annotated methods not valid
	 */
	public static List<String> callNLIGetEntityProviderMethod(@Nonnull Method method, @Nonnull Object instance) {
		assertValidEntityProviderAnnotation(method);
		try {
			method.setAccessible(true);
			return (List<String>) method.invoke(instance);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("tryed to invoke method " + method + " but got an error", e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();

			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new IllegalArgumentException("method " + method + " throw an exception", cause);
		}
	}

	/**
	 * Check if method is a valid annotated method from type EntityProvider. Annotated methods must not throw
	 * Exceptions.
	 *
	 * @param method
	 *            the method that should be a EntityProvider Method
	 *
	 * @throws IllegalArgumentException
	 *             in case of wrong parameter types, the method throws exceptions or the return type is not a String
	 *
	 */
	public static void assertValidEntityProviderAnnotation(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 0) {
			throw new IllegalArgumentException("The method " + method.toString() + " should not have paramters");
		}
		if (!method.getReturnType().equals(List.class)) {
			throw new IllegalArgumentException(
					"The returntype of a method annotated with @EntityProvider should be List<String>.");
		}
		if (method.getExceptionTypes().length > 0) {
			throw new IllegalArgumentException(
					"The method annotated with @EntityProvider should should not throw exceptions.");
		}
	}

}
