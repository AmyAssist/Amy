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

import java.util.UUID;

import org.slf4j.Logger;

import asg.cliche.Command;
import asg.cliche.Param;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactory;

/**
 * A console for {@link MessageHub}
 *
 * @author Tim Neumann
 */
public class MessageHubConsole {
	@Reference
	private MessageHub msgHub;
	@Reference
	private Logger logger;
	@Reference
	private TopicFactory tf;

	/**
	 * Subscribe to a topic
	 *
	 * @param topic
	 *            The topic to subscribe to
	 * @return The UUID of the subscription
	 */
	@Command(name = "MessageHub Subscribe", abbrev = "msgh:sub", description = "Subsscribe to the given topic")
	public String subscribe(@Param(name = "topic") String topic) {
		return this.msgHub.subscribe(this.tf.createTopicFilter(topic),
				(t, m) -> this.logger.info("MQTT message" + (m.wasRetained() ? " (retained)" : "")
						+ (m.mayBeDuplicate() ? " (maybe duplicate)" : "") + " on topic " + t.getStringRepresentation()
						+ "with QOS " + m.getQualityOfService() + " with message " + m.getPayload()))
				.toString();
	}

	/**
	 * Unsubscribe
	 * 
	 * @param uuid
	 *            the subscription identifier to unsubscribe from
	 */
	@Command(name = "MessageHub Unsubscribe", abbrev = "msgh:usub", description = "Unsubscribes a given subscription")
	public void unsubscribe(@Param(name = "UUID") String uuid) {
		this.msgHub.unsubscribe(UUID.fromString(uuid));
	}

	/**
	 * Publish to a topic
	 *
	 * @param topic
	 *            The topic to publish to
	 * @param message
	 *            The message to publish
	 */
	@Command(name = "MessageHub Publish", abbrev = "msgh:pub", description = "Publish a message to the given topic")
	public void publish(@Param(name = "topic") String topic, @Param(name = "message") String message) {
		this.msgHub.publish(topic, message);
	}

	/**
	 * Publish to a topic with advances control
	 *
	 * @param topic
	 *            The topic to publish to
	 * @param message
	 *            The message to publish
	 * @param qos
	 *            The quality of service to send with
	 * @param retain
	 *            Whether to retain the message.
	 */
	@Command(name = "MessageHub Publish Advanced", abbrev = "msgh:pubA",
			description = "Publish a message to the given topic with more advanced control")
	public void publishAdvanced(@Param(name = "topic") String topic, @Param(name = "message") String message,
			@Param(name = "quality of service") int qos, @Param(name = "retain") boolean retain) {
		this.msgHub.publish(this.tf.createTopicName(topic), message, qos, retain);
	}
}
