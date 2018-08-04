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

import java.util.UUID;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * The local audio environment
 * 
 * @author Tim Neumann
 */
public class LocalAudioEnvironment extends AudioEnvironment {

	private SourceDataLine sdl;
	private TargetDataLine tdl;

	/**
	 * Creates a new local audio environment with the given audio formats.
	 * 
	 * @param inputFormat
	 *            The format of the microphone input line.
	 * @param outputFormat
	 *            The format of the speaker output line.
	 * @throws LineUnavailableException
	 *             When either the input or output line can't be opened with the given format.
	 */
	public LocalAudioEnvironment(AudioFormat inputFormat, AudioFormat outputFormat) throws LineUnavailableException {
		this.sdl = AudioSystem.getSourceDataLine(outputFormat);
		this.tdl = AudioSystem.getTargetDataLine(inputFormat);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#getAudioEnvironmentIdentifier()
	 */
	@Override
	public UUID getAudioEnvironmentIdentifier() {
		return UUID.randomUUID();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#readFromInput(byte[], int, int)
	 */
	@Override
	protected int readFromInput(byte[] buffer, int off, int len) {
		return this.tdl.read(buffer, off, len);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#writeToOutput(byte[], int, int)
	 */
	@Override
	protected void writeToOutput(byte[] buffer, int off, int len) {
		this.sdl.write(buffer, off, len);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#getOutputFormat()
	 */
	@Override
	public AudioFormat getOutputFormat() {
		return this.sdl.getFormat();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#getInputFormat()
	 */
	@Override
	public AudioFormat getInputFormat() {
		return this.tdl.getFormat();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#start()
	 */
	@Override
	public void start() {
		try {
			this.sdl.open(this.sdl.getFormat());
			this.tdl.open(this.tdl.getFormat());
		} catch (LineUnavailableException e) {
			throw new IllegalStateException(e);
		}

		this.sdl.start();
		this.tdl.start();

		super.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#stop()
	 */
	@Override
	public void stop() {
		super.stop();

		if (this.sdl.isActive()) {
			this.sdl.flush();
			this.sdl.stop();
		}

		if (this.tdl.isActive()) {
			this.tdl.flush();
			this.tdl.stop();
		}

		this.sdl.close();
		this.tdl.close();
	}
}
