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

package de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager;

/**
 * Handler that handles SpeechRecognition System intern commands
 * 
 * @author Kai Menzel
 */
public abstract class AbstractRecognitionResultHandler implements RecognitionResultHandler {

	private final Logger logger = LoggerFactory.getLogger(SpeechRecognizerManager.class);

	private SpeechRecognitionResultManager srManager;
	private Grammar grammar;
	private Grammar nextGrammar;

	/**
	 * @param srManager
	 *            Manager Object which handles this ResultHandler
	 * @param grammar
	 *            Grammar this ResultHandler handles
	 */
	public AbstractRecognitionResultHandler(SpeechRecognitionResultManager srManager, Grammar grammar) {
		this.srManager = srManager;
		this.grammar = grammar;
	}

	/**
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.RecognitionResultHandler#isRecognitionThreadRunning()
	 */
	@Override
	public boolean isRecognitionThreadRunning() {
		return this.srManager.isRecognitionThreadRunning();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.RecognitionResultHandler#handle(String
	 *      result)
	 */
	@Override
	public void handle(String result) {
		if (!predefinedInputHandling(result) && !checkGrammarSwitch(result)) {
			this.srManager.handleCommand(result);
			if (this.srManager.isSingleCallActive()) {
				this.srManager.handleSingleCallListeningState(false);
			}
		}

	}

	/**
	 * Handles the Recognizer Specific Actions that trigger before giving the input to the inputHandler. Mainly waking
	 * up and going to Sleep
	 * 
	 * @param result
	 *            Recognized String
	 * @return true if the result is an predefined one
	 */
	private boolean predefinedInputHandling(String result) {
		if (result.equals(Constants.SHUT_UP) || this.srManager.isSoundPlaying()) {
			if (result.equals(Constants.SHUT_UP) && this.srManager.isSoundPlaying()) {
				this.srManager.stopOutput();
			}
			return true;
		}
		return environmentSpecificInputHandling(result, this.srManager);
	}

	/**
	 * Handles the Environment Specific Actions that trigger before giving the input to the inputHandler. Mainly waking
	 * up and going to Sleep
	 * 
	 * @param result
	 *            Recognized String
	 * @param manager
	 *            Manager Object which handles this ResultHandler
	 * @return true if the result is an predefined one
	 */
	protected abstract boolean environmentSpecificInputHandling(String result, SpeechRecognitionResultManager manager);

	/**
	 * check if the Result is a keyword for a specific GrammarSwitch
	 * 
	 * @param result
	 *            SpeechRecognitionResult
	 * @return true if switch will be initialized
	 */
	private boolean checkGrammarSwitch(String result) {
		if (!this.grammar.getSwitchList().isEmpty()) {
			for (Map.Entry<String, Grammar> entry : this.grammar.getSwitchList().entrySet()) {
				if (result.equalsIgnoreCase(entry.getKey())) {
					this.nextGrammar = entry.getValue();
					this.srManager.setRecognitionThreadRunning(false);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.RecognitionResultHandler#initiateChange()
	 */
	@Override
	public void initiateChange() {
		if (this.nextGrammar != null) {
			this.srManager.handleGrammarSwitch(this.nextGrammar);
			this.nextGrammar = null;
		} else {
			this.logger.info("Recognizer stopped");
		}

	}

}
