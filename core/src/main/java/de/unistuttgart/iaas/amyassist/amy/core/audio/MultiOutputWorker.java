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
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A worker responsible for copying bytes from a {@link AudioInputStream} to a single {@link SourceDataLine}. It is
 * important, that every one of the specified source data lines can consume the output, because otherwise the playback
 * will hang for all outputs.
 * 
 * @author Tim Neumann
 */
public class MultiOutputWorker implements Runnable {

	private static final int BYTE_BUFFER_SIZE = 1024;

	private Logger logger = LoggerFactory.getLogger(SingleOutputWorker.class);

	private AudioOutput src;
	private List<SourceDataLine> tgts;

	/**
	 * Creates a new single output worker for the given source and target
	 * 
	 * @param source
	 *            The audio source to read from
	 * @param targets
	 *            The audio targets to write to
	 */
	public MultiOutputWorker(AudioOutput source, List<SourceDataLine> targets) {
		this.src = source;
		this.tgts = targets;
	}

	@Override
	public void run() {
		try {
			boolean finished = moveBytes(this.src.getInpuStream(), this.tgts);
			if (finished) {
				this.src.setFinished();
				this.src.getInpuStream().close();
			}
		} catch (IOException e) {
			this.logger.warn("IO Exception in worker", e);
		}
	}

	/**
	 * Moves bytes from ais to all sdls while not interrupted and not finished.
	 * 
	 * @param ais
	 *            The stream to copy from
	 * @param sdls
	 *            The lines to copy to
	 * @return Whether it finished.
	 * @throws IOException
	 *             If an input or output error occurs while reading from ais.
	 */
	private boolean moveBytes(AudioInputStream ais, List<SourceDataLine> sdls) throws IOException {
		byte[] buffer = new byte[BYTE_BUFFER_SIZE];
		int readBytes = 0;

		while (!Thread.currentThread().isInterrupted() && readBytes >= 0) {
			readBytes = ais.read(buffer, 0, BYTE_BUFFER_SIZE);

			if (readBytes > 0) {
				for (SourceDataLine sdl : sdls) {
					sdl.write(buffer, 0, readBytes);
				}
			}
		}
		return (readBytes < 0);
	}

}
