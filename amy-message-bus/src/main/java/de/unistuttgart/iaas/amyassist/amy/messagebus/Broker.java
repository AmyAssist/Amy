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

package de.unistuttgart.iaas.amyassist.amy.messagebus;

public interface Broker {

	/**
	 * Subscribe to a topic
	 * 
	 * @param topic
	 *            to subscribe to
	 * @param eventExecuter
	 *            Class that gets called shoud sth gets published on given Topic
	 */
	void subscribe(String topic, AbstractBusMessageExecutor eventExecuter);

	/**
	 * Unsubscribe to a topic
	 * 
	 * @param topic
	 *            to unsubscribe to
	 * @param eventExecuter
	 *            class that will be unsubscribed to the Topic
	 */
	void unsubscribe(String topic, AbstractBusMessageExecutor eventExecuter);

	/**
	 * Publish a message to a Topic
	 * 
	 * @param topic
	 *            Topic of the Message
	 * @param message
	 *            containing the Information
	 */
	void publish(String topic, String message);
}
