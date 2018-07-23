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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

@Service
public class BrokerImpl implements Broker {

	Map<String, List<AbstractBusMessageExecutor>> eventListener = new HashMap<>();

	/**
	 * Subscribe to a topic
	 * 
	 * @param topic
	 *            to subscribe to
	 * @param eventExecuter
	 *            Class that gets called shoud sth gets published on given Topic
	 */
	@Override
	public void subscribe(String topic, AbstractBusMessageExecutor eventExecuter) {
		if (eventListener.containsKey(topic)) {
			eventListener.get(topic).add(eventExecuter);
		} else {
			List<AbstractBusMessageExecutor> listeners = new LinkedList<>();
			listeners.add(eventExecuter);
			eventListener.put(topic, listeners);
		}

	}

	/**
	 * Unsubscribe to a topic
	 * 
	 * @param topic
	 *            to unsubscribe to
	 * @param eventExecuter
	 *            class that will be unsubscribed to the Topic
	 */
	@Override
	public void unsubscribe(String topic, AbstractBusMessageExecutor eventExecuter) {
		if (eventListener.containsKey(topic)) {
			eventListener.get(topic).remove(eventExecuter);
		}

	}

	/**
	 * Publish a message to a Topic
	 * 
	 * @param topic
	 *            Topic of the Message
	 * @param message
	 *            containing the Information
	 */
	@Override
	public void publish(String topic, String message) {
		if (eventListener.containsKey(topic)) {
			for (AbstractBusMessageExecutor eventExecuter : eventListener.get(topic)) {
				eventExecuter.setEventCommand(message);
				eventExecuter.run();
				//(new Thread(eventExecuter)).start();
			}
		}
	}

}
