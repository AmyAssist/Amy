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

package io.github.amyassist.amy.messagehub.internal;

import io.github.amyassist.amy.core.di.ServiceLocator;
import io.github.amyassist.amy.messagehub.Message;
import io.github.amyassist.amy.messagehub.topic.TopicName;

/**
 * This is a representation of a Subscription
 * 
 * @author Leon Kiefer
 */
interface Subscription {

	/**
	 * Let the subscription handle a message, that was published on an topic, that matches the topic filter.
	 * 
	 * @param topic
	 *            the topic the message was published to, matching the topic filter of this subscription
	 * @param msg
	 *            the message that was published
	 * @param serviceLocator
	 *            the ServiceLocatior to use to get Services
	 */
	void handle(TopicName topic, Message msg, ServiceLocator serviceLocator);

}
