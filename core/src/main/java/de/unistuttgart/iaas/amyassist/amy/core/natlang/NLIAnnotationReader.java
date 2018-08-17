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
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Intent;


/**
 * This class is responsible to read the annotations of a given class
 * 
 * @author Leon Kiefer, Felix Burk
 */
public class NLIAnnotationReader {
	
	private static Logger logger = LoggerFactory.getLogger(NLIAnnotationReader.class);

	private NLIAnnotationReader() {
		// hide constructor
	}

	/**
	 * Get's the methods annotated with {@link Grammar}
	 * 
	 * @param cls
	 *            The class of which to get the grammars
	 * @return a List of grammars
	 * @throws IllegalArgumentException
	 *             if a method annotated with {@link Grammar} is not a valid NLIMethod
	 */
	@Deprecated
	public static Set<Method> getValidNLIMethods(Class<?> cls) {
		Set<Method> validMethods = new HashSet<>();
		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(cls, Grammar.class);
		for (Method method : methodsWithAnnotation) {
			assertValid(method);
			validMethods.add(method);
		}
		return validMethods;
	}
	
	/**
	 * Get's the methods annotated with {@link Intent}
	 * 
	 * @param cls
	 *            The class of which to get the grammars
	 * @return a List of grammars
	 * @throws IllegalArgumentException
	 *             if a method annotated with {@link Grammar} is not a valid NLIMethod
	 */
	public static Set<Method> getValidIntentMethods(Class<?> cls) {
		Set<Method> validMethods = new HashSet<>();
		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(cls, Intent.class);
		for (Method method : methodsWithAnnotation) {
			assertValid(method);
			validMethods.add(method);
		}
		return validMethods;
	}

	/**
	 * Check if method is a valid annotated method. Annotated methods must not throw Exceptions.
	 * 
	 * @param method
	 *            the method that should be a NLIMethod
	 *            
	 * @throws IllegalArgumentException 
	 * 			  in case of wrong parameter types, the method throws exceptions
	 * 			  or the return type is not a String
	 * 
	 */
	public static void assertValid(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		for(Class<?> cls : parameterTypes) {
			logger.error(cls.getTypeName());
		}
		if (parameterTypes.length != 1
				|| !parameterTypes[0].getTypeName().equals("java.util.Map")) {
			throw new IllegalArgumentException("The method " + method.toString()
					+ " does not have the correct parameter type. It should be String[].");
		}
		if (!method.getReturnType().equals(String.class)) {
			throw new IllegalArgumentException("The returntype of a method annotated with @Grammar should be String.");
		}
		if (method.getExceptionTypes().length > 0) {
			throw new IllegalArgumentException(
					"The method annotated with @Grammar should should not throw exceptions.");
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
}
