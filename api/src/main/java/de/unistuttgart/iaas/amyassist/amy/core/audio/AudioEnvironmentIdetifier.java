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

package de.unistuttgart.iaas.amyassist.amy.core.audio;

/**
 * A unique identifier of an audio environment, which also contains information about that Audio environment
 * 
 * @author Tim Neumann
 */
public interface AudioEnvironmentIdetifier {

	/**
	 * @return The type of this AudioEnvironment
	 */
	AudioEnvironmentType getAudioEnvironmentType();

	/**
	 * @return Whether global sounds should be played on this environment
	 */
	boolean shouldPlayGlobalSound();

	/**
	 * Compares this environment identifier to an other one. This returns true if and only if the Audio Environment
	 * described by the other identifier is exactly the same as the one described by this. (The same instance. When ==
	 * of the two environments would return true)
	 * 
	 * The result of this method needs to return the same as {@link Object#equals(Object)}, which needs to be compatible
	 * with {@link Object#hashCode()}.
	 * 
	 * @param other
	 *            The {@link AudioEnvironmentIdetifier} of the AudioEnvironment to compare to.
	 * @return Whether the Environment decribed by the other identifier is exactly the same as the one described by
	 *         this.
	 */
	boolean equals(AudioEnvironmentIdetifier other);

	/**
	 * A type of a AudioEnvironment
	 * 
	 * @author Tim Neumann
	 */
	public enum AudioEnvironmentType {
		/**
		 * The type representing local audio environments
		 */
		LOCAL,
		/**
		 * The type representing remote audio environments
		 */
		REMOTE
	}
}
