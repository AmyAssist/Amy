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

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioOutput;
import de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment.CancellationState;

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

	@Override
	public void run() {
		AudioOutput currentStream = null;
		boolean needToCloseStream = true;
		while (!Thread.currentThread().isInterrupted()) {
			try {
				currentStream = this.ae.getOutputQueue().take();
				needToCloseStream = true;
				// If cancel was called while waiting for new input we don't want to cancel immediately.
				this.ae.resetCancellationState();

				boolean finished = doOutputWork(currentStream.getInputStream());
				if (this.ae.getOutputCancellationState().isShouldCancel()
						&& !this.ae.getOutputCancellationState().isDiscardStream() && !finished) {
					this.ae.getOutputQueue().addFirst(currentStream);
					needToCloseStream = false;
				}
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			} catch (InterruptedIOException e2) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				this.logger.warn("IO Exception in audio worker.", e);
			} finally {
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
		CancellationState currentState = this.ae.getOutputCancellationState();

		this.ae.setCurrentlyOutputting(true);
		while (readBytes >= 0 && !currentState.isShouldCancel()) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException();

			readBytes = stream.read(buffer, 0, BYTE_BUFFER_SIZE);

			if (readBytes > 0) {
				this.ae.writeToOutput(buffer, 0, readBytes);
			}
			currentState = this.ae.getOutputCancellationState();
		}
		this.ae.setCurrentlyOutputting(false);
		return (readBytes < 0);
	}

}
