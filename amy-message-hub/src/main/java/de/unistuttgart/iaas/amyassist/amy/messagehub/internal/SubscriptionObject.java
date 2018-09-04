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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceLocator;
import de.unistuttgart.iaas.amyassist.amy.messagehub.Message;
import de.unistuttgart.iaas.amyassist.amy.messagehub.annotations.MessageReceiver;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName;

/**
 * A Subscription from a MessageReciever
 * 
 * @author Leon kiefer
 */
class SubscriptionObject implements Subscription {

	private final Class<?> messageReceiverClass;
	private final Method method;

	/**
	 * @param cls
	 *            the {@link MessageReceiver} class
	 * @param method
	 *            the method of the {link {@link de.unistuttgart.iaas.amyassist.amy.messagehub.annotations.Subscription}
	 */
	public SubscriptionObject(Class<?> cls, Method method) {
		this.messageReceiverClass = cls;
		this.method = method;
	}

	@Override
	public void handle(TopicName topic, Message msg, ServiceLocator serviceLocator) {
		Object createAndInitialize = serviceLocator.createAndInitialize(this.messageReceiverClass);

		try {
			this.method.invoke(createAndInitialize, msg.getPayload());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("tryed to invoke method " + this.method + " but got an error", e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();

			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new IllegalArgumentException("method " + this.method + " throw an exception", cause);
		}

		serviceLocator.preDestroy(createAndInitialize);
	}
}
