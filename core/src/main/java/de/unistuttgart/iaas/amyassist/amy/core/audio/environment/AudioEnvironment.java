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

package de.unistuttgart.iaas.amyassist.amy.core.audio.environment;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioOutput;
import de.unistuttgart.iaas.amyassist.amy.core.audio.QueuedInputStream;

/**
 * An Audio Environment, which does the AudioIO.
 * 
 * @author Tim Neumann
 */
public abstract class AudioEnvironment {

	/** The size of buffer used */
	protected static final int BYTE_BUFFER_SIZE = 1024;
	/** The maximum amount of time to wait for a input stream to consume new data in ms. */
	protected static final int MAX_INPUT_WAIT_TIME = 100;

	/** The output Queue for the audio environment */
	private BlockingDeque<AudioOutput> outputQueue;
	/** The list of streams to copy the input from this audio environment to. */
	private List<QueuedInputStream> inputStreams;
	/** The Thread used to transfer the output */
	private Thread outputThread;
	/** The Thread used to transfer the input */
	private Thread inputThread;
	/** The current output cancellation state */
	private volatile CancellationState outputCancellationState;
	/** Whether this environment is currently outputting */
	private volatile boolean currentlyOutputting;

	/**
	 * Initializes the audio environment
	 */
	public AudioEnvironment() {
		this.outputQueue = new LinkedBlockingDeque<>();
		this.inputStreams = new ArrayList<>();
		this.outputThread = new Thread(new EnvironmentOutputWorker(this),
				"AE<" + this.getAudioEnvironmentIdentifier().toString() + ">OutputThread");
		this.inputThread = new Thread(new EnvironmentInputWorker(this),
				"AE<" + this.getAudioEnvironmentIdentifier().toString() + ">InputThread");

		this.outputCancellationState = new CancellationState();
		this.currentlyOutputting = false;
	}

	/**
	 * Read from the input for this audio environment. For example from a microphone.
	 * 
	 * This method should behave like {@link InputStream#read(byte[], int, int)}
	 * 
	 * @param buffer
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset in array b at which the data is written.
	 * @param len
	 *            the maximum number of bytes to read.
	 * 
	 * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the
	 *         stream has been reached.
	 * @throws InterruptedException
	 *             When the Thread is interrupted while waiting for input
	 */
	protected abstract int readFromInput(byte[] buffer, int off, int len) throws InterruptedException;

	/**
	 * Write to the output of this audio environment. For example to a speaker.
	 * 
	 * This method should behave like {@link OutputStream#write(byte[], int, int)}.
	 * 
	 * @param buffer
	 *            the data
	 * @param off
	 *            the start offset in the data
	 * @param len
	 *            the number of bytes to write
	 */
	protected abstract void writeToOutput(byte[] buffer, int off, int len);

	/**
	 * @return The {@link AudioFormat} format for outputting. The {@link AudioOutput} given to
	 *         {@link #playAudio(AudioOutput, de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager.OutputBehavior)}
	 *         must have this format.
	 */
	public abstract AudioFormat getOutputFormat();

	/**
	 * @return The {@link AudioFormat} format for inputting. The {@link AudioInputStream} returned by
	 *         {@link #getAudioInputStream()} will have this format.
	 */
	public abstract AudioFormat getInputFormat();

	/**
	 * Get the audio environment identifier of this audio environment.
	 * 
	 * @return The identifier of this audio environment
	 */
	public abstract UUID getAudioEnvironmentIdentifier();

	/**
	 * Get's the current output cancellation state
	 * 
	 * @return The output state
	 */
	protected synchronized CancellationState getOutputCancellationState() {
		return this.outputCancellationState;
	}

	/**
	 * Reset's the current output cancellation state. This should only be called from the output thread.
	 */
	protected synchronized void resetCancellationState() {
		this.outputCancellationState = new CancellationState();
	}

	/**
	 * Set's the current output cancellation state to cancel if it not already is.
	 * 
	 * This will result of a cancellation of the current output
	 * 
	 * @param discardStream
	 *            Whether to discard the input stream when canceling
	 */
	protected synchronized void cancel(boolean discardStream) {
		if (!this.outputCancellationState.isShouldCancel()) {
			this.outputCancellationState = new CancellationState(discardStream);
		}
	}

	/**
	 * Stops the current output.
	 */
	public void stopOutput() {
		this.cancel(true);
	}

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
	public void playAudio(AudioOutput audioToPlay, AudioManager.OutputBehavior behavior) {

		if (!audioToPlay.getFormat().matches(getOutputFormat()))
			throw new IllegalArgumentException("AudioToPlay has the wrong AudioFormat.");

		switch (behavior) {
		case INTERRUPT_ALL:
			this.outputQueue.clear();
			this.cancel(true);
			this.outputQueue.addFirst(audioToPlay);
			break;
		case INTERRUPT_CURRENT:
			this.cancel(true);
			this.outputQueue.addFirst(audioToPlay);
			break;
		case QUEUE:
			this.outputQueue.add(audioToPlay);
			break;
		case QUEUE_PRIORITY:
			this.outputQueue.addFirst(audioToPlay);
			break;
		case SUSPEND:
			this.cancel(false);
			this.outputQueue.addFirst(audioToPlay);
			break;
		default:
			throw new IllegalStateException("Unknown behavior");
		}
	}

	/**
	 * Get's a {@link AudioInputStream} from this audio environment. This stream will contain the audio input (like
	 * microphone data) of this environment.
	 * 
	 * This stream needs to be read from fast enough. Otherwise this stream will end.
	 * 
	 * When this stream is no longer needed, it should be closed.
	 * 
	 * @return The stream.
	 */
	public AudioInputStream getAudioInputStream() {
		QueuedInputStream qis = new QueuedInputStream();
		this.inputStreams.add(qis);
		return new AudioInputStream(qis, getInputFormat(), AudioSystem.NOT_SPECIFIED);
	}

	/**
	 * Starts this audio environment
	 */
	public void start() {
		this.outputThread.start();
		this.inputThread.start();
	}

	/**
	 * Stops this audio environment
	 */
	public void stop() {
		this.outputThread.interrupt();
		this.inputThread.interrupt();

		try {
			this.outputThread.join();
			this.inputThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Get's {@link #currentlyOutputting currentlyOutputting}
	 * 
	 * @return currentlyOutputting
	 */
	public boolean isCurrentlyOutputting() {
		return this.currentlyOutputting;
	}

	/**
	 * Set's {@link #currentlyOutputting currentlyOutputting}
	 * 
	 * @param currentlyOutputting
	 *            currentlyOutputting
	 */
	protected void setCurrentlyOutputting(boolean currentlyOutputting) {
		this.currentlyOutputting = currentlyOutputting;
	}

	/**
	 * Get's {@link #inputStreams inputStreams}
	 * 
	 * @return inputStreams
	 */
	protected List<QueuedInputStream> getInputStreams() {
		return this.inputStreams;
	}

	/**
	 * Get's {@link #outputQueue outputQueue}
	 * 
	 * @return outputQueue
	 */
	protected BlockingDeque<AudioOutput> getOutputQueue() {
		return this.outputQueue;
	}

	/**
	 * The possible states regarding the cancellation of the output a environment can be in.
	 * 
	 * @author Tim Neumann
	 */
	protected class CancellationState {
		/** Whether the output should be cancelled */
		private final boolean shouldCancel;
		/** Whether the stream should be discarded */
		private final boolean discardStream;

		/**
		 * Initializes a running state
		 */
		public CancellationState() {
			this.shouldCancel = false;
			this.discardStream = false;
		}

		/**
		 * Initializes a cancel state
		 * 
		 * @param pDiscardStream
		 *            whether the stream should be discarded after cancellation
		 */
		public CancellationState(boolean pDiscardStream) {
			this.shouldCancel = true;
			this.discardStream = pDiscardStream;
		}

		/**
		 * Get's {@link #shouldCancel shouldCancel}
		 * 
		 * @return shouldCancel
		 */
		public boolean isShouldCancel() {
			return this.shouldCancel;
		}

		/**
		 * Get's {@link #discardStream discardStream}
		 * 
		 * @return discardStream
		 */
		public boolean isDiscardStream() {
			return this.discardStream;
		}

	}
}
