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

package io.github.amyassist.amy.core.audio;

import java.util.UUID;

import io.github.amyassist.amy.core.audio.environment.AudioEnvironment;

/**
 * The interface for a internal audio manager. This interface is a more potent version of the {@link AudioManager} and
 * should only be seen by the core.
 * 
 * @author Tim Neumann
 */
public interface InternalAudioManager extends AudioManager {
	/**
	 * Registers an audio environment in the audio manager.
	 * 
	 * @param environment
	 *            The audio environment to register.
	 * @throws IllegalStateException
	 *             When an audio environment with the same identifier is already registered.
	 */
	public void registerAudioEnvironment(AudioEnvironment environment);

	/**
	 * Removes the audio environment described by the given identifier from the audio manager.
	 * 
	 * @param environmentIdentifier
	 *            The identifier of the audio environment to remove.
	 */
	public void unregisterAudioEnvironment(UUID environmentIdentifier);
}
