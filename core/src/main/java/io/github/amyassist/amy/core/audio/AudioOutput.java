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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of a queued audio ouput
 * 
 * @author Tim Neumann
 */
public class AudioOutput {
	private AudioInputStream ais;

	private Logger logger = LoggerFactory.getLogger(AudioOutput.class);

	/**
	 * Creates a audio output, with the input stream
	 * 
	 * @param inputStream
	 *            The audio input stream for this output. This can't be null. *
	 * @throws IllegalArgumentException
	 *             When the ais is null.
	 */
	public AudioOutput(AudioInputStream inputStream) {
		if (inputStream == null)
			throw new IllegalArgumentException("InputStream can't be null.");
		this.ais = inputStream;
	}

	/**
	 * @return The audio input stream associated with this output.
	 */
	public AudioInputStream getInputStream() {
		return this.ais;
	}

	/**
	 * @return The {@link AudioFormat} of this AudioOutput
	 */
	public AudioFormat getFormat() {
		return this.ais.getFormat();
	}

	/**
	 * Tries to close the input stream. If it is not possible to close the stream, the coresponding exception is logged
	 * and false is returned.
	 * 
	 * @return Whether closing was successful.
	 */
	public boolean tryToCloseStream() {
		try {
			this.ais.close();
			return true;
		} catch (IOException e) {
			this.logger.debug("IO Exception when closing stream", e);
			return false;
		}
	}
}
