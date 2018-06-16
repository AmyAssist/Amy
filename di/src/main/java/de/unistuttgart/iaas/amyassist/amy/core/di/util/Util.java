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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Util for checks and java reflection
 * 
 * @author Leon Kiefer
 */
public class Util {
	private Util() {
		// hide constructor
	}

	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	/**
	 * Checks if the given class can be used as a Service. There for it must be
	 * a not abstract class with a default constructor.
	 * 
	 * @param cls
	 * @return
	 */
	public static boolean classCheck(@Nonnull Class<?> cls) {
		if (!constructorCheck(cls) || cls.isArray() || cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) {
			return false;
		}

		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(cls, Reference.class);
		for (Field field : dependencyFields) {
			if (field.isAnnotationPresent(Context.class)) {
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
	public static boolean constructorCheck(@Nonnull Class<?> cls) {
		try {
			cls.getConstructor();
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}

	public static void postConstruct(@Nonnull Object instance) {
		Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(instance.getClass(), PostConstruct.class);
		for (Method m : methodsWithAnnotation) {
			try {
				m.invoke(instance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("tryed to invoke method {} but got an error", m, e);
			}
		}
	}

	public static void inject(@Nonnull Object instance, @Nullable Object object, @Nonnull Field field) {
		if (object != null && !field.getType().isAssignableFrom(object.getClass())) {
			throw new IllegalArgumentException(
					"the object doesn't have the correct type to be assigable to the given field. The object is of type "
							+ object.getClass() + " and the field of " + field.getType());
		}

		try {
			FieldUtils.writeField(field, instance, object, true);
		} catch (IllegalAccessException e) {
			logger.error("tryed to inject the dependency {} into {} but failed", object, instance, e);
		}
	}
}
