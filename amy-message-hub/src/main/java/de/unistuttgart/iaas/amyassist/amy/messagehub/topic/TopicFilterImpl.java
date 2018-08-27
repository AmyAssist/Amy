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
 * The implementation of {@link TopicFilter}
 *
 * @author Tim Neumann
 */
public class TopicFilterImpl extends AbstractTopic implements TopicFilter {

	/**
	 * Create a new topic filter with the given string
	 * <p>
	 * This checks the given String against the rules specified in the OASIS Standard for MQTT Version 3.1.1. chapter
	 * 4.7
	 *
	 * @param topicString
	 *            The topic String describing the new topic filter.
	 *
	 * @throws TopicFormatException
	 *             When the given topic string is not a valid topic filter.
	 */
	protected TopicFilterImpl(String topicString) throws TopicFormatException {
		super(topicString);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFilter#doesFilterMatch(de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicName)
	 */
	@Override
	public boolean doesFilterMatch(TopicName name) {
		if (this.isSpecialTopic() != name.isSpecialTopic())
			return false;

		List<TopicLevel> filterLevels = this.getTopicLevels();
		List<TopicLevel> nameLevels = name.getTopicLevels();

		for (int i = 0; i < filterLevels.size(); i++) {
			if (filterLevels.get(i).isMultiLevelWildcard())
				return true;
			else if (nameLevels.size() <= i)
				return false;
			else if (filterLevels.get(i).isNormalLevel() && !filterLevels.get(i).equals(nameLevels.get(i)))
				return false;

		}

		return (filterLevels.size() == nameLevels.size())
	}
}
