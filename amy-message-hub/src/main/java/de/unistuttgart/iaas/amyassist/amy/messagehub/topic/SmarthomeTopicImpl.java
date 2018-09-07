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

import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.LocationTopics;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.RoomTopics;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.SmarthomeFunctionTopics;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.SystemTopics;

/**
 * A smarthome topic
 * 
 * @author Tim Neumann
 */
class SmarthomeTopicImpl extends GenericTopic implements SmarthomeTopic {

	private static final String E_MSG = "Can't create new Topic from this with the given string.";

	/**
	 * Create a new smarthome topic
	 * 
	 * @throws TopicFormatException
	 *             When the given topic string is not a valid topic.
	 */
	protected SmarthomeTopicImpl() throws TopicFormatException {
		super(SystemTopics.SMARTHOME.getTopicString());
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.GenericTopic#resolve(java.lang.String)
	 */
	@Override
	public LocationTopic resolve(LocationTopics newPart) {
		try {
			return new LocationTopicImpl(this, newPart.getTopicString());
		} catch (TopicFormatException e) {
			throw new IllegalArgumentException(E_MSG, e);
		}
	}

	private class LocationTopicImpl extends GenericTopic implements LocationTopic {
		/**
		 * Creates a new topic from a exisiting topic and a String containing the extension of the existing topic.
		 * 
		 * @see GenericTopic#GenericTopic(Topic, String)
		 * 
		 * @param orig
		 *            The original topic
		 * @param extension
		 *            The extension to be added at the end of the original topic. (Without trailing level seperator)
		 * @throws TopicFormatException
		 *             When the format of the orig combined with the extension is invalid.
		 */
		protected LocationTopicImpl(Topic orig, String extension) throws TopicFormatException {
			super(orig, extension);
		}

		/**
		 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.SmarthomeTopic.LocationTopic#resolve(de.unistuttgart.iaas.amyassist.amy.messagehub.topics.RoomTopics)
		 */
		@Override
		public RoomTopic resolve(RoomTopics newPart) {
			try {
				return new RoomTopicImpl(this, newPart.getTopicString());
			} catch (TopicFormatException e) {
				throw new IllegalArgumentException(E_MSG, e);
			}
		}

		private class RoomTopicImpl extends GenericTopic implements RoomTopic {
			/**
			 * Creates a new topic from a exisiting topic and a String containing the extension of the existing topic.
			 * 
			 * @see GenericTopic#GenericTopic(Topic, String)
			 * 
			 * @param orig
			 *            The original topic
			 * @param extension
			 *            The extension to be added at the end of the original topic. (Without trailing level seperator)
			 * @throws TopicFormatException
			 *             When the format of the orig combined with the extension is invalid.
			 */
			protected RoomTopicImpl(Topic orig, String extension) throws TopicFormatException {
				super(orig, extension);
			}

			/**
			 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.SmarthomeTopic.LocationTopic.RoomTopic#resolve(de.unistuttgart.iaas.amyassist.amy.messagehub.topics.SmarthomeFunctionTopics)
			 */
			@Override
			public FunctionTopic resolve(SmarthomeFunctionTopics newPart) {
				try {
					return new FunctionTopicImpl(this, newPart.getTopicString());
				} catch (TopicFormatException e) {
					throw new IllegalArgumentException(E_MSG, e);
				}
			}

			private class FunctionTopicImpl extends GenericTopic implements FunctionTopic {
				/**
				 * Creates a new topic from a exisiting topic and a String containing the extension of the existing
				 * topic.
				 * 
				 * @see GenericTopic#GenericTopic(Topic, String)
				 * 
				 * @param orig
				 *            The original topic
				 * @param extension
				 *            The extension to be added at the end of the original topic. (Without trailing level
				 *            seperator)
				 * @throws TopicFormatException
				 *             When the format of the orig combined with the extension is invalid.
				 */
				protected FunctionTopicImpl(Topic orig, String extension) throws TopicFormatException {
					super(orig, extension);
				}
			}
		}
	}
}
