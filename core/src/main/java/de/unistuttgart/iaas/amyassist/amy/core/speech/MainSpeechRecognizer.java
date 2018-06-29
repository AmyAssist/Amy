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
 * Class that translate Aduio-Input into Strings, powered by CMU Sphinx - https://cmusphinx.github.io/ which is Licenced
 * under BSD
 * 
 * @author Kai Menzel
 */
public class MainSpeechRecognizer extends SpeechRecognizer {

	/**
	 * Creates the Recognizers and Configures them
	 * 
	 * @param audioUI
	 *            Handler of all recognizers and the TTS
	 *
	 * @param grammar
	 *            Grammar to use in this Recognizer
	 * @param inputHandler
	 *            Handler which will handle the input
	 * @param ais
	 *            set custom AudioInputStream.
	 * @param voiceOutput
	 *            true if the TTS shall voice the Answer
	 */
	public MainSpeechRecognizer(AudioUserInteraction audioUI, Grammar grammar, SpeechInputHandler inputHandler,
			AudioInputStream ais, boolean voiceOutput) {
		super(audioUI, grammar, inputHandler, ais, voiceOutput);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#predefinedInputHandling(String result)
	 */
	@Override
	protected void predefinedInputHandling(String result) {
		if (result.equals(Constants.SHUT_UP)) {
			this.tts.stopOutput();
		} else if (result.equals(Constants.WAKE_UP)) {
			Constants.setSRListening(SpeechRecognitionListening.AWAKE);
			say("waking up");
		} else if (result.startsWith(Constants.WAKE_UP + " ")) {
			Constants.setSRListening(SpeechRecognitionListening.AWAKE);
			this.makeDecision(result.replaceFirst(Constants.WAKE_UP + " ", ""));
		} else if (Constants.isSRListening() == SpeechRecognitionListening.AWAKE) {
			if (result.equals(Constants.GO_SLEEP)) {
				Constants.setSRListening(SpeechRecognitionListening.ASLEEP);
				say("now sleeping");
			} else {
				this.makeDecision(result);
			}
		}
	}

}
