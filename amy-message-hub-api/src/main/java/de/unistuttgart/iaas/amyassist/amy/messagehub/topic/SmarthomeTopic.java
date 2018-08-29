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

/**
 * A smarthome topic
 * 
 * @author Tim Neumann
 */
public interface SmarthomeTopic extends Topic {
	/**
	 * Get a new topic, which is a (grand-) child of this topic.
	 * 
	 * @param newPart
	 *            The new parts of the topic to add.
	 * @return The new Topic.
	 * @throws IllegalArgumentException
	 *             If the new topic would be malformated.
	 */
	LocationTopic resolve(LocationTopics newPart);

	/**
	 * A smarthome location topic
	 * 
	 * @author Tim Neumann
	 */
	public interface LocationTopic extends Topic {
		/**
		 * Get a new topic, which is a (grand-) child of this topic.
		 * 
		 * @param newPart
		 *            The new parts of the topic to add.
		 * @return The new Topic.
		 * @throws IllegalArgumentException
		 *             If the new topic would be malformated.
		 */
		RoomTopic resolve(RoomTopics newPart);

		/**
		 * A smarthome location room topic
		 * 
		 * @author Tim Neumann
		 */
		public interface RoomTopic extends Topic {
			/**
			 * Get a new topic, which is a (grand-) child of this topic.
			 * 
			 * @param newPart
			 *            The new parts of the topic to add.
			 * @return The new Topic.
			 * @throws IllegalArgumentException
			 *             If the new topic would be malformated.
			 */
			FunctionTopic resolve(SmarthomeFunctionTopics newPart);

			/**
			 * A smarthome location room function topic
			 * 
			 * @author Tim Neumann
			 */
			public interface FunctionTopic extends Topic {
				// Nothing.
			}
		}
	}
}
