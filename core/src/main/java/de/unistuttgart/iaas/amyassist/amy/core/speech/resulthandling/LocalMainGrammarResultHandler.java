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

import de.unistuttgart.iaas.amyassist.amy.core.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.Constants;
import de.unistuttgart.iaas.amyassist.amy.core.speech.api.SpeechRecognizerManager;

/**
 * TODO: Description
 * 
 * @author Kai Menzel
 */
public class LocalMainGrammarResultHandler extends RecognitionResultHandler {

	public LocalMainGrammarResultHandler(SpeechRecognizerManager srManager, Grammar grammar) {
		super(srManager, grammar);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.resulthandling.RecognitionResultHandler#predefinedInputHandling(java.lang.String)
	 */
	@Override
	protected boolean predefinedInputHandling(String result) {
		if (result.equals(Constants.SHUT_UP)) {
			this.srManager.stopOutput();
			return true;
		} else if (result.equals(Constants.WAKE_UP)) {
			this.srManager.handleListeningState(true);
			this.srManager.voiceOutput("waking up");
			return true;
		} else if (this.srManager.isListening()) {
			if (result.equals(Constants.GO_SLEEP)) {
				this.srManager.handleListeningState(false);
				this.srManager.voiceOutput("now sleeping");
				return true;
			}
			return false;
		}
		return true;
	}

}
