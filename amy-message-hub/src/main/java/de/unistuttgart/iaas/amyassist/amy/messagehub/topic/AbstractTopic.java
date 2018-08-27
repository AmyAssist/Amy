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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A abstract class containing most implementation for a {@link Topic}.
 *
 * @author Tim Neumann
 */
public abstract class AbstractTopic implements Topic {
	private final List<TopicLevel> topicLevels;
	private boolean special = false;

	/**
	 * Create a new abstract topic with the given string
	 * <p>
	 * This checks the given String against the rules specified in the OASIS Standard for MQTT Version 3.1.1. chapter
	 * 4.7
	 *
	 * @param topicString
	 *            The topic String describing the new topic.
	 *
	 * @throws TopicFormatException
	 *             When the given topic string is not a valid topic.
	 */
	protected AbstractTopic(String topicString) throws TopicFormatException {

		validateTopicString(topicString);

		String usedString = topicString;
		if (usedString.charAt(0) == Topic.SPECIAL_TOPIC_PREFIX) {
			this.special = true;
			usedString = usedString.substring(1);
		}

		this.topicLevels = new ArrayList<>();

		String[] tokens = usedString.split(Character.toString(Topic.TOPIC_LEVEL_SEPERATOR), -1);

		for (String token : tokens) {
			this.topicLevels.add(new TopicLevelImpl(token));
		}

		validateTopicLevels(this.topicLevels);
	}

	/**
	 * Checks that the given topic String conforms to rules for topics.
	 * <p>
	 * Does not check the validity of the single levels.
	 *
	 * @param topicString
	 *            The topic string to validate
	 * @throws TopicFormatException
	 *             When the given String is invalid.
	 *
	 */
	protected void validateTopicString(String topicString) throws TopicFormatException {
		if (topicString.isEmpty())
			throw new TopicFormatException("The topic String must be at least one character long.");

		if (topicString.contains(Character.toString(Topic.ILLEGAL_NULL_CHARACTER)))
			throw new TopicFormatException("The topic String can't contain a Null character(U+0000).");

		if (topicString.getBytes(StandardCharsets.UTF_8).length > 65535)
			throw new TopicFormatException("The topic String can't encode to more than 65535 bytes.");
	}

	/**
	 * Checks that the given list are valid topic levels.
	 * <p>
	 * Does not check the validity of a single level.
	 *
	 * @param levels
	 *            A list of the levels.
	 * @throws TopicFormatException
	 *             When the given levels are invalid
	 */
	protected void validateTopicLevels(List<TopicLevel> levels) throws TopicFormatException {
		for (int i = 0; i < (levels.size() - 1); i++) {
			if (levels.get(i).isMultiLevelWildcard())
				throw new TopicFormatException("Only the last level may be a multi level wildcard");
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.Topic#getStringRepresentation()
	 */
	@Override
	public String getStringRepresentation() {
		StringBuilder result = new StringBuilder();
		if (this.special) {
			result.append(Topic.SPECIAL_TOPIC_PREFIX);
		}

		result.append(this.topicLevels.get(0).getStringRepresentation());
		for (int i = 1; i < this.topicLevels.size(); i++) {
			result.append(Topic.TOPIC_LEVEL_SEPERATOR);
			result.append(this.topicLevels.get(i).getStringRepresentation());
		}

		return result.toString();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.Topic#getTopicLevels()
	 */
	@Override
	public List<TopicLevel> getTopicLevels() {
		return Collections.unmodifiableList(this.topicLevels);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.Topic#isSpecialTopic()
	 */
	@Override
	public boolean isSpecialTopic() {
		return this.special;
	}

	/**
	 * This returns the same as {@link #getStringRepresentation()}.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getStringRepresentation();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.topicLevels.hashCode() + (this.special ? 0 : 1);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractTopic))
			return false;
		AbstractTopic t = (AbstractTopic) obj;
		return (t.topicLevels.equals(this.topicLevels) && (t.special == this.special));
	}

}
