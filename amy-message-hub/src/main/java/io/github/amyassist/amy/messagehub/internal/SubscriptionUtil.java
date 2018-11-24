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

package de.unistuttgart.iaas.amyassist.amy.messagehub.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName;

/**
 * Util for low level operations
 * 
 * @author Leon Kiefer
 */
public class SubscriptionUtil {

	private SubscriptionUtil() {
		// hide constructor
	}

	/**
	 * check if the given method is a valid Subscription method. If not throw an exception.
	 * 
	 * @param method
	 *            the method to validate as Subscription method
	 * @throws IllegalArgumentException
	 *             if the given method is not a valid Subscription method
	 */
	public static void assertValidSubscriptionMethod(Method method) {
		if (!method.getReturnType().equals(Void.TYPE))
			throw new IllegalArgumentException(
					"The returntype of a method annotated with @Subscription should be void.");
		if (method.getExceptionTypes().length > 0) {
			throw new IllegalArgumentException("The method annotated with @Subscription should not throw exceptions.");
		}
		if (Modifier.isStatic(method.getModifiers())) {
			throw new IllegalArgumentException("The method annotated with @Subscription should not be static.");
		}
		if (!Modifier.isPublic(method.getModifiers())) {
			throw new IllegalArgumentException("The method annotated with @Subscription should be public.");
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 1) {
			if (!parameterTypes[0].equals(String.class)) {
				throw new IllegalArgumentException(
						"The first parameter of a method annotated with @Subscription must be a String.");
			}
		} else if (parameterTypes.length == 2) {
			if (!parameterTypes[0].equals(String.class)) {
				throw new IllegalArgumentException(
						"The first parameter of a method annotated with @Subscription must be a String.");
			}
			if (!parameterTypes[1].equals(TopicName.class)) {
				throw new IllegalArgumentException(
						"The second parameter of a method annotated with @Subscription must be a TopicName.");
			}
		} else {
			throw new IllegalArgumentException(
					"Subscription can only have the message and the topicname as parameter.");
		}

		if (method.isVarArgs()) {
			throw new IllegalArgumentException("The method annotated with @Subscription should not be a VarArgs.");
		}
	}
}
