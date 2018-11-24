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

package io.github.amyassist.amy.core.audio.environment;

import java.util.UUID;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import io.github.amyassist.amy.core.audio.AudioManager;
import io.github.amyassist.amy.core.audio.AudioOutput;

/**
 * The interface of a audio environment, which does the audio IO
 * 
 * @author Tim Neumann
 */
public interface AudioEnvironment {

	/**
	 * @return The {@link AudioFormat} format for outputting. The {@link AudioOutput} given to
	 *         {@link #playAudio(AudioOutput, io.github.amyassist.amy.core.audio.AudioManager.OutputBehavior)}
	 *         must have this format.
	 */
	AudioFormat getOutputFormat();

	/**
	 * @return The {@link AudioFormat} format for inputting. The {@link AudioInputStream} returned by
	 *         {@link #getAudioInputStream()} will have this format.
	 */
	AudioFormat getInputFormat();

	/**
	 * Get the audio environment identifier of this audio environment.
	 * 
	 * @return The identifier of this audio environment
	 */
	UUID getAudioEnvironmentIdentifier();

	/**
	 * Stops the current output.
	 */
	void stopOutput();

	/**
	 * Plays the given audio output. When there is already a output stream being played it behaves like defined.
	 * 
	 * @param audioToPlay
	 *            The audio output to play. This must have the format returned by {@link #getOutputFormat()}.
	 * @param behavior
	 *            How to behave if there is an output already
	 * @throws IllegalArgumentException
	 *             When the audioToPlay has the wrong format.
	 * @throws IllegalStateException
	 *             When the behavior is unknown
	 */
	void playAudio(AudioOutput audioToPlay, AudioManager.OutputBehavior behavior);

	/**
	 * Get's a {@link AudioInputStream} from this audio environment. This stream will contain the audio input (like
	 * microphone data) of this environment.
	 * <p>
	 * This stream needs to be read from fast enough. Otherwise it will slow down every other part that needs audio from
	 * this environment.
	 * <p>
	 * When this stream is no longer needed, it should be closed.
	 * 
	 * @return The stream.
	 */
	AudioInputStream getAudioInputStream();

	/**
	 * Starts this audio environment
	 */
	void start();

	/**
	 * Stops this audio environment. If this audio environment is already stopped, does nothing.
	 */
	void stop();

	/**
	 * @return Whether this environment is currently outputting.
	 */
	boolean isCurrentlyOutputting();

}
