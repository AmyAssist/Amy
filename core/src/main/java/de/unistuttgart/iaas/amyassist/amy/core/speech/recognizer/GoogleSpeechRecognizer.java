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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.speech.resulthandler.AbstractSpeechResultHandler;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR.LaunchChromeException;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSRListener;

/**
 * Calls the remote SR Service
 * 
 * @author Kai Menzel
 */
public class GoogleSpeechRecognizer implements RemoteSRListener, SpeechRecognizerType {

	// --------------------------------------------------------------
	// Dependencies

	// --------------------------------------------------------------
	// Fields

	/**
	 * logger for all the Speech Recognition classes
	 */
	private final Logger logger = LoggerFactory.getLogger(GoogleSpeechRecognizer.class);

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
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognizerType#getRecognition(de.unistuttgart.iaas.amyassist.amy.core.speech.resulthandler.AbstractSpeechResultHandler)
	 */
	@Override
	public void getRecognition(AbstractSpeechResultHandler resultHandler) {
		this.resultHandler = resultHandler;
		
		if(!this.recognizer.requestSR()) {
			this.logger.info("A problem connecting to the remote Speech Recognition has occured.");
			while(!this.recognizer.requestSR()) {
				this.logger.info("trying to reconnect");
				try {
					this.recognizer.restart();
				} catch (LaunchChromeException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.logger.info("Reconnected");
		}
		
		this.logger.info("waiting for speech input");
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
