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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PreDestroy;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.runtime.ServiceDescriptionImpl;

/**
 * Util for checks and java reflection
 * 
 * @author Leon Kiefer
 */
public class Util {
	private Util() {
		// hide constructor
	}

	/**
	 * Checks if the given class can be used as a Service. There for it must be a not abstract class with a default
	 * constructor.
	 * 
	 * @param cls
	 *            the class to be checked
	 * @return true ig the given class is a valid service class
	 */
	public static boolean isValidServiceClass(@Nonnull Class<?> cls) {
		if (!hasValidConstructors(cls) || cls.isArray() || cls.isInterface()
				|| Modifier.isAbstract(cls.getModifiers())) {
			return false;
		}

		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(cls, Reference.class);
		for (Field field : dependencyFields) {
			if (field.isAnnotationPresent(Context.class)) {
				return false;
			}
		}

		Method[] postConstructMethod = MethodUtils.getMethodsWithAnnotation(cls, PostConstruct.class, true, true);
		for (Method m : postConstructMethod) {
			if (!isValidAnnotatedMethod(m)) {
				return false;
			}
		}

		Method[] preDestroyMethod = MethodUtils.getMethodsWithAnnotation(cls, PreDestroy.class, true, true);
		for (Method m : preDestroyMethod) {
			if (!isValidAnnotatedMethod(m)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * check if default constructor exists and is accessible
	 * 
	 * @param cls
	 *            The class to check
	 * @return Whether the default constructor is present.
	 */
	public static boolean hasValidConstructors(@Nonnull Class<?> cls) {
		if (cls.getConstructors().length != 1) {
			return false;
		}

		try {
			cls.getConstructor();
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	/**
	 * Call the Methods annotated with {@link PostConstruct} on the given instance
	 * 
	 * @param instance
	 *            the instance to post construct
	 */
	public static void postConstruct(@Nonnull Object instance) {
		callAnnotatedMethods(instance, PostConstruct.class);
	}

	/**
	 * Call the Methods annotated with {@link PreDestroy} on the given instance
	 * 
	 * @param destroyMe
	 *            the instance to destroy
	 */
	public static void preDestroy(@Nonnull Object destroyMe) {
		callAnnotatedMethods(destroyMe, PreDestroy.class);
	}

	/**
	 * 
	 * @param instance
	 *            the instance of which to call the methods
	 * @param annotationCls
	 *            the class of the annotation
	 * @throws IllegalArgumentException
	 *             if the annotated methods not valid
	 */
	public static void callAnnotatedMethods(@Nonnull Object instance,
			@Nonnull Class<? extends Annotation> annotationCls) {
		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(instance.getClass(), annotationCls, true,
				true);
		assertValidAnnotatedMethods(methodsWithAnnotation);

		for (Method m : methodsWithAnnotation) {
			try {
				m.setAccessible(true);
				m.invoke(instance);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("tryed to invoke method " + m + " but got an error", e);
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();

				if (cause instanceof RuntimeException) {
					throw (RuntimeException) cause;
				}
				throw new IllegalArgumentException("method " + m + " throw an exception", cause);
			}
		}
	}

	private static void assertValidAnnotatedMethods(@Nonnull Method[] methods) {
		for (Method m : methods) {
			if (!isValidAnnotatedMethod(m)) {
				throw new IllegalArgumentException(
						"The method " + m + " in class " + m.getDeclaringClass() + " is not a valid annotated method");
			}
		}
	}

	/**
	 * Check if method is a valid annotated method. Annotated methods must not throw Exceptions, have return type void
	 * and take no arguments.
	 * 
	 * @param method
	 *            the method to check
	 * @return true if the given method is a valid annotated method
	 */
	public static boolean isValidAnnotatedMethod(@Nonnull Method method) {
		return method.getExceptionTypes().length == 0 && !method.isVarArgs() && method.getReturnType().equals(Void.TYPE)
				&& method.getParameterTypes().length == 0;
	}

	/**
	 * Inject object into field of instance. Checks if the type of object can be injected into the field.
	 * 
	 * @param instance
	 *            the instance to be modified
	 * @param object
	 *            the value which should be injected into the field
	 * @param field
	 *            the field of the class in which to inject
	 */
	public static void inject(@Nonnull Object instance, @Nullable Object object, @Nonnull Field field) {
		if (object != null && !field.getType().isAssignableFrom(object.getClass())) {
			throw new IllegalArgumentException(
					"the object doesn't have the correct type to be assigable to the given field. The object is of type "
							+ object.getClass() + " and the field of " + field.getType());
		}

		try {
			FieldUtils.writeField(field, instance, object, true);
		} catch (IllegalAccessException e) {
			throw new InjectionException(object, instance, e);
		}
	}

	/**
	 * @param field
	 * @return
	 */
	public static ServiceDescription<?> serviceDescriptionFor(Field field) {
		Class<?> serviceType = field.getType();
		Annotation[] annotations = field.getAnnotations();
		return new ServiceDescriptionImpl<>(serviceType, new HashSet<>(Arrays.asList(annotations)));
	}

}
