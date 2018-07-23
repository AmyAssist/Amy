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
public class LocalAudioEnvironment implements AudioEnvironment {

	private TargetDataLine tdl;
	private SourceDataLine sdl;

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
		this.tdl = AudioSystem.getTargetDataLine(inputFormat);
		this.tdl.open(inputFormat);
		this.sdl = AudioSystem.getSourceDataLine(outputFormat);
		this.sdl.open(outputFormat);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.AudioEnvironment#getMicrophoneInputLine()
	 */
	@Override
	public TargetDataLine getMicrophoneInputLine() {
		return this.tdl;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.AudioEnvironment#getSpeakerOutputLine()
	 */
	@Override
	public SourceDataLine getSpeakerOutputLine() {
		return this.sdl;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.audio.AudioEnvironment#getAudioEnvironmentIdentifier()
	 */
	@Override
	public AudioEnvironmentIdetifier getAudioEnvironmentIdentifier() {
		return new AudioEnvironmentIdetifier() {

			@Override
			public boolean shouldPlayGlobalSound() {
				return true;
			}

			@Override
			public AudioEnvironmentType getAudioEnvironmentType() {
				return AudioEnvironmentType.LOCAL;
			}

			@Override
			public boolean equals(AudioEnvironmentIdetifier other) {
				return this.getClass().isInstance(other);
			}

			@Override
			public boolean equals(Object obj) {
				return this.getClass().isInstance(obj);
			}

			@Override
			public int hashCode() {
				return this.getClass().hashCode();
			}
		};
	}

}
