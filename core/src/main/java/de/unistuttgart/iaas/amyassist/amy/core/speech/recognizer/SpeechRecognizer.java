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

import java.io.File;

import javax.sound.sampled.AudioInputStream;

import de.unistuttgart.iaas.amyassist.amy.core.speech.resulthandler.AbstractSpeechResultHandler;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR;

/**
 * Enum that holds the mainGrammar and the tempGrammar
 * 
 * @author Kai Menzel
 */
public enum SpeechRecognizer {
	/**
	 * The Main Grammar Recognition is currently Active
	 */
	MAIN,
	/**
	 * The Temp Grammar Recognition is currently Active
	 */
	TEMP,
	/**
	 * The Google Speech Recognition is currently Active
	 */
	GOOGLE;

	private boolean initiated = false;

	private AbstractSpeechResultHandler resultHandler;

	private SpeechRecognizerType recognizer;

	// Init

	public void initiateAsSphinxRecognizer(File grammar, AudioInputStream ais) {
		this.recognizer = new SphinxSpeechRecognizer(grammar, ais);
		this.initiated = true;
	}

	public void initiateAsSphinxRecognizer(File grammar, AudioInputStream ais,
			AbstractSpeechResultHandler resultHandler) {
		this.recognizer = new SphinxSpeechRecognizer(grammar, ais);
		this.resultHandler = resultHandler;
		this.initiated = true;
	}

	public void initiateAsRemoteRecognizer(RemoteSR recognizer) {
		this.recognizer = new GoogleSpeechRecognizer(recognizer);
		this.initiated = true;
	}

	public void initiateAsRemoteRecognizer(RemoteSR recognizer, AbstractSpeechResultHandler resultHandler) {
		this.recognizer = new GoogleSpeechRecognizer(recognizer);
		this.resultHandler = resultHandler;
		this.initiated = true;
	}

	// SetResultHandler

	/**
	 * Set's {@link #resultHandler resultHandler}
	 * 
	 * @param resultHandler
	 *            resultHandler
	 */
	public void setResultHandler(AbstractSpeechResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	// RequestSR

	public void requestSR(AbstractSpeechResultHandler resultHandler) {
		if (isInitiated()) {
			this.recognizer.getRecognition(resultHandler);
		} else {
			throw new RecognizerNotInitiatedException("The Recognizer had not been initiated");
		}

	}

	public void requestSR() {
		if (isInitiated()) {
			if (this.resultHandler != null) {
				this.recognizer.getRecognition(this.resultHandler);
			} else {
				throw new ResultHandlerNotSet("No Result Handler Set");
			}
		} else {
			throw new RecognizerNotInitiatedException("The Recognizer had not been initiated");
		}

	}

	// GetInitiatedState

	/**
	 * Get's {@link #initiated initiated}
	 * 
	 * @return initiated
	 */
	public boolean isInitiated() {
		return this.initiated;
	}

	// Exceptions

	public class RecognizerNotInitiatedException extends NullPointerException {

		/**
		 * @param string
		 */
		public RecognizerNotInitiatedException(String string) {
			super(string);
		}

	}

	public class ResultHandlerNotSet extends NullPointerException {

		/**
		 * @param string
		 */
		public ResultHandlerNotSet(String string) {
			super(string);
		}

	}
}
