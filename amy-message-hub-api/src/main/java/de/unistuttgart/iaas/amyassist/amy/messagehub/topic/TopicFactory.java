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

/**
 * A Factory for creating {@link TopicFilter} and {@link TopicName} objects.
 *
 * @author Tim Neumann
 */
public interface TopicFactory {
	/**
	 * Creates a topic filter from the given String
	 * <p>
	 * This method conforms to OASIS Standard for MQTT Version 3.1.1.
	 *
	 * @param topicFilterString
	 *            The String describing the filter to create.
	 * @return The topic filter described by the String.
	 * @throws IllegalArgumentException
	 *             If the given String is not a valid topic filter as defined in the standard.
	 */
	TopicFilter createTopicFilter(String topicFilterString);

	/**
	 * Creates a topic name from the given String
	 * <p>
	 * This method conforms to OASIS Standard for MQTT Version 3.1.1.
	 *
	 * @param topicNameString
	 *            The String describing the name to create.
	 * @return The topic name described by the String.
	 * @throws IllegalArgumentException
	 *             If the given String is not a valid topic name as defined in the standard.
	 */
	TopicName createTopicName(String topicNameString);

	/**
	 * Creates a topic from a amount of strings subtopics.
	 * 
	 * @param parts
	 *            The subtopics
	 * 
	 * @return The new topic.
	 */
	Topic from(String... parts);

	/**
	 * @return The smarthome topic
	 */
	SmarthomeTopic smarthomeTopic();
}
