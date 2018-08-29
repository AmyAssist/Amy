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
 * Implementation of {@link TopicLevel}
 *
 * @author Tim Neumann
 */
class TopicLevelImpl implements TopicLevel {

	private final String levelS;
	private final boolean singleWildcard;
	private final boolean multiWildcard;

	/**
	 * Creates a new level from the given level string
	 *
	 * @param levelString
	 *            The string to create this level from.
	 * @throws TopicFormatException
	 *             When the given String is not a valid topic level.
	 */
	protected TopicLevelImpl(String levelString) throws TopicFormatException {
		this.levelS = levelString;
		if (levelString.contains(Character.toString(Constants.TOPIC_LEVEL_SEPERATOR)))
			throw new IllegalArgumentException("A topic level can't contain topic level seperators.");

		if (levelString.equals(Character.toString(Constants.SINGLE_LEVEL_WILDCARD))) {
			this.singleWildcard = true;
			this.multiWildcard = false;
		} else if (levelString.equals(Character.toString(Constants.MULTI_LEVEL_WILDCARD))) {
			this.multiWildcard = true;
			this.singleWildcard = false;
		} else if (levelString.contains(Character.toString(Constants.SINGLE_LEVEL_WILDCARD))
				|| levelString.contains(Character.toString(Constants.MULTI_LEVEL_WILDCARD)))
			throw new TopicFormatException("A level can not contain a wildcard and another character.");
		else {
			this.multiWildcard = false;
			this.singleWildcard = false;
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevel#getStringRepresentation()
	 */
	@Override
	public String getStringRepresentation() {
		return this.levelS;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevel#isNormalLevel()
	 */
	@Override
	public boolean isNormalLevel() {
		return (!(isSingleLevelWildcard() || isMultiLevelWildcard()));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevel#isSingleLevelWildcard()
	 */
	@Override
	public boolean isSingleLevelWildcard() {
		return this.singleWildcard;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevel#isMultiLevelWildcard()
	 */
	@Override
	public boolean isMultiLevelWildcard() {
		return this.multiWildcard;
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
		return this.levelS.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TopicLevelImpl))
			return false;
		return ((TopicLevelImpl) obj).levelS.equals(this.levelS);
	}
}
