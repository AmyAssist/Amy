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

package de.unistuttgart.iaas.amyassist.amy.core.speech.resulthandling;

import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.api.SpeechRecognizerManager;

/**
 * TODO: Description
 * @author Kai Menzel
 */
public abstract class RecognitionResultHandler implements RecognitionResultHandlerInterface {
	
	protected SpeechRecognizerManager srManager;
	protected Grammar grammar;
	
	/**
	 * 
	 */
	public RecognitionResultHandler(SpeechRecognizerManager srManager, Grammar grammar) {
		this.srManager = srManager;
		this.grammar = grammar;
	}

	/**
	 * @throws InterruptedException 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.resulthandling.RecognitionResultHandlerInterface#handle(String result)
	 */
	@Override
	public void handle(String result){
		if(!predefinedInputHandling(result)) {
			if(!checkGrammarSwitch(result)) {
				this.srManager.handleCommand(result);
			}
		}

	}
	
	/**
	 * Handles the Recognizer Specific Actions that trigger before giving the input to the inputHandler. Mainly waking
	 * up and going to Sleep
	 * 
	 * @param result
	 *            Recognized String
	 * @return 
	 */
	protected abstract boolean predefinedInputHandling(String result);
	
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
					this.srManager.handleGrammarSwitch(entry.getValue());
					return true;
				}
			}
		}
		return false;
	}

}
