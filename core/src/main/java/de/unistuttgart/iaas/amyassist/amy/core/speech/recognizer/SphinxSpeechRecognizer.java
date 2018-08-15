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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.speech.data.RuntimeExceptionRecognizerCantBeCreated;
import de.unistuttgart.iaas.amyassist.amy.core.speech.resulthandler.AbstractSpeechResultHandler;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 * Class that translate Audio-Input into Strings, powered by CMU Sphinx - https://cmusphinx.github.io/ which is Licenced
 * under BSD
 *
 * @author Kai Menzel
 */
public class SphinxSpeechRecognizer implements SpeechRecognizerType {

	/**
	 * logger for all the Speech Recognition classes
	 */
	private final Logger logger = LoggerFactory.getLogger(SphinxSpeechRecognizer.class);

	/**
	 * The Input Stream Source
	 */
	private final AudioInputStream ais;

	/**
	 * The Recognizer which Handles the Recognition
	 */
	private final StreamSpeechRecognizer recognizer;

	// -----------------------------------------------------------------------------------------------

	/**
	 * Creates the Recognizers and Configures them
	 * 
	 * @param file
	 *            Grammar to use in this Recognizer
	 * @param ais
	 *            set custom AudioInputStream.
	 */
	public SphinxSpeechRecognizer(File file, AudioInputStream ais) {
		this.ais = ais;

		// Create the Recognizer
		try {
			this.recognizer = new StreamSpeechRecognizer(this.createConfiguration(file));
		} catch (IOException e) {
			throw new RuntimeExceptionRecognizerCantBeCreated("StreamRecognizer can't be instantiated", e);
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * Starts and runs the recognizer calls makes Decision with the recognized String
	 */
	@Override
	public void getRecognition(AbstractSpeechResultHandler resultHandler) {

		// starts Recognition
		startRecognizer();

		this.logger.info(":: waiting for speech input");

		// wait for input from the recognizer
		SpeechResult speechResult = null;
		while (speechResult == null) {
			speechResult = this.recognizer.getResult();
		}
		/**
		 * If Thread is not Interrupted, get and use SpeechRecognitionResult
		 */
		String speechRecognitionResult = speechResult.getHypothesis();
		this.logger.debug("I recognized: {}", speechRecognitionResult);

		if (speechRecognitionResult.replace(" ", "").equals("") || speechRecognitionResult.equals("<unk>")) {
			getRecognition(resultHandler);
		} else {
			stopRecognizer();
			resultHandler.handle(speechRecognitionResult);
		}
	}

	// ===============================================================================================

	private boolean recognizerActive = false;

	private void startRecognizer() {
		if (!this.recognizerActive) {
			this.recognizer.startRecognition(this.ais);
			this.recognizerActive = true;
		}
	}

	private void stopRecognizer() {
		if (this.recognizerActive) {
			this.recognizer.stopRecognition();
			this.recognizerActive = false;
		}
	}

	// ===============================================================================================

	/**
	 * creates Configuration for the recognizers
	 * 
	 * @param file
	 *            Grammar of the Recognizer
	 *
	 * @return the configuration
	 * @throws FileNotFoundException
	 *             throw if the given Grammar File does not exist
	 */
	private Configuration createConfiguration(File file) throws FileNotFoundException {
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

		configuration.setUseGrammar(false);
		if (file != null && file.toString().endsWith(".gram")) {
			try {
				if (file.exists()) {
					configuration.setGrammarPath(file.getParentFile().toURI().toURL().toString());
					configuration.setGrammarName(file.getName().replace(".gram", ""));
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
