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
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFormat;

/**
 * A mock environment for testing
 * 
 * @author Tim Neumann
 */
public class TestingEnvironment extends AudioEnvironment {

	private AudioFormat f;
	private volatile BlockingQueue<Integer> iq;
	private volatile BlockingQueue<Byte> oq;
	private boolean inputEndReached = false;

	/**
	 * Initializes this testing environment
	 * 
	 * @param format
	 *            The audio format for input and output
	 * @param inputQueue
	 *            The input queue. Values between 0 and 255 are allowed and also -1 as eos.
	 * @param outputQueue
	 *            The output queue.
	 */
	public TestingEnvironment(AudioFormat format, BlockingQueue<Integer> inputQueue, BlockingQueue<Byte> outputQueue) {
		this.f = format;
		this.iq = inputQueue;
		this.oq = outputQueue;
	}

	private int read() throws InterruptedIOException {
		if (this.inputEndReached)
			return -1;
		Integer el;

		try {
			el = this.iq.take();
			int elR = el.intValue();
			if (elR == -1) {
				this.inputEndReached = true;
			}
			return elR;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new InterruptedIOException();
		}

	}

	@Override
	protected int readFromInput(byte[] b, int off, int len) throws InterruptedException {
		if (b == null)
			throw new NullPointerException();
		else if (off < 0 || len < 0 || len > b.length - off)
			throw new IndexOutOfBoundsException();
		else if (len == 0)
			return 0;

		int c;
		try {
			c = read();
		} catch (InterruptedIOException e) {
			throw new InterruptedException();
		}

		if (c == -1)
			return -1;
		b[off] = (byte) c;

		int i = 1;
		try {
			for (; i < len; i++) {
				c = read();
				if (c == -1) {
					break;
				}
				b[off + i] = (byte) c;
			}
		} catch (IOException ee) {
		}
		return i;
	}

	@Override
	protected void writeToOutput(byte[] buffer, int off, int len) {
		if (buffer == null)
			throw new NullPointerException("buffer was null");
		if (off < 0 || len < 0 || off + len > buffer.length)
			throw new IndexOutOfBoundsException(
					"Either off or len was less then 0 or the sum of both was bigger then the size of the buffer.");
		for (int i = off; i < off + len; i++) {
			this.oq.add(buffer[i]);
		}
	}

	@Override
	public AudioFormat getOutputFormat() {
		return this.f;
	}

	@Override
	public AudioFormat getInputFormat() {
		return this.f;
	}

	@Override
	public UUID getAudioEnvironmentIdentifier() {
		return new UUID(0, 1);
	}

}
