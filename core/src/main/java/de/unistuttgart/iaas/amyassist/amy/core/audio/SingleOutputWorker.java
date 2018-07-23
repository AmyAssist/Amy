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

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A worker responsible for copying bytes from a {@link AudioInputStream} to a single {@link SourceDataLine}.
 * 
 * @author Tim Neumann
 */
public class SingleOutputWorker implements Runnable {

	private static final int BYTE_BUFFER_SIZE = 1024;

	private Logger logger = LoggerFactory.getLogger(SingleOutputWorker.class);

	private AudioOutput src;
	private SourceDataLine tgt;

	/**
	 * Creates a new single output worker for the given source and target
	 * 
	 * @param source
	 *            The audio source to read from
	 * @param target
	 *            The audio target to write to
	 */
	public SingleOutputWorker(AudioOutput source, SourceDataLine target) {
		this.src = source;
		this.tgt = target;
	}

	@Override
	public void run() {
		try {
			boolean finished = moveBytes(this.src.getInpuStream(), this.tgt);
			if (finished) {
				this.src.setFinished();
				this.src.getInpuStream().close();
			}
		} catch (IOException e) {
			this.logger.warn("IO Exception in worker", e);
		}
	}

	/**
	 * Moves bytes from ais to sdl while not interrupted and not finished.
	 * 
	 * @param ais
	 *            The stream to copy from
	 * @param sdl
	 *            The line to copy to
	 * @return Whether it finished.
	 * @throws IOException
	 *             If an input or output error occurs while reading from ais.
	 */
	private boolean moveBytes(AudioInputStream ais, SourceDataLine sdl) throws IOException {
		byte[] buffer = new byte[BYTE_BUFFER_SIZE];
		int readBytes = 0;

		while (!Thread.currentThread().isInterrupted() && readBytes >= 0) {
			readBytes = ais.read(buffer, 0, BYTE_BUFFER_SIZE);

			if (readBytes > 0) {
				sdl.write(buffer, 0, readBytes);
			}
		}
		return (readBytes < 0);
	}

}
