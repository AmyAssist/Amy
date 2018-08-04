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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.audio.QueuedInputStream;

/**
 * A worker doing to audio input for a audio environment
 * 
 * @author Tim Neumann
 */
public class EnvironmentInputWorker implements Runnable {

	/** The size of buffer used */
	private static final int BYTE_BUFFER_SIZE = 1024;
	/** The maximum amount of time to wait for a input stream to consume new data in ms. */
	private static final int MAX_INPUT_WAIT_TIME = 10;
	/** The parent audio environment */
	private AudioEnvironment ae;

	private Logger logger = LoggerFactory.getLogger(EnvironmentInputWorker.class);

	/**
	 * Creates a new input worker for the given owner
	 * 
	 * @param owner
	 *            The audio environment owning this worker
	 */
	public EnvironmentInputWorker(AudioEnvironment owner) {
		this.ae = owner;
	}

	@Override
	public void run() {
		boolean done = false;
		while (!Thread.currentThread().isInterrupted() && !done) {
			try {
				done = doInputWork();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Reads up to {@link #BYTE_BUFFER_SIZE} bytes from the parent and writes them all queued input streams of the
	 * parent.
	 * <p>
	 * Uses {@link #doByteInput(byte)}, so it also ends streams as necessary and removes streams from the parents input
	 * stream list like described in the JavaDoc of that method.
	 * 
	 * @return Whether the end of the input has been reached.
	 * @throws InterruptedException
	 *             When the method was interrupted.
	 */
	protected boolean doInputWork() throws InterruptedException {
		byte[] buffer = new byte[BYTE_BUFFER_SIZE];
		int bytesRead = this.ae.readFromInput(buffer, 0, buffer.length);

		if (bytesRead < 0)
			return true;

		for (int i = 0; i < bytesRead; i++) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException();

			doByteInput(buffer[i]);
		}

		return false;
	}

	/**
	 * Copies the given byte to all queued input streams of the parent.
	 * <p>
	 * If a queue does not accept new bytes and another queue needs new bytes because it is almost empty, this method
	 * will end the stream of the blocking queue, by clearing it and inserting a single -1.
	 * <p>
	 * Also removes ended and closed queues from the parents input stream list.
	 * 
	 * @param b
	 *            The byte to write.
	 * @throws InterruptedException
	 *             When the method was interrupted.
	 */
	protected void doByteInput(byte b) throws InterruptedException {
		boolean atLeastOneFull = false;
		/*
		 * The smallest amount of elements in any queue. This is used to check whether one queue is running dry because
		 * another one is blocking.
		 */
		int lowestSize = BYTE_BUFFER_SIZE;

		while (atLeastOneFull) {
			List<QueuedInputStream> toRemove = new ArrayList<>();
			atLeastOneFull = false;
			for (QueuedInputStream qis : this.ae.getInputStreams()) {
				if (Thread.currentThread().isInterrupted())
					throw new InterruptedException();

				// Remove closed streams
				if (qis.isClosed()) {
					toRemove.add(qis);
					continue;
				}

				// toUnsignedInt makes sure the conversion is the reverse from casting a int between 0 and 255 to a
				// byte.
				Integer value = Byte.toUnsignedInt(b);

				// Try to add new value to queue.
				boolean success = qis.getQueue().offer(value, MAX_INPUT_WAIT_TIME, TimeUnit.MILLISECONDS);

				// If it failed, try again or end the stream
				if (!success) {
					if (lowestSize < BYTE_BUFFER_SIZE / 10) {
						qis.getQueue().clear();
						qis.getQueue().put(-1);
						toRemove.add(qis);
						this.logger.warn("Full queue. Another queue needs bytes. Ending stream.");
					} else {
						atLeastOneFull = true;
					}
				}

				// Update the lowest size
				if (qis.getQueue().size() < lowestSize) {
					lowestSize = qis.getQueue().size();
				}
			}

			this.ae.getInputStreams().removeAll(toRemove);
		}
	}

}
