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

import java.util.function.BiConsumer;

import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName;

/**
 * A adapter for the message hub, to be used to communicate with a broker.
 * 
 * @author Tim Neumann
 */
public interface MessagingAdapter {
	/**
	 * Publishes a message on the given topic.
	 * 
	 * @param topic
	 *            The topic to publish to
	 * @param payload
	 *            The payload to publish
	 * @param qualityOfService
	 *            The quality of service level to publish with (0-2)
	 * @param retain
	 *            Whether the message should be retained.
	 * 
	 * @throws IllegalStateException
	 *             When an error occurs while publishing.
	 */
	void publish(TopicName topic, String payload, int qualityOfService, boolean retain);

	/**
	 * Subscribe to a topic
	 * 
	 * @param topic
	 *            The topic to subscribe to.
	 * @throws IllegalStateException
	 *             When an error occurs while subscribing.
	 */
	void subscribe(TopicFilter topic);

	/**
	 * Unsubscribe from a topic
	 * 
	 * @param topic
	 *            The topic to unsubscribe from
	 * @throws IllegalStateException
	 *             When an error occurs while unsubscribing.
	 */
	void unsubscribe(TopicFilter topic);

	/**
	 * Set the callback for all received messages for this adapter
	 * 
	 * @param callback
	 *            The function called when a message is received.
	 */
	void setCallback(BiConsumer<TopicName, Message> callback);
}
