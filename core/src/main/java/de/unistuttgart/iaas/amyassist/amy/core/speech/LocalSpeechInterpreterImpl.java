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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.RecognizerCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager;

/**
 * Service that Controls the Local SpeechRecognition
 * 
 * @author Kai Menzel
 */
@Service(LocalSpeechInterpreter.class)
public class LocalSpeechInterpreterImpl implements LocalSpeechInterpreter, Runnable {

	@Reference
	private Logger logger;
	@Reference
	private SpeechRecognizerManager recognitionManager;
	@Reference
	private RecognizerCreator recognizerCreator;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.LocalSpeechInterpreter#start()
	 */
	@Override
	public void start() {
		(new Thread(this)).start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.LocalSpeechInterpreter#stop()
	 */
	@Override
	public void stop() {
		this.recognitionManager.setCurrentGrammar(null);
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!Grammar.MAIN.isInitiated()) {
			if (this.recognizerCreator.isRecognitionDisabled()) {
				return;
			}
			Thread.yield();
		}
		if (this.recognizerCreator.isRecognitionDisabled()) {
			this.logger.info("[INFORMATION] :: [Speech Recognition] :: Disabled");
		} else {
			this.recognitionManager.setCurrentGrammar(Grammar.MAIN);
		}

	}

}
