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

import java.util.ArrayList;
import java.util.List;

import io.github.amyassist.amy.core.audio.QueuedInputStream;

/**
 * A worker doing to audio input for a audio environment
 * 
 * @author Tim Neumann
 */
public class EnvironmentInputWorker extends EnvironmentWorker {

	/** The size of buffer used */
	private static final int BYTE_BUFFER_SIZE = 1024;
	/** The parent audio environment */
	private AbstractAudioEnvironment ae;

	/**
	 * Creates a new input worker for the given owner
	 * 
	 * @param owner
	 *            The audio environment owning this worker
	 */
	public EnvironmentInputWorker(AbstractAudioEnvironment owner) {
		super("AE<" + owner.getAudioEnvironmentIdentifier().toString() + ">InputWorker");
		this.ae = owner;
	}

	@Override
	public void run() {
		boolean done = false;
		try {
			while (!this.shouldStop() && !done) {
				done = doInputWork();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// End all remaining input streams.
		for (QueuedInputStream qis : this.ae.getInputStreams()) {
			qis.setAutoEnding(true);
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
			if (this.shouldStop())
				throw new InterruptedException();

			doByteInput(buffer[i]);
		}

		return false;
	}

	/**
	 * Copies the given byte to all queued input streams of the parent.
	 * <p>
	 * Also removes closed queues from the parents input stream list.
	 * 
	 * @param b
	 *            The byte to write.
	 * @throws InterruptedException
	 *             When the method was interrupted.
	 */
	protected void doByteInput(byte b) throws InterruptedException {
		List<QueuedInputStream> toRemove = new ArrayList<>();

		// toUnsignedInt makes sure the conversion is the reverse from casting a int between 0 and 255 to a byte.
		Integer value = Byte.toUnsignedInt(b);

		for (QueuedInputStream qis : this.ae.getInputStreams()) {
			if (this.shouldStop())
				throw new InterruptedException();

			// Remove closed streams
			if (qis.isClosed()) {
				toRemove.add(qis);
				continue;
			}

			qis.getQueue().put(value);
		}
		this.ae.getInputStreams().removeAll(toRemove);
	}
}
