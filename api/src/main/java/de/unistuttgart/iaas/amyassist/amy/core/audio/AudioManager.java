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

import javax.sound.sampled.AudioInputStream;

/**
 * The class that manages everything that has to do with audio.
 * 
 * @author Tim Neumann
 */
public interface AudioManager {
	/**
	 * Queues the playback of the given audio input stream on all audio environments, that are configured to play global
	 * audio.
	 * 
	 * The output behavior is applied to all effected audio environments.
	 * 
	 * For example if the Output Behavior is {@link OutputBehavior#QUEUE QUEUE} the global output has to wait until all
	 * previous outputs on all effected audio environments are done. If the Output Behavior is
	 * {@link OutputBehavior#INTERRUPT_CURRENT INTERRUPT_CURRENT} or {@link OutputBehavior#INTERRUPT_ALL INTERRUPT_ALL}
	 * it interrupts all effected audio environments
	 * 
	 * Additionally due to technical restrictions a interruption of any of the effected audio environments will cause
	 * the complete global output on all environments to be interrupted.
	 * 
	 * @param audioToPlay
	 *            The audio to play.
	 * @param behavior
	 *            How to behave if there is some output already.
	 */
	void playAudioGlobaly(AudioInputStream audioToPlay, OutputBehavior behavior);

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
	void playAudio(AudioEnvironmentIdetifier identifier, AudioInputStream audioToPlay, OutputBehavior behavior);

	/**
	 * Interrupts the audio output on the audio environment specified by the given identifier.
	 * 
	 * If the environment is currently outputting a global audio, this will interrupt the output on all environments.
	 * 
	 * @param identifier
	 *            The identifier of the audio environment to interrupt.
	 */
	void interruptAudio(AudioEnvironmentIdetifier identifier);

	/**
	 * @return A list of {@link AudioEnvironmentIdetifier}s, each being the identifier of a registered audio environment
	 */
	List<AudioEnvironmentIdetifier> getAllRegisteredAudioEnvironments();

	/**
	 * Checks whether the audio environment described by the given identifier is currently outputting any audio.
	 * 
	 * @param identifier
	 *            The identifier of the audio environment to check
	 * @return Whether the audio environment is currently outputting.
	 */
	boolean isAudioEnvironmentCurrentlyOutputting(AudioEnvironmentIdetifier identifier);

	/**
	 * Get's a AudioInputStream with the microphone input from the audio environment described by the given identifier.
	 * 
	 * @param identifier
	 *            The identifier of the audio environment to get the input Stream from.
	 * @return The audio input stream of the audio environment.
	 */
	AudioInputStream getInputStreamOfAudioEnvironment(AudioEnvironmentIdetifier identifier);

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
