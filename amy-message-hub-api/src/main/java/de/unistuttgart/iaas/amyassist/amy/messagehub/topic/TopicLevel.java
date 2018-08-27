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
 * The interface of a topic level used in {@link Topic}.
 *
 * This is based on the OASIS Standard for MQTT Version 3.1.1.
 *
 * @author Tim Neumann
 */
public interface TopicLevel {
	/**
	 * Get the string representation of this topic level.
	 *
	 * @return The string representation of this level.
	 */
	String getStringRepresentation();

	/**
	 * Check whether this is a normal topic level without any special meaning.
	 * <p>
	 * This is true if this level is neither equal to {@link Topic#SINGLE_LEVEL_WILDCARD} nor to
	 * {@link Topic#MULTI_LEVEL_WILDCARD}.
	 *
	 * @return Whether this is a normal topic level.
	 */
	boolean isNormalLevel();

	/**
	 * Check whether this level is a single level wildcard, which matches any single level.
	 * <p>
	 * This is true if this level is equal to {@link Topic#SINGLE_LEVEL_WILDCARD}.
	 *
	 * @return Whether this is a single level wildcard.
	 */
	boolean isSingleLevelWildcard();

	/**
	 * Check whether this level is a multi level wildcard, which matches any amount of levels.
	 * <p>
	 * This is true if this level is equal to {@link Topic#MULTI_LEVEL_WILDCARD}.
	 *
	 * @return Whether this is a multi level wildcard.
	 */
	boolean isMultiLevelWildcard();
}
