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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.speech.data.RuntimeExceptionRecognizerCantBeCreated;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.RecognitionResultHandler;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 * Class that translate Audio-Input into Strings, powered by CMU Sphinx - https://cmusphinx.github.io/ which is Licenced
 * under BSD
 *
 * @author Kai Menzel
 */
public class SpeechRecognizer implements Runnable {

	/**
	 * logger for all the Speech Recognition classes
	 */
	private final Logger logger = LoggerFactory.getLogger(SpeechRecognizer.class);

	/**
	 * Grammar of the Current running Recognizer
	 */
	private final Grammar grammar;

	/**
	 * The Input Stream Source
	 */
	private final AudioInputStream ais;

	/**
	 * Handler who use the translated String for commanding the System
	 */
	private final RecognitionResultHandler resultHandler;

	/**
	 * The Recognizer which Handles the Recognition
	 */
	private final StreamSpeechRecognizer recognizer;

	// -----------------------------------------------------------------------------------------------

	/**
	 * Creates the Recognizers and Configures them
	 * 
	 * @param grammar
	 *            Grammar to use in this Recognizer
	 * @param resultHandler
	 *            specific Handler
	 * @param ais
	 *            set custom AudioInputStream.
	 */
	public SpeechRecognizer(Grammar grammar, RecognitionResultHandler resultHandler, AudioInputStream ais) {
		this.grammar = grammar;
		this.resultHandler = resultHandler;
		this.ais = ais;

		// Create the Recognizer
		try {
			this.recognizer = new StreamSpeechRecognizer(this.createConfiguration());
		} catch (IOException e) {
			throw new RuntimeExceptionRecognizerCantBeCreated("StreamRecognizer can't be instantiated", e);
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * @see java.lang.Runnable#run() Starts and runs the recognizer calls makeDecision() with the recognized String
	 */
	@Override
	public void run() {

		// starts Recognition
		this.recognizer.startRecognition(this.ais);

		this.logger.info("[INFORMATION] :: Speech Recognition activated");

		while (this.resultHandler.isRecognitionThreadRunning()) {

			// wait for input from the recognizer
			SpeechResult speechResult = getSpeechResult();

			/**
			 * If Thread is not Interrupted, get and use SpeechRecognitionResult
			 */
			if (speechResult != null) {
				String speechRecognitionResult = speechResult.getHypothesis();
				this.logger.debug("Result: {}", speechRecognitionResult);
				makeDecision(speechRecognitionResult);
			}
		}

		this.recognizer.stopRecognition();
		this.resultHandler.initiateChange();
	}

	// ===============================================================================================

	/**
	 * Gives Input to Handler checks if input is useful
	 * 
	 * @param result
	 *            the speechRecognitionResultString
	 */
	public void makeDecision(String result) {
		if (result.replace(" ", "").equals("") || result.equals("<unk>")) {
			return;
		}

		if (this.resultHandler.isRecognitionThreadRunning()) {
			this.resultHandler.handle(result);
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * wait for input from the recognizer
	 * 
	 * @return Result of VoiceInput or null if ThreadIsInterrupted
	 */
	private SpeechResult getSpeechResult() {
		SpeechResult speechResult = null;
		while (speechResult == null && this.resultHandler.isRecognitionThreadRunning()) {
			speechResult = this.recognizer.getResult();
		}
		return speechResult;
	}

	// ===============================================================================================

	/**
	 * creates Configuration for the recognizers
	 *
	 * @return the configuration
	 * @throws FileNotFoundException
	 *             throw if the given Grammar File does not exist
	 */
	private Configuration createConfiguration() throws FileNotFoundException {
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

		configuration.setUseGrammar(false);
		if (this.grammar != null && this.grammar.getFile() != null
				&& this.grammar.getFile().toString().endsWith(".gram")) {
			try {
				if (this.grammar.getFile().exists()) {
					configuration.setGrammarPath(this.grammar.getFile().getParentFile().toURI().toURL().toString());
					configuration.setGrammarName(this.grammar.getFile().getName().replace(".gram", ""));
					configuration.setUseGrammar(true);
				} else {
					throw new FileNotFoundException();
				}
			} catch (MalformedURLException e) {
				this.logger.error("Wrong URL Format", e);
			}
		}
		return configuration;
	}

	// ===============================================================================================

}
