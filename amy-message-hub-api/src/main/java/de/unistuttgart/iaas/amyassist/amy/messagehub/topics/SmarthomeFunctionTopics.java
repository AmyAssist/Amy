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

package de.unistuttgart.iaas.amyassist.amy.messagehub.topics;

/**
 * The 4th level topics for smarthome
 * 
 * @author Tim Neumann
 */
public enum SmarthomeFunctionTopics {
	/** The mute topic */
	MUTE;

	/**
	 * Get the topic string for this smarthome function in a given location and room.
	 * 
	 * @param locationTopic
	 *            The location topic this function is in
	 * @param roomTopic
	 *            The room topic this function is in
	 * @return The topic string of this function.
	 */
	public String getTopicString(LocationTopics locationTopic, RoomTopics roomTopic) {
		return roomTopic.getTopicString(SystemTopics.SMARTHOME, locationTopic) + "/" + this.name().toLowerCase();
	}

	/**
	 * Get the topic string for a custom smarthome function in a given location and room.
	 * 
	 * @param locationTopic
	 *            The location topic this function is in
	 * @param roomTopic
	 *            The room topic this function is in
	 * @param customFunction
	 *            The custom function for the topic
	 * @return The topic string of this function.
	 */
	public static String getTopicString(LocationTopics locationTopic, RoomTopics roomTopic, String customFunction) {
		return roomTopic.getTopicString(SystemTopics.SMARTHOME, locationTopic) + "/" + customFunction;
	}
}
