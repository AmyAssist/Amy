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

package de.unistuttgart.iaas.amyassist.amy.messagehub.topic;

import java.util.List;

/**
 * The interface of a abstract message topic.
 *
 * This conforms to the OASIS Standard for MQTT Version 3.1.1.
 *
 * @author Tim Neumann
 */
public interface Topic {
	/**
	 * Get the string representation of this topic.
	 *
	 * @return The string representation of this topic.
	 */
	String getStringRepresentation();

	/**
	 * Get the levels of this topic as a List of Strings.
	 * <p>
	 * The first element is left most level in the string representation.
	 * <p>
	 * The first element does not include the leading {@link Constants#SPECIAL_TOPIC_PREFIX}, if it is part of this
	 * topic. But any level may still contain that character, because it is only special as the first character of the
	 * topic.
	 *
	 * @return A list of the levels of this topic.
	 */
	List<TopicLevel> getTopicLevels();

	/**
	 * Check if this is a special topic (starting with the '{@link Constants#SPECIAL_TOPIC_PREFIX}').
	 *
	 * @return Whether this is a special topic.
	 */
	boolean isSpecialTopic();

	/**
	 * Get a Topic Name from this topic.
	 * 
	 * @return The topic name
	 * @throws UnsupportedOperationException
	 *             If the topic can't be turned into a name.
	 */
	TopicName getTopicName();

	/**
	 * Get a Topic Filter from this topic
	 * 
	 * @return The topic filter
	 * @throws UnsupportedOperationException
	 *             If the topic can't be turned into a filter.
	 */
	TopicFilter getTopicFilter();

	/**
	 * Get a new topic, which is a (grand-) child of this topic.
	 * 
	 * @param newParts
	 *            The new parts of the topic to add.
	 * @return The new Topic.
	 * @throws IllegalArgumentException
	 *             If the new topic would be malformated.
	 */
	Topic resolve(String newParts);
}
