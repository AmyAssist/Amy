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

package de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer;

import de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler.AbstractSpeechResultHandler;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSRListener;

/**
 * Calls the remote SR Service
 * 
 * @author Kai Menzel
 */
public class GoogleSpeechRecognizer implements RemoteSRListener, SpeechRecognizer {

	// --------------------------------------------------------------
	// Dependencies

	// --------------------------------------------------------------
	// Fields

	/**
	 * Handler who use the translated String for commanding the System
	 */
	private AbstractSpeechResultHandler resultHandler;

	/**
	 * The Google Speech Service
	 */
	private RemoteSR recognizer;

	// --------------------------------------------------------------
	// Construcor

	/**
	 * Returns Recognition Result to given Handler from Given Recognizer
	 * 
	 * @param grammar
	 *            Grammar of this Recognizer
	 * @param resultHandler
	 *            ResultHandler of this Recognizer
	 * @param recognizer
	 *            The Remote Recognizer
	 */
	public GoogleSpeechRecognizer(RemoteSR recognizer) {
		this.recognizer = recognizer;

		this.recognizer.setListener(this);
	}

	// --------------------------------------------------------------
	// Init

	// --------------------------------------------------------------
	// Methods

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognizer#getRecognition(de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler.AbstractSpeechResultHandler)
	 */
	@Override
	public void getRecognition(AbstractSpeechResultHandler resultHandler) {
		this.resultHandler = resultHandler;
		this.recognizer.requestSR();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSRListener#remoteSRDidRecognizeSpeech(java.lang.String)
	 */
	@Override
	public void remoteSRDidRecognizeSpeech(String message) {
		this.resultHandler.handle(message);
	}

	// --------------------------------------------------------------
	// Getter

	// --------------------------------------------------------------
	// Setter

}
