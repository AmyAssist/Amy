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

package de.unistuttgart.iaas.amyassist.amy.messagehub;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactory;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName;

/**
 * The implementation of the MessageHub api interface as Runnable Service
 *
 * @author Kai Menzel, Leon Kiefer
 */
@Service(MessageHub.class)
public class MessageHubService implements MessageHub {
	@Reference
	private Logger logger;

	@Reference
	private TopicFactory tf;

	@Reference
	private MessagingAdapter adapter;

	private Map<UUID, BiConsumer<TopicName, Message>> eventListener = new HashMap<>();
	private Map<TopicFilter, List<UUID>> topicListeners = new HashMap<>();

	@PostConstruct
	private void init() {
		this.adapter.setCallback(this::messageArrived);
	}

	private void executeHandler(BiConsumer<TopicName, Message> handler, Message msg, TopicName topic) {
		try {
			handler.accept(topic, msg);
		} catch (RuntimeException e) {
			this.logger.error("Error in event handler", e);
		}
	}

	@Override
	public void publish(String topic, String message) {
		publish(this.tf.createTopicName(topic), message, 2, false);
	}

	private void messageArrived(TopicName topic, Message message) {
		for (Entry<TopicFilter, List<UUID>> entry : this.topicListeners.entrySet()) {
			TopicFilter filter = entry.getKey();
			if (filter.doesFilterMatch(topic)) {
				for (UUID uuid : entry.getValue()) {
					BiConsumer<TopicName, Message> handler = this.eventListener.get(uuid);
					executeHandler(handler, message, topic);
				}
			}
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(java.lang.String,
	 *      java.util.function.Consumer)
	 */
	@Override
	public UUID subscribe(String topic, Consumer<String> handler) {
		return subscribe(this.tf.createTopicFilter(topic), (t, m) -> handler.accept(m.getPayload()));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(TopicFilter,
	 *      java.util.function.BiConsumer)
	 */
	@Override
	public UUID subscribe(TopicFilter topic, BiConsumer<TopicName, Message> handler) {
		UUID uuid = UUID.randomUUID();

		if (this.topicListeners.containsKey(topic)) {
			this.topicListeners.get(topic).add(uuid);
		} else {
			List<UUID> listeners = new LinkedList<>();
			listeners.add(uuid);
			this.topicListeners.put(topic, listeners);
			this.adapter.subscribe(topic);
		}

		this.eventListener.put(uuid, handler);

		return uuid;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#unsubscribe(java.util.UUID)
	 */
	@Override
	public void unsubscribe(UUID identifier) {
		this.eventListener.remove(identifier);
		for (TopicFilter topic : this.topicListeners.keySet()) {
			if (this.topicListeners.get(topic).contains(identifier)) {
				this.topicListeners.get(topic).remove(identifier);
				if (this.topicListeners.get(topic).isEmpty()) {
					this.topicListeners.remove(topic);
					this.adapter.unsubscribe(topic);
				}
				break;
			}
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(TopicName, java.lang.String, int, boolean)
	 */
	@Override
	public void publish(TopicName topic, String payload, int qualityOfService, boolean retain) {
		this.adapter.publish(topic, payload, qualityOfService, retain);
	}
}
