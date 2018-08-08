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

package de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler;

import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionStateVariables;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionStateVariables.ListeningState;

/**
 * Handler that handles SpeechRecognition System intern commands
 * 
 * @author Kai Menzel
 */
public abstract class AbstractSpeechResultHandler {

	private SpeechRecognitionStateVariables srVar;

	/**
	 * @param srVar
	 *            Variables Class
	 */
	public AbstractSpeechResultHandler(SpeechRecognitionStateVariables srVar) {
		this.srVar = srVar;
	}

	/**
	 * Method that handles the direct SR output
	 * 
	 * @param result
	 *            speechRecognitionResult
	 */
	public void handle(String result) {
		if (!isPredefinedInputHandling(result)) {

			this.srVar.handleSpeechResult(result);
			if (this.srVar.getListeningState() == ListeningState.SINGLE_CALL_LISTENING) {
				this.srVar.setListeningState(ListeningState.NOT_LISTENING);
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
	private boolean isPredefinedInputHandling(String result) {
		if (result.equals(Constants.SHUT_UP) || this.srVar.isSoundPlaying()) {
			if (result.equals(Constants.SHUT_UP) && this.srVar.isSoundPlaying()) {
				this.srVar.stopOutput();
			}
			return true;
		}
		return environmentSpecificInputHandling(result, this.srVar);
	}

	/**
	 * Handles the Environment Specific Actions that trigger before giving the input to the inputHandler. Mainly waking
	 * up and going to Sleep
	 * 
	 * @param result
	 *            Recognized String
	 * @param srVariables
	 *            variables Class
	 * @return true if the result is an predefined one
	 */
	protected abstract boolean environmentSpecificInputHandling(String result,
			SpeechRecognitionStateVariables srVariables);

	/**
	 * Getter
	 * 
	 * @return Current GrammarState
	 */
	public Grammar getCurrentGrammarState() {
		return this.srVar.getCurrentGrammarState();
	}
}
