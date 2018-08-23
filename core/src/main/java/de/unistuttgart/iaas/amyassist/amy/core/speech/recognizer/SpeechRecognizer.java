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

	/**
	 * request an Speech To Text of the Recognizer, result will be handled by the given resultHandler
	 * 
	 * @param requestedResultHandler
	 *            handles stt Result
	 */
	public void requestSR(AbstractSpeechResultHandler requestedResultHandler) {
		if (isInitiated()) {
			this.recognizer.getRecognition(requestedResultHandler);
		} else {
			throw new RecognizerNotInitiatedException("The Recognizer had not been initiated");
		}

	}

	/**
	 * request an Speech To Text of the Recognizer, result will be handled by the default resultHandler (default has to
	 * be set prior)
	 */
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

	/**
	 * Thrown to Signal that the wanted Recognizer was not (Correctly) Initiated
	 * 
	 * @author Kai Menzel
	 */
	public class RecognizerNotInitiatedException extends NullPointerException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param string
		 *            Exception-Message
		 */
		public RecognizerNotInitiatedException(String string) {
			super(string);
		}

	}

	/**
	 * Thrown to Signal that no Handler was set to a Recognizer
	 * 
	 * @author Kai Menzel
	 */
	public class ResultHandlerNotSet extends NullPointerException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param string
		 *            Exceptio-Message
		 */
		public ResultHandlerNotSet(String string) {
			super(string);
		}

	}
}
