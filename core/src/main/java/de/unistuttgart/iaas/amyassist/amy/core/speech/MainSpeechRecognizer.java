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

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import javax.sound.sampled.AudioInputStream;

/**
 * Class that translate Aduio-Input into Strings, powered by CMU Sphinx -
 * https://cmusphinx.github.io/ which is Licenced under BSD
 * 
 * @author Kai Menzel
 */
public class MainSpeechRecognizer extends SpeechRecognizer {

	/**
	 * @param audioUI
	 * @param grammar
	 * @param inputHandler
	 * @param ais
	 */
	public MainSpeechRecognizer(AudioUserInteraction audioUI, Grammar grammar, SpeechInputHandler inputHandler,
			AudioInputStream ais) {
		super(audioUI, grammar, inputHandler, ais);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#predefinedInputHandling()
	 */
	@Override
	protected void predefinedInputHandling() {
		if(this.speechRecognitionResult.equals(this.audioUI.getSHUTDOWN())) {
			this.tts.stopOutput();
		 } else if (this.speechRecognitionResult.equals(this.audioUI.getWAKEUP())) {
			this.listening = true;
			say("waking up");
		} else if (this.speechRecognitionResult.startsWith(this.audioUI.getWAKEUP() + " ")) {
			this.listening = true;
			this.makeDecision(this.speechRecognitionResult.replaceFirst(this.audioUI.getWAKEUP() + " ", ""));
		} else if (this.listening) {
			if (this.speechRecognitionResult.equals(this.audioUI.getGOSLEEP())) {
				this.listening = false;
				say("now sleeping");
			} else {
				this.makeDecision(this.speechRecognitionResult);
			}
		}
	}

	

}
