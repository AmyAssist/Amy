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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.LocalAudio;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.RuntimeExceptionRecognizerCantBeCreated;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 * Class that translate Audio-Input into Strings, powered by CMU Sphinx - https://cmusphinx.github.io/ which is Licenced
 * under BSD
 *
 * @author Kai Menzel
 */
@Service(SphinxSpeechRecognizer.class)
public class SphinxSpeechRecognizer implements SpeechRecognizer, Runnable {

	@Reference
	private Logger logger;
	@Reference
	private AudioManager audioManager;
	@Reference
	private LocalAudio localAudio;
	@Reference
	private SpeechResultPreHandler resultHandler;

	private AudioInputStream audioInputStream;
	private StreamSpeechRecognizer recognizer;
	private String result;

	private boolean recognizerActive = false;

	/**
	 * 
	 * Create a Recognizer
	 * 
	 * @param grammar
	 *            Grammar to use in this Recognizer
	 */
	public void setGrammar(File grammar) {
		stopRecognizer();
		try {
			this.recognizer = new StreamSpeechRecognizer(this.createConfiguration(grammar));
		} catch (IOException e) {
			throw new RuntimeExceptionRecognizerCantBeCreated("StreamRecognizer can't be instantiated", e);
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * Starts and runs the recognizer calls makes Decision with the recognized String
	 */
	@Override
	public void requestRecognition() {

		// starts Recognition
		startRecognizer();

		this.logger.info(":: waiting for speech input");

		requestNewSrResult();
	}

	private void requestNewSrResult() {
		// wait for input from the recognizer
		SpeechResult speechResult = null;
		while (speechResult == null) {
			speechResult = this.recognizer.getResult();
		}

		/**
		 * If Thread is not Interrupted, get and use SpeechRecognitionResult
		 */
		this.result = speechResult.getHypothesis();

		// check empty answer
		if (!(this.result.replace(" ", "").equals("") || this.result.equals("<unk>"))) {
			stopRecognizer();
		} else {
			requestNewSrResult();
		}
	}

	private void startRecognizer() {
		if (!this.recognizerActive) {
			this.recognizer.startRecognition(getAudioInputStream());
			this.recognizerActive = true;
		}
	}

	private void stopRecognizer() {
		if (this.recognizerActive) {
			this.recognizerActive = false;
			this.recognizer.stopRecognition();
			(new Thread(this)).start();
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.resultHandler.handle(this.result);
	}

	private AudioInputStream getAudioInputStream() {
		if (this.audioInputStream == null) {
			this.audioInputStream = createNewAudioInputStream();
		}
		return this.audioInputStream;
	}

	/**
	 * starts the default AudioInputStream
	 * 
	 * @return AudioInputStream from the local mic
	 * @throws RuntimeException
	 *             If no audio environment could be found.
	 */
	private AudioInputStream createNewAudioInputStream() {
		try {
			return this.audioManager
					.getInputStreamOfAudioEnvironment(this.localAudio.getLocalAudioEnvironmentIdentifier());
		} catch (NoSuchElementException e) {
			throw new IllegalStateException("Could not get local audio environment", e);
		}
	}

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

}
