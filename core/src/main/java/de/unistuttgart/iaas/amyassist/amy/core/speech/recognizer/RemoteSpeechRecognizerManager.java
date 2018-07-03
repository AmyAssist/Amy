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

import javax.sound.sampled.AudioInputStream;

import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.RecognitionResultHandlerInterface;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.RemoteResultHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.tts.Output;

/**
 * Class that manages the Recognizers belonging to a given AudioInputStream
 * 
 * @author Kai Menzel
 */
public class RemoteSpeechRecognizerManager extends SpeechRecognizerManager {

	/**
	 * Object that handles All Recognizers with the given AudioInputStream
	 * 
	 * @param ais
	 *            AudioInputStream for the SpeechRecognition
	 * @param inputHandler
	 *            Handler that will handle the SpeechRecognitionResult
	 * @param output
	 *            Output Object where to Output the result of the Recognizer
	 * @param grammarData
	 *            DataSet of all GrammarObjects
	 * 
	 */
	public RemoteSpeechRecognizerManager(AudioInputStream ais, SpeechInputHandler inputHandler, Output output,
			GrammarObjectsCreator grammarData) {
		super(ais, inputHandler, output, grammarData);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognizerManager#getMainResultHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar)
	 */
	@Override
	protected RecognitionResultHandlerInterface getMainResultHandler(Grammar grammar) {
		return new RemoteResultHandler(this, grammar);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognizerManager#getResultHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar)
	 */
	@Override
	protected RecognitionResultHandlerInterface getResultHandler(Grammar grammar) {
		return new RemoteResultHandler(this, grammar);
	}

}
