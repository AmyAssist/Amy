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
 * A class with static methods for topics.
 * 
 * @author Tim Neumann
 */
public class Topics {
	private Topics() {
		// hide constructor.
	}

	/**
	 * Get's the topic string for the given topic parts
	 * 
	 * @param parts
	 *            The parts of the topic string
	 * @return The topic string.
	 */
	public static String from(String... parts) {
		return String.join("/", parts);
	}

	/**
	 * Get's the topic string for the given topic parts
	 * 
	 * @param parts
	 *            The parts of the topic string
	 * @return The topic string.
	 */
	public static TopicStubbing from(Topic... parts) {
		String[] partsS = new String[parts.length];
		for (int i = 0; i < parts.length; i++) {
			partsS[i] = parts[i].getTopicString();
		}
		return new TopicStubbing(from(partsS));
	}

	/**
	 * Extends a given topic string by the given topics.
	 * 
	 * @param beginning
	 *            The existing topic string
	 * @param parts
	 *            The parts to add.
	 * @return The extended topic string.
	 */
	public static String extend(String beginning, String... parts) {
		return from(beginning, from(parts));
	}

	/**
	 * Extends a given topic string by the given topics.
	 * 
	 * @param beginning
	 *            The existing topic string
	 * @param parts
	 *            The parts to add.
	 * @return The extended topic string.
	 */
	public static String extend(String beginning, Topic... parts) {
		return from(beginning, from(parts).toString());
	}

	/**
	 * Get a smarthome topic string for a given location, room and function.
	 * 
	 * @param location
	 *            The location
	 * @param room
	 *            The room
	 * @param function
	 *            The function
	 * @return The topic string.
	 */
	public static String getSmarthomeTopic(LocationTopics location, RoomTopics room, SmarthomeFunctionTopics function) {
		return from(SystemTopics.SMARTHOME, location, room, function).toString();
	}

	/**
	 * Get a smarthome topic string for a the location ALL, the room ALL and the given function.
	 * 
	 * @param function
	 *            The function
	 * @return The topic string.
	 */
	public static String getGlobalSmarthomeTopic(SmarthomeFunctionTopics function) {
		return from(SystemTopics.SMARTHOME, LocationTopics.ALL, RoomTopics.ALL, function).toString();
	}

	/**
	 * A class for ongoing stubbing to be able to call from(...).extend(...).extend(...).toString()
	 * 
	 * @author Tim Neumann
	 */
	public static class TopicStubbing {
		private String topic;

		/**
		 * Create a new stubbing
		 * 
		 * @param pTopic
		 *            The topic.
		 */
		TopicStubbing(String pTopic) {
			this.topic = pTopic;
		}

		/**
		 * Extends the stubbing by the parts
		 * 
		 * @param parts
		 *            The parts
		 * @return The new extended stubbing
		 */
		public TopicStubbing extend(String... parts) {
			return new TopicStubbing(Topics.extend(this.topic, parts));
		}

		/**
		 * Extends the stubbing by the parts
		 * 
		 * @param parts
		 *            The parts
		 * @return The new extended stubbing
		 */
		public TopicStubbing extend(Topic... parts) {
			return new TopicStubbing(Topics.extend(this.topic, parts));
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.topic;
		}
	}
}
