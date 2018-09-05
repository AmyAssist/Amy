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
 * The room topics
 * 
 * @author Tim Neumann
 */
public enum RoomTopics {
	/** All rooms */
	ALL,
	/** The living room */
	LIVING;

	/**
	 * Get the topic string for this location in a given system and location.
	 * 
	 * @param systemTopic
	 *            The system topic this room is in.
	 * @param locationTopic
	 *            The location topic this room is in
	 * @return The topic string of this room.
	 */
	public String getTopicString(SystemTopics systemTopic, LocationTopics locationTopic) {
		return locationTopic.getTopicString(systemTopic) + "/" + this.name().toLowerCase();
	}
}
