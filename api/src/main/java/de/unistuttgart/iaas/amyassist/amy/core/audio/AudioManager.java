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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * The class that manages everything that has to do with audio.
 * 
 * @author Tim Neumann
 */
public interface AudioManager {

	/**
	 * Queues the playback of the given audio input stream on the audio environment specified by the given identifier
	 * 
	 * @param identifier
	 *            The identifier of the audio environment to play this audio on.
	 * @param audioToPlay
	 *            The audio to play.
	 * @param behavior
	 *            How to behave if there is some output already.
	 */
	void playAudio(UUID identifier, AudioInputStream audioToPlay, OutputBehavior behavior);

	/**
	 * Stops all audio output on the audio environment specified by the given identifier.
	 * 
	 * This includes all queued output.
	 * 
	 * @param identifier
	 *            The identifier of the audio environment to interrupt.
	 */
	void stopAudioOutput(UUID identifier);

	/**
	 * @return A list of {@link UUID}s, each being the identifier of a registered audio environment
	 */
	List<UUID> getAllRegisteredAudioEnvironments();

	/**
	 * Checks whether the audio environment described by the given identifier is currently outputting any audio.
	 * 
	 * @param identifier
	 *            The identifier of the audio environment to check
	 * @return Whether the audio environment is currently outputting.
	 */
	boolean isAudioEnvironmentCurrentlyOutputting(UUID identifier);

	/**
	 * @return The identifier of the local audio environment.
	 * @throws NoSuchElementException
	 *             When this audio manager has no local audio environment.
	 */
	UUID getLocalAudioEnvironmentIdentifier() throws NoSuchElementException;

	/**
	 * @return Whether this audio manager has a local audio environment
	 */
	boolean hasLocalAudioEnvironment();

	/**
	 * Get's a AudioInputStream with the audio input from the audio environment described by the given identifier.
	 * <p>
	 * For example the microphone input.
	 * <p>
	 * This stream should be read from with appropriate speed, because every other thread that has a InputStream from
	 * the same environment will need to wait otherwise.
	 * <p>
	 * If the stream is no longer needed, it must be closed.
	 * 
	 * @param identifier
	 *            The identifier of the audio environment to get the input Stream from.
	 * @return The audio input stream of the audio environment.
	 */
	AudioInputStream getInputStreamOfAudioEnvironment(UUID identifier);

	/**
	 * @return The default audio format used for output(e.g. speaker).
	 */
	AudioFormat getDefaultOutputAudioFormat();

	/**
	 * @return The default audio format used for input(e.g. microphone).
	 */
	AudioFormat getDefaultInputAudioFormat();

	/**
	 * The possible behaviors of a output, if there is currently another output
	 * 
	 * @author Tim Neumann
	 */
	public enum OutputBehavior {
		/**
		 * Queue the new output.
		 */
		QUEUE,

		/**
		 * Put the new output at the first position of the queue
		 */
		QUEUE_PRIORITY,

		/**
		 * Interrupt the current output and play the new one instead. Then continue with the next output in the queue if
		 * any.
		 */
		INTERRUPT_CURRENT,

		/**
		 * Interrupt the current output, clear the queue and play the new output.
		 */
		INTERRUPT_ALL,

		/**
		 * Suspend the current output and continue it after the new output
		 */
		SUSPEND
	}
}
