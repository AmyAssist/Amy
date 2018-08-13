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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager.ListeningState;

/**
 * Handler that handles the local SpeechRecognition System intern commands for additional Grammars
 * 
 * @author Kai Menzel
 */
@Service
public class TempGrammarSpeechResultHandler extends AbstractSpeechResultHandler {

	/**
	 * Handles the Environment Specific Actions that trigger before giving the input to the inputHandler. Mainly waking
	 * up and going to Sleep
	 * 
	 * @param result
	 *            Recognized String
	 * @param srVar
	 *            variables Class
	 * @return true if the result is an predefined one
	 */
	@Override
	protected boolean environmentSpecificInputHandling(String result, SpeechRecognizerManager srVar) {
		if (result.equals(Constants.MULTI_CALL_STOP)) {
			srVar.setListeningState(ListeningState.NOT_LISTENING);
			srVar.setCurrentGrammar(Grammar.MAIN);
			return true;
		}
		return false;
	}

}
