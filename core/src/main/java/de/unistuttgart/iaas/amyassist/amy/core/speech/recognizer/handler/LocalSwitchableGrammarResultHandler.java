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

import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.AbstractSpeechRecognizerManager;

/**
 * Handler that handles the local SpeechRecognition System intern commands for additional Grammars
 * 
 * @author Kai Menzel
 */
public class LocalSwitchableGrammarResultHandler extends AbstractRecognitionResultHandler {

	/**
	 * @param srManager
	 *            Manager Object which handles this ResultHandler
	 * @param grammar
	 *            Grammar this ResultHandler handles
	 */
	public LocalSwitchableGrammarResultHandler(AbstractSpeechRecognizerManager srManager, Grammar grammar) {
		super(srManager, grammar);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.AbstractRecognitionResultHandler#predefinedInputHandling(java.lang.String)
	 */
	@Override
	protected boolean predefinedInputHandling(String result) {
		if (result.equals(Constants.SHUT_UP)) {
			this.srManager.stopOutput();
			return true;
		} else if (result.equals(Constants.GO_SLEEP)) {
			this.srManager.handleListeningState(false);
			this.srManager.voiceOutput("now sleeping");
			this.srManager.handleGrammarSwitch(null);
			return true;
		}
		return false;
	}

}
