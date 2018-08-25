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
	 * The topic level separator '{@value #TOPIC_LEVEL_SEPERATOR}' as defined in chapter 4.7.1.1 of the standard.
	 */
	char TOPIC_LEVEL_SEPERATOR = 0x002F;

	/**
	 * The multi-level wildcard '{@value #MULTI_LEVEL_WILDCARD}' as defined in chapter 4.7.1.2 of the standard.
	 */
	char MULTI_LEVEL_WILDCARD = 0x0023;

	/**
	 * The single level wildcard '{@value #SINGLE_LEVEL_WILDCARD}' as defined in chapter 4.7.1.3 of the standard.
	 */
	char SINGLE_LEVEL_WILDCARD = 0x002B;

	/**
	 * The character indicating that any topic beginning with it is a special topic '{@value #SPECIAL_TOPIC_PREFIX}' as
	 * defined in chapter 4.7.2 of the standard.
	 */
	char SPECIAL_TOPIC_PREFIX = '$';

	/**
	 * The illegal null character as defined in chapter 4.7.3 of the standard.
	 */
	char ILLEGAL_NULL_CHARACTER = 0x0000;

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
	 * The first element does not include the leading {@link #SPECIAL_TOPIC_PREFIX}, if it is part of this topic. But
	 * any level may still contain that character, because it is only special as the first character of the topic.
	 *
	 * @return A list of the levels of this topic.
	 */
	List<TopicLevel> getTopicLevels();

	/**
	 * Check if this is a special topic (starting with the '{@link #SPECIAL_TOPIC_PREFIX}').
	 *
	 * @return Whether this is a special topic.
	 */
	boolean isSpecialTopic();
}
