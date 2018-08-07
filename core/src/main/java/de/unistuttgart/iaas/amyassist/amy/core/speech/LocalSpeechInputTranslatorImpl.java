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

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.SpeechRecognitionStateVariables;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler.MainGrammarSpeechResultHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler.TempGrammarSpeechResultHandler;

/**
 * Service that Controls the Local SpeechRecognition
 * 
 * @author Kai Menzel
 */
@Service(LocalSpeechInputTranslator.class)
public class LocalSpeechInputTranslatorImpl implements LocalSpeechInputTranslator {

	@Reference
	private Logger logger;

	@Reference
	private SpeechRecognitionStateVariables srVar;

	@PostConstruct
	private void init() {
		this.srVar.setMainResultHandler(new MainGrammarSpeechResultHandler(this.srVar));
		this.srVar.setTempResultHandler(new TempGrammarSpeechResultHandler(this.srVar));
		this.srVar.init();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.LocalSpeechInputTranslator#start()
	 */
	@Override
	public void start() {
		this.srVar.setCurrentGrammar(Grammar.MAIN);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.LocalSpeechInputTranslator#stop()
	 */
	@Override
	public void stop() {
		this.srVar.setCurrentGrammar(Grammar.NONE);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.LocalSpeechInputTranslator#changeGrammar(de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar)
	 */
	@Override
	public void changeGrammar(Grammar grammar) {
		if (this.srVar.getCurrentGrammarState() != grammar) {
			this.srVar.setCurrentGrammar(grammar);
		}

	}

}
