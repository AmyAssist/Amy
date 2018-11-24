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

package io.github.amyassist.amy.messagehub;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.amyassist.amy.messagehub.topic.TopicFilter;
import io.github.amyassist.amy.messagehub.topic.TopicName;

/**
 * The MessageHub is a programmatic API to publish and subscribe to topics.
 *
 * @author Kai Menzel, Leon Kiefer, Tim Neumann
 */
public interface MessageHub {

	/**
	 * Subscribe to a topic
	 *
	 * @param topic
	 *            The topic to subscribe to. This may contain wildcards.
	 * @param handler
	 *            A handler being called with the payload of the message, when a message is received.
	 * @return The identifier representing this subscription
	 */
	UUID subscribe(String topic, Consumer<String> handler);

	/**
	 * Subscribe to a topic
	 *
	 * @param topic
	 *            The topic to subscribe to. This may contain wildcards.
	 * @param handler
	 *            A handler being called with the real topic and the message, when a message is received.
	 * @return The identifier representing this subscription
	 */
	UUID subscribe(String topic, BiConsumer<TopicName, Message> handler);

	/**
	 * Subscribe to a topic
	 *
	 * @param topic
	 *            The topic to subscribe to. This may contain wildcards.
	 * @param handler
	 *            A handler being called with the payload of the message, when a message is received.
	 * @return The identifier representing this subscription
	 */
	UUID subscribe(TopicFilter topic, Consumer<String> handler);

	/**
	 * Subscribe to a topic
	 *
	 * @param topic
	 *            The topic to subscribe to. This may contain wildcards.
	 * @param handler
	 *            A handler being called with the real topic and the message, when a message is received.
	 * @return The identifier representing this subscription
	 */
	UUID subscribe(TopicFilter topic, BiConsumer<TopicName, Message> handler);

	/**
	 * Remove a subscription.
	 *
	 * @param identifier
	 *            The identifier representing the subscription to remove
	 */
	void unsubscribe(UUID identifier);

	/**
	 * Publish a message to a topic
	 *
	 * @param topic
	 *            The topic to publish to,
	 * @param payload
	 *            The payload of the message.
	 */
	void publish(String topic, String payload);

	/**
	 * Publish a message to a topic
	 *
	 * @param topic
	 *            The topic to publish to,
	 * @param payload
	 *            The payload of the message.
	 * @param qualityOfService
	 *            The quality of service(0-2) of the message.
	 * @param retain
	 *            Whether the server should retain this message for new subscribes until a new retained message is sent
	 *            over this topic.
	 */
	void publish(String topic, String payload, int qualityOfService, boolean retain);

	/**
	 * Publish a message to a topic
	 *
	 * @param topic
	 *            The topic to publish to,
	 * @param payload
	 *            The payload of the message.
	 * @param retain
	 *            Whether the server should retain this message for new subscribes until a new retained message is sent
	 *            over this topic.
	 */
	void publish(String topic, String payload, boolean retain);

	/**
	 * Publish a message to a topic
	 *
	 * @param topic
	 *            The topic to publish to,
	 * @param payload
	 *            The payload of the message.
	 * @param qualityOfService
	 *            The quality of service(0-2) of the message.
	 */
	void publish(String topic, String payload, int qualityOfService);

	/**
	 * Publish a message to a topic with a given quality of service.
	 *
	 * @param topic
	 *            The topic to publish to
	 * @param payload
	 *            The payload of the message.
	 * @param qualityOfService
	 *            The quality of service(0-2) of the message.
	 * @param retain
	 *            Whether the server should retain this message for new subscribes until a new retained message is sent
	 *            over this topic.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/MQTT#Quality_of_service_(QoS)"> quality of service </a>
	 */
	void publish(TopicName topic, String payload, int qualityOfService, boolean retain);

	/**
	 * Publish a message to a topic.
	 *
	 * @param topic
	 *            The topic to publish to
	 * @param payload
	 *            The payload of the message.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/MQTT#Quality_of_service_(QoS)"> quality of service </a>
	 */
	void publish(TopicName topic, String payload);

	/**
	 * Publish a message to a topic with a given quality of service.
	 *
	 * @param topic
	 *            The topic to publish to
	 * @param payload
	 *            The payload of the message.
	 * @param qualityOfService
	 *            The quality of service(0-2) of the message.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/MQTT#Quality_of_service_(QoS)"> quality of service </a>
	 */
	void publish(TopicName topic, String payload, int qualityOfService);

	/**
	 * Publish a message to a topic.
	 *
	 * @param topic
	 *            The topic to publish to
	 * @param payload
	 *            The payload of the message.
	 * @param retain
	 *            Whether the server should retain this message for new subscribes until a new retained message is sent
	 *            over this topic.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/MQTT#Quality_of_service_(QoS)"> quality of service </a>
	 */
	void publish(TopicName topic, String payload, boolean retain);

}
