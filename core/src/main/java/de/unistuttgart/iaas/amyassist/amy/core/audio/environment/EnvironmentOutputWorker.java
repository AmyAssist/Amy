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

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager.OutputBehavior;
import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioOutput;

/**
 * A worker doing to audio output for a audio environment
 * 
 * @author Tim Neumann
 */
public class EnvironmentOutputWorker implements Runnable {

	/** The size of buffer used */
	protected static final int BYTE_BUFFER_SIZE = 1024;
	/** The parent audio environment */
	private AudioEnvironment ae;
	/** Whether this worker should cancel the current output. */
	private boolean shouldCancel = false;
	/** Whether the stream should be discarded. */
	private boolean discardStream = false;
	/** Whether the worker is currently outputting. */
	private boolean currentlyOutputting = false;

	private Logger logger = LoggerFactory.getLogger(EnvironmentOutputWorker.class);

	/**
	 * Creates a new output worker for the given owner
	 * 
	 * @param owner
	 *            The audio environment owning this worker
	 */
	public EnvironmentOutputWorker(AudioEnvironment owner) {
		this.ae = owner;
	}

	/**
	 * Cancels the current output of this worker without stopping to worker.
	 * 
	 * @param pDiscardStream
	 *            Whether the stream that was being played should be discarded. If false this results in the stream
	 *            being added to the front of the queue again.
	 */
	public void cancel(boolean pDiscardStream) {
		if (!this.shouldCancel) {
			// Set discard stream first. Because as soon as cancel is set to true. The value of discard stream can be
			// used.
			this.discardStream = pDiscardStream;
			this.shouldCancel = true;
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

	@Override
	public void run() {
		AudioOutput currentStream = null;
		boolean needToCloseStream = true;
		while (!Thread.currentThread().isInterrupted()) {
			try {
				currentStream = this.ae.takeHeadOfOutputQueue();
				needToCloseStream = true;
				// If cancel was called while waiting for new input we don't want to cancel immediately.
				// We don't need to reset discard stream because it is not going to be used until cancel is called
				// again.
				this.shouldCancel = false;

				boolean finished = doOutputWork(currentStream.getInputStream());
				/*
				 * Do not need to check for should cancel. Because if not finished, there is only one way to get here:
				 * By canceling.
				 */
				if (!finished && !this.discardStream) {
					this.ae.playAudio(currentStream, OutputBehavior.QUEUE_PRIORITY);
					needToCloseStream = false;
				}
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			} catch (InterruptedIOException e2) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				this.logger.warn("IO Exception in audio worker.", e);
			} finally {
				this.currentlyOutputting = false;
				if (needToCloseStream && currentStream != null) {
					currentStream.tryToCloseStream();
				}
			}

		}
	}

	/**
	 * Do the work to output the given stream until the end of stream is reached, the current output cancellation state
	 * indicates to stop or the Thread is interrupted.
	 * 
	 * This method does not reset the cancellation sate.
	 * 
	 * @param stream
	 *            The input stream to work with
	 * 
	 * @return Whether the end of stream has been reached.
	 * 
	 * @throws IOException
	 *             When a IO error occurs with the input stream
	 * @throws InterruptedException
	 *             When the thread was interrupted
	 */
	protected boolean doOutputWork(AudioInputStream stream) throws IOException, InterruptedException {
		byte[] buffer = new byte[BYTE_BUFFER_SIZE];
		int readBytes = 0;

		this.currentlyOutputting = true;
		while (readBytes >= 0 && !this.shouldCancel) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException();

			readBytes = stream.read(buffer, 0, BYTE_BUFFER_SIZE);

			if (readBytes > 0) {
				this.ae.writeToOutput(buffer, 0, readBytes);
			}
		}
		this.currentlyOutputting = false;
		return (readBytes < 0);
	}

}
