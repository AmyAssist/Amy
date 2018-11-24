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

package io.github.amyassist.amy.core.audio;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A queued input stream used for splitting the audio input to multiple input streams.
 * 
 * @author Tim Neumann
 */
public class QueuedInputStream extends InputStream {

	private static final int QUEUE_SIZE = 10000;

	/**
	 * The queue of this input stream. Every normal value v should follow this rule: 0<=v<255 A -1 signals the end of
	 * this stream. Other values are not allowed.
	 */
	private BlockingQueue<Integer> queue;

	/**
	 * Whether the queue has been closed.
	 */
	private boolean closed;

	/**
	 * Whether this stream has come to an end.
	 */
	private boolean ended;

	/**
	 * Whether to automatically end this stream, when the queue runs dry.
	 */
	private boolean autoEnding;

	/**
	 * Initializes this queued input stream
	 */
	public QueuedInputStream() {
		this.queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
		this.closed = false;
	}

	@Override
	public int read() throws IOException {
		if (this.closed)
			throw new IOException("Stream closed.");
		if (this.ended)
			return -1;
		if (this.autoEnding && this.queue.isEmpty()) {
			this.ended = true;
			return -1;
		}
		try {
			int i = this.queue.take();
			if (i == -1) {
				this.ended = true;
			}
			return i;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new InterruptedIOException();
		}
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		if (this.closed)
			return 0;
		return this.queue.size();
	}

	/**
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		this.closed = true;
	}

	/**
	 * Get's {@link #closed closed}
	 * 
	 * @return closed
	 */
	public boolean isClosed() {
		return this.closed;
	}

	/**
	 * Get's {@link #queue queue}. See {@link #queue queue} for information on what may be written into that.
	 * 
	 * @return queue
	 */
	public BlockingQueue<Integer> getQueue() {
		return this.queue;
	}

	/**
	 * Get's {@link #autoEnding autoEnding}
	 * 
	 * @return autoEnding
	 */
	public boolean isAutoEnding() {
		return this.autoEnding;
	}

	/**
	 * Set's {@link #autoEnding autoEnding}
	 * 
	 * @param autoEnding
	 *            autoEnding
	 */
	public void setAutoEnding(boolean autoEnding) {
		this.autoEnding = autoEnding;
	}
}
