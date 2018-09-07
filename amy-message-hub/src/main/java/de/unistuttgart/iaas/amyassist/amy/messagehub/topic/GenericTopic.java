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
 * A generic {@link Topic}.
 *
 * @author Tim Neumann
 */
class GenericTopic implements Topic {
	private final List<TopicLevel> topicLevels;

	/**
	 * Create a new generic topic with the given string
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
	protected GenericTopic(String topicString) throws TopicFormatException {

		validateTopicString(topicString);

		this.topicLevels = new ArrayList<>();

		String[] tokens = topicString.split(Character.toString(Constants.TOPIC_LEVEL_SEPERATOR), -1);

		for (String token : tokens) {
			this.topicLevels.add(new TopicLevelImpl(token));
		}

		validateTopicLevels(this.topicLevels);
	}

	/**
	 * Creates a new generic topic from any existing topic. No checks are being done.
	 * 
	 * @param orig
	 *            The original topic.
	 */
	protected GenericTopic(Topic orig) {
		this.topicLevels = new ArrayList<>(orig.getTopicLevels());
	}

	/**
	 * Creates a new generic topic from any exisiting topic and a String containing the extension of the existing topic.
	 * 
	 * @param orig
	 *            The original topic
	 * @param extension
	 *            The extension to be added at the end of the original topic. (Without trailing level seperator)
	 * @throws TopicFormatException
	 *             When the format of the orig combined with the extension is invalid.
	 */
	protected GenericTopic(Topic orig, String extension) throws TopicFormatException {
		validateTopicString(orig.getStringRepresentation() + Constants.TOPIC_LEVEL_SEPERATOR + extension);

		this.topicLevels = new ArrayList<>(orig.getTopicLevels());
		String[] tokens = extension.split(Character.toString(Constants.TOPIC_LEVEL_SEPERATOR), -1);

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
	protected static void validateTopicString(String topicString) throws TopicFormatException {
		if (topicString.isEmpty())
			throw new TopicFormatException("The topic String must be at least one character long.");

		if (topicString.contains(Character.toString(Constants.ILLEGAL_NULL_CHARACTER)))
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
	protected static void validateTopicLevels(List<TopicLevel> levels) throws TopicFormatException {
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

		result.append(this.topicLevels.get(0).getStringRepresentation());
		for (int i = 1; i < this.topicLevels.size(); i++) {
			result.append(Constants.TOPIC_LEVEL_SEPERATOR);
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
		return this.topicLevels.get(0).getStringRepresentation()
				.startsWith(Character.toString(Constants.SPECIAL_TOPIC_PREFIX));
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
		return this.topicLevels.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GenericTopic))
			return false;
		GenericTopic t = (GenericTopic) obj;
		return (t.topicLevels.equals(this.topicLevels));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.Topic#getTopicName()
	 */
	@Override
	public TopicName getTopicName() {
		try {
			return new TopicNameImpl(this);
		} catch (TopicFormatException e) {
			throw new UnsupportedOperationException("Can't turn this into a topic name", e);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.Topic#getTopicFilter()
	 */
	@Override
	public TopicFilter getTopicFilter() {
		return new TopicFilterImpl(this);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.Topic#resolve(java.lang.String)
	 */
	@Override
	public Topic resolve(String newParts) {
		try {
			return new GenericTopic(this, newParts);
		} catch (TopicFormatException e) {
			throw new IllegalArgumentException("Can't create new Topic from this with the given string.", e);
		}
	}
}
