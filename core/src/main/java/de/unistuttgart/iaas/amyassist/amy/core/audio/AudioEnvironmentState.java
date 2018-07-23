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

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Future;

/**
 * This represents the state of a audio environment. Used by the audio manager service.
 * 
 * @author Tim Neumann
 */
public class AudioEnvironmentState {
	/** The audio environment this state is for */
	private AudioEnvironment ae;
	/** The output Queue for the audio environment */
	private Deque<AudioOutput> outputQueue;
	/** The current output worker for the audio environment */
	private Future<?> currentOutputWorker;
	/** The current audio output for the audio environment */
	private AudioOutput currentStream;
	/** Whether the microphone input line is being consumed. */
	private boolean microphoneLineConsumed;

	/**
	 * Creates a new audio environment state for the given environment
	 * 
	 * @param environment
	 *            The environment this state is for.
	 */
	public AudioEnvironmentState(AudioEnvironment environment) {
		this.ae = environment;
		this.outputQueue = new ConcurrentLinkedDeque<>();
		this.currentOutputWorker = null;
		this.currentStream = null;
		this.microphoneLineConsumed = false;
	}

	/**
	 * Get's {@link #outputQueue outputQueue}
	 * 
	 * @return outputQueue
	 */
	public Deque<AudioOutput> getOutputQueue() {
		return this.outputQueue;
	}

	/**
	 * Get's {@link #currentOutputWorker currentOutputWorker}
	 * 
	 * @return currentOutputWorker
	 */
	public Future<?> getCurrentOutputWorker() {
		return this.currentOutputWorker;
	}

	/**
	 * Set's {@link #currentOutputWorker currentOutputWorker}
	 * 
	 * @param currentOutputWorker
	 *            currentOutputWorker
	 */
	public void setCurrentOutputWorker(Future<?> currentOutputWorker) {
		this.currentOutputWorker = currentOutputWorker;
	}

	/**
	 * Get's {@link #currentStream currentStream}
	 * 
	 * @return currentStream
	 */
	public AudioOutput getCurrentStream() {
		return this.currentStream;
	}

	/**
	 * Set's {@link #currentStream currentStream}
	 * 
	 * @param currentStream
	 *            currentStream
	 */
	public void setCurrentStream(AudioOutput currentStream) {
		this.currentStream = currentStream;
	}

	/**
	 * Get's {@link #ae ae}
	 * 
	 * @return ae
	 */
	public AudioEnvironment getAe() {
		return this.ae;
	}

	/**
	 * Get's {@link #microphoneLineConsumed microphoneLineConsumed}
	 * 
	 * @return microphoneLineConsumed
	 */
	public boolean isMicrophoneLineConsumed() {
		return this.microphoneLineConsumed;
	}

	/**
	 * Set's {@link #microphoneLineConsumed microphoneLineConsumed}
	 * 
	 * @param microphoneLineConsumed
	 *            microphoneLineConsumed
	 */
	public void setMicrophoneLineConsumed(boolean microphoneLineConsumed) {
		this.microphoneLineConsumed = microphoneLineConsumed;
	}

}
