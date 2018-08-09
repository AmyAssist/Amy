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

package de.unistuttgart.iaas.amyassist.amy.core.speech.output;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;
import de.unistuttgart.iaas.amyassist.amy.core.speech.tts.TextToSpeech;

/**
 * This class outputs
 * 
 * @author Tim Neumann, Kai Menzel
 */
@Service(Output.class)
public class OutputImpl implements Output {

	@Reference
	private Logger logger;
	@Reference
	private TextToSpeech tts;
	@Reference
	private AudioManager am;

	private void outputAudioStream(AudioInputStream ais) {
		if (this.am.hasLocalAudioEnvironment()) {
			this.am.playAudio(this.am.getLocalAudioEnvironmentIdentifier(), ais, AudioManager.OutputBehavior.QUEUE);
		}
	}

	/**
	 * Method to Voice and Log output the input String
	 * 
	 * @param s
	 *            String that shall be said
	 */
	@Override
	public void voiceOutput(String s) {
		this.logger.info("saying: {}", s);
		outputAudioStream(this.tts.getMaryAudio(s));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output#soundOutput(de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds)
	 */
	@Override
	public void soundOutput(Sounds sound) {
		try {
			outputAudioStream(sound.getSoundData());
		} catch (IOException e) {
			this.logger.error("IO Exception when playing audio", e);
		}
	}

	/**
	 * This method stops the output immediately.
	 */
	@Override
	public void stopOutput() {
		if (this.am.hasLocalAudioEnvironment()) {
			this.am.stopAudioOutput(this.am.getLocalAudioEnvironmentIdentifier());
		}
	}

	@Override
	public boolean isCurrentlyOutputting() {
		if (!this.am.hasLocalAudioEnvironment())
			return this.am.isAudioEnvironmentCurrentlyOutputting(this.am.getLocalAudioEnvironmentIdentifier());
		return false;
	}
}
