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

package de.unistuttgart.iaas.amyassist.amy.core.output.tts;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * Class that uses MaryTTS to turn a String to a Voice Output
 * 
 * @see <a href="https://github.com/marytts/marytts">MaryTTS</a>
 * 
 * @author Kai Menzel
 */
public interface TextToSpeech {

	/**
	 * Method that returns an AudioInputStream to given String
	 * 
	 * @param s
	 *            String to voice
	 * @return AudioInputStream of String
	 */
	AudioInputStream getMaryAudio(String s);

	/**
	 * Method to get Mary's AudioFormat
	 * 
	 * @return AudioFormat of Mary (16kHz, Mono)
	 */
	AudioFormat getMaryAudioFormat();
}