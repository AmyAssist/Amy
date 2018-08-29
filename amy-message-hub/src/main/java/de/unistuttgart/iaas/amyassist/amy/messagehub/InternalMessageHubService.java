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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PreDestroy;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactory;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName;

/**
 * The internal implementation of the MessageHub
 *
 * @author Tim Neumann
 */
@Service(InternalMessageHubService.class)
public class InternalMessageHubService {
	@Reference
	private Logger logger;

	@Reference
	private TopicFactory tf;

	@Reference
	private MessagingAdapter adapter;

	private final Map<UUID, BiConsumer<TopicName, Message>> eventListener = new ConcurrentHashMap<>();
	private final Map<TopicFilter, List<UUID>> topicListeners = new ConcurrentHashMap<>();

	private volatile boolean adapterFullyConnected = false;

	@PostConstruct
	private void init() {
		this.adapter.setCallback(this::messageArrived);
		this.adapter.setStartCallback(this::doSubscriptionsInAdapter);
	}

	@PreDestroy
	private void destroy() {
		this.adapter.setCallback(null);
	}

	private void executeHandler(BiConsumer<TopicName, Message> handler, Message msg, TopicName topic) {
		try {
			handler.accept(topic, msg);
		} catch (RuntimeException e) {
			this.logger.error("Error in event handler", e);
		}
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

	private void doSubscriptionsInAdapter() {
		synchronized (this.topicListeners) {
			for (TopicFilter topic : this.topicListeners.keySet()) {
				this.adapter.subscribe(topic);
			}
			this.adapterFullyConnected = true;
		}
	}

	/**
	 * @param topic
	 *            The topic to subscribe to
	 * @param handler
	 *            The handler for messages of the topic
	 * @return The uuid of the subscription.
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(TopicFilter,
	 *      java.util.function.BiConsumer)
	 */
	public UUID subscribe(TopicFilter topic, BiConsumer<TopicName, Message> handler) {
		UUID uuid = UUID.randomUUID();

		synchronized (this.topicListeners) {
			if (this.topicListeners.containsKey(topic)) {
				this.topicListeners.get(topic).add(uuid);
			} else {
				List<UUID> listeners = new LinkedList<>();
				listeners.add(uuid);
				this.topicListeners.put(topic, listeners);
				if (this.adapterFullyConnected) {
					this.adapter.subscribe(topic);
				}
			}

			this.eventListener.put(uuid, handler);
		}

		return uuid;
	}

	/**
	 * @param identifier
	 *            The uuid of the subscribtion to unsubscribe.
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#unsubscribe(java.util.UUID)
	 */
	public void unsubscribe(UUID identifier) {
		synchronized (this.topicListeners) {
			this.eventListener.remove(identifier);
			for (Entry<TopicFilter, List<UUID>> entry : this.topicListeners.entrySet()) {
				if (entry.getValue().contains(identifier)) {
					entry.getValue().remove(identifier);
					if (entry.getValue().isEmpty()) {
						this.topicListeners.remove(entry.getKey());
						this.adapter.unsubscribe(entry.getKey());
					}
					break;
				}
			}
		}
	}

	/**
	 * @param topic
	 *            The topic to publish to
	 * @param payload
	 *            The payload to publish
	 * @param qualityOfService
	 *            The qos to publish with
	 * @param retain
	 *            Whether to retain the message
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(TopicName, java.lang.String, int, boolean)
	 */
	public void publish(TopicName topic, String payload, int qualityOfService, boolean retain) {
		this.adapter.publish(topic, payload, qualityOfService, retain);
	}
}
