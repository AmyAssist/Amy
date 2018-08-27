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
 * The implementation of {@link TopicName}
 *
 * @author Tim Neumann
 */
public class TopicNameImpl extends AbstractTopic implements TopicName {

	/**
	 * Create a new topic name with the given string
	 * <p>
	 * This checks the given String against the rules specified in the OASIS Standard for MQTT Version 3.1.1. chapter
	 * 4.7
	 * <p>
	 * This includes a check, that the topic string does not contain any wildcards.
	 *
	 * @param topicString
	 *            The topic String describing the new topic name.
	 *
	 * @throws TopicFormatException
	 *             When the given topic string is not a valid topic name.
	 */
	protected TopicNameImpl(String topicString) throws TopicFormatException {
		super(topicString);
		validateTopicString(topicString);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.AbstractTopic#validateTopicString(java.lang.String)
	 */
	protected final void validateTopicStringForName(String topicString) throws TopicFormatException {
		super.validateTopicString(topicString);
		if (topicString.contains(Character.toString(Topic.SINGLE_LEVEL_WILDCARD)))
			throw new TopicFormatException("A topic name can't contain a single level wildcard.");
		if (topicString.contains(Character.toString(Topic.MULTI_LEVEL_WILDCARD)))
			throw new TopicFormatException("A topic name can't contain a multi-level wildcard.");
	}

}
