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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.messagehub.internal.InternalMessageHubService;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactory;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName;

/**
 * The implementation of {@link MessageHub}. Uses the internal message hub service.
 * 
 * @author Tim Neumann
 */
@Service(MessageHub.class)
public class MessageHubService implements MessageHub {

	@Reference
	private InternalMessageHubService internalMessageHub;

	@Reference
	private TopicFactory tf;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(java.lang.String,
	 *      java.util.function.Consumer)
	 */
	@Override
	public UUID subscribe(String topic, Consumer<String> handler) {
		return subscribe(this.tf.createTopicFilter(topic), (t, m) -> handler.accept(m.getPayload()));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(java.lang.String,
	 *      java.util.function.BiConsumer)
	 */
	@Override
	public UUID subscribe(String topic, BiConsumer<TopicName, Message> handler) {
		return subscribe(this.tf.createTopicFilter(topic), handler);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter,
	 *      java.util.function.BiConsumer)
	 */
	@Override
	public UUID subscribe(TopicFilter topic, BiConsumer<TopicName, Message> handler) {
		return this.internalMessageHub.subscribe(topic, handler);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#subscribe(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter,
	 *      java.util.function.Consumer)
	 */
	@Override
	public UUID subscribe(TopicFilter topic, Consumer<String> handler) {
		return subscribe(topic, (t, m) -> handler.accept(m.getPayload()));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#unsubscribe(java.util.UUID)
	 */
	@Override
	public void unsubscribe(UUID identifier) {
		this.internalMessageHub.unsubscribe(identifier);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(java.lang.String, java.lang.String, int,
	 *      boolean)
	 */
	@Override
	public void publish(String topic, String payload, int qualityOfService, boolean retain) {
		publish(this.tf.createTopicName(topic), payload, qualityOfService, retain);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(java.lang.String, java.lang.String,
	 *      boolean)
	 */
	@Override
	public void publish(String topic, String payload, boolean retain) {
		publish(topic, payload, 2, retain);

	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void publish(String topic, String payload, int qualityOfService) {
		publish(topic, payload, qualityOfService, false);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(java.lang.String, java.lang.String)
	 */
	@Override
	public void publish(String topic, String payload) {
		publish(topic, payload, 2, false);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName,
	 *      java.lang.String, int, boolean)
	 */
	@Override
	public void publish(TopicName topic, String payload, int qualityOfService, boolean retain) {
		this.internalMessageHub.publish(topic, payload, qualityOfService, retain);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName,
	 *      java.lang.String, boolean)
	 */
	@Override
	public void publish(TopicName topic, String payload, boolean retain) {
		publish(topic, payload, 2, retain);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName,
	 *      java.lang.String, int)
	 */
	@Override
	public void publish(TopicName topic, String payload, int qualityOfService) {
		publish(topic, payload, qualityOfService, false);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub#publish(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName,
	 *      java.lang.String)
	 */
	@Override
	public void publish(TopicName topic, String payload) {
		publish(topic, payload, 2, false);
	}

}
