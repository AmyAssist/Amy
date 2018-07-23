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

import javax.sound.sampled.AudioInputStream;

/**
 * A representation of a queued audio ouput
 * 
 * @author Tim Neumann
 */
public class AudioOutput {
	private boolean global;
	private boolean finished;
	private AudioInputStream ais;

	/**
	 * Creates a audio output, with the input stream
	 * 
	 * @param inputStream
	 *            The audio input stream for this output. This can't be null.
	 * @param pGlobal
	 *            Whether this output is global
	 * @throws IllegalArgumentException
	 *             When the ais is null.
	 */
	public AudioOutput(AudioInputStream inputStream, boolean pGlobal) {
		if (inputStream == null)
			throw new IllegalArgumentException("InputStream can't be null.");
		this.ais = inputStream;
		this.global = pGlobal;
		this.finished = false;
	}

	/**
	 * @return Whether this is a global audio output
	 */
	public boolean isGlobal() {
		return this.global;
	}

	/**
	 * @return The audio input stream associated with this output.
	 */
	public AudioInputStream getInpuStream() {
		return this.ais;
	}

	/**
	 * @return Whether this audio output is finished.
	 */
	public boolean isFinished() {
		return this.finished;
	}

	/**
	 * Marks this audio output as finished.
	 */
	public void setFinished() {
		this.finished = true;
	}
}
