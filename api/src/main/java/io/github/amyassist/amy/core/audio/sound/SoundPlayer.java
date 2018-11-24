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

package de.unistuttgart.iaas.amyassist.amy.core.audio.sound;

import java.util.function.Consumer;

import javax.sound.sampled.AudioInputStream;

/**
 * A sound player. It can be started and stopped by the given methods.
 * 
 * To played data is written to the {@link AudioInputStream} returned by {@link #getAudioStream()}. The sound is not
 * played directly.
 * 
 * @author Tim Neumann
 */
public interface SoundPlayer {

	/**
	 * Get the {@link AudioInputStream} on which the audio is being played.
	 * 
	 * @return The audio stream
	 */
	AudioInputStream getAudioStream();

	/**
	 * Starts the audio output.
	 */
	void start();

	/**
	 * Stops / cancels the audio output.
	 */
	void stop();

	/**
	 * Checks if the player is currently running
	 * 
	 * @return Whether the player is running
	 */
	boolean isRunning();

	/**
	 * Sets the callback, which is called when this player ends playback.
	 * <p>
	 * This method can only be called before {@link #start()}.
	 * <p>
	 * The callback is provided with a reason for the end of the playback.
	 * 
	 * @param callback
	 *            The method called.
	 * @throws IllegalStateException
	 *             When the player has already been started.
	 */
	void setOnStopHook(Consumer<StopReason> callback);

	/**
	 * All possible reasons for the SoundPlayer to stop.
	 * 
	 * @author Tim Neumann
	 */
	public enum StopReason {
		/** When the audio that should be played, was played completely */
		END_OF_AUDIO,
		/** When the audio stream, the audio is played on was closed. */
		STREAM_CLOSED,
		/** When the sound player was stopped via {@link SoundPlayer#stop()}. */
		PLAYER_STOPPED,
		/** Any other reason. */
		OTHER;
	}
}
