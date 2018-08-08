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

/**
 * A worker doing to audio output for a audio environment
 * 
 * @author Tim Neumann
 */
public class EnvironmentOutputWorker implements Runnable {

	/** The size of buffer used */
	protected static final int BYTE_BUFFER_SIZE = 1024;
	/** The parent audio environment */
	private AbstractAudioEnvironment ae;
	/** Whether this worker should cancel the current output. */
	private boolean shouldCancel = false;
	/** Whether the stream should be closed. */
	private boolean closeStream = false;
	/** Whether the worker is currently outputting. */
	private boolean currentlyOutputting = false;
	/** The hook to be executed after the next cancellation */
	private PostCancellationHook hook = null;

	/**
	 * The lock to synchronize output. All write access to the cancel state as well as adding elements to queue need to
	 * be synchronized.
	 */
	protected Object outputLock = new Object();

	private Logger logger = LoggerFactory.getLogger(EnvironmentOutputWorker.class);

	/**
	 * Creates a new output worker for the given owner
	 * 
	 * @param owner
	 *            The audio environment owning this worker
	 */
	public EnvironmentOutputWorker(AbstractAudioEnvironment owner) {
		this.ae = owner;
	}

	/**
	 * Cancels the current output of this worker without stopping to worker.
	 * 
	 * @param pCloseStream
	 *            Whether the stream that was being played should be closed.
	 */
	public void cancel(boolean pCloseStream) {
		cancel(pCloseStream, null);
	}

	/**
	 * Cancels the current output of this worker without stopping the worker.
	 * 
	 * This version does not rein
	 * 
	 * @param pCloseStream
	 *            Whether the stream should be discarded.
	 * @param pHook
	 *            The hook executed after the cancellation before the next element from the queue is read. This method
	 *            is only called if a stream was really cancelled.
	 */
	public void cancel(boolean pCloseStream, PostCancellationHook pHook) {
		synchronized (this.outputLock) {
			if (!this.shouldCancel) {
				/*
				 * Set discard stream first. Because as soon as cancel is set to true. The value of discard stream can
				 * be used.
				 */
				this.closeStream = pCloseStream;
				this.hook = pHook;
				this.shouldCancel = true;
				this.logger.debug("Cancelling output.");
			}
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
				/*
				 * If cancel was called while waiting for new input we don't want to cancel immediately. We don't need
				 * to reset cancel stream or the hook because they are not going to be used until cancel is called
				 * again.
				 */
				synchronized (this.outputLock) {
					this.shouldCancel = false;
				}

				doOutputWork(currentStream.getInputStream());

				if (this.shouldCancel && !this.closeStream) {
					needToCloseStream = false;
				}
			} catch (InterruptedException | InterruptedIOException e1) {
				this.logger.trace("Interrupted.", e1);
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				this.logger.warn("IO Exception in audio worker.", e);
			} finally {
				this.currentlyOutputting = false;
				if (needToCloseStream && currentStream != null) {
					currentStream.tryToCloseStream();
				}
				if (this.shouldCancel && this.hook != null) {
					this.hook.execute(currentStream);
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

	/**
	 * A interface to specify a method to be called after the output has been cancelled.
	 * 
	 * @author Tim Neumann
	 */
	@FunctionalInterface
	public interface PostCancellationHook {
		/**
		 * Method that is executed.
		 * 
		 * @param cancelledStream
		 *            The output that was playing when the worker was cancelled. This may already be closed, if the
		 *            cancel specified to close the stream.
		 */
		void execute(AudioOutput cancelledStream);
	}
}
