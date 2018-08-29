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

package de.unistuttgart.iaas.amyassist.amy.core.speech.sphinx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.LocalAudio;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 * Class that translate Audio-Input into Strings, powered by CMU Sphinx - https://cmusphinx.github.io/ which is Licenced
 * under BSD
 *
 * @author Kai Menzel, Tim Neumann
 */
@Service(SphinxRecognizer.class)
public class SphinxRecognizer implements SpeechRecognizer, Runnable {

	@Reference
	private Logger logger;
	@Reference
	private AudioManager audioManager;
	@Reference
	private LocalAudio localAudio;
	@Reference
	private SphinxGrammarCreator grammarCreator;

	@Context(de.unistuttgart.iaas.amyassist.amy.core.di.Context.SPHINX_GRAMMAR)
	private String grammarName;

	private StreamSpeechRecognizer recognizer;

	private volatile boolean stop;
	private volatile boolean recognizeContinous;
	private volatile Consumer<String> handler;
	private volatile AudioInputStream ais;

	private Thread thread;

	@Override
	public void recognizeOnce(Consumer<String> resultHandler) {
		if (isCurrentlyRecognizing())
			throw new IllegalStateException("Already running");

		this.recognizeContinous = false;
		this.handler = resultHandler;

		this.thread = new Thread(this, "Sphinx Recognizer Once");
		this.thread.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#recognizeContinuously(java.util.function.Consumer)
	 */
	@Override
	public void recognizeContinuously(Consumer<String> resultHandler) {
		if (isCurrentlyRecognizing())
			throw new IllegalStateException("Already running");
		this.recognizeContinous = true;
		this.handler = resultHandler;

		this.thread = new Thread(this, "Sphinx Recognizer Continous");
		this.thread.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#stopContinuousRecgonition()
	 */
	@Override
	public void stopContinuousRecognition() {
		if (!this.recognizeContinous)
			throw new IllegalStateException("Not running continuous.");
		this.stop = true;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#isCurrentlyRecognizing()
	 */
	@Override
	public boolean isCurrentlyRecognizing() {
		return this.thread != null && this.thread.isAlive();
	}

	private void startRecognizer() {
		if (this.recognizer == null) {
			// First start
			try {
				this.recognizer = new StreamSpeechRecognizer(createConfiguration());
			} catch (IOException e) {
				throw new IllegalStateException("Error initializing ", e);
			}
		}
		this.ais = getAudioInputStream();
		this.recognizer.startRecognition(this.ais);
	}

	private void stopRecognizer() {
		this.recognizer.stopRecognition();
		try {
			this.ais.close();
		} catch (IOException e) {
			this.logger.error("Failed to close ais", e);
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.stop = false;
		startRecognizer();
		this.logger.info("Sphinx listening");
		String result;
		while (!Thread.currentThread().isInterrupted() && !this.stop) {
			result = processResult(this.recognizer.getResult());
			if (result != "") {
				this.handler.accept(result);
				if (!this.recognizeContinous) {
					this.stop = true;
				}
			}
		}
		this.logger.info("Sphinx not listening anymore");
		stopRecognizer();
	}

	private String processResult(SpeechResult r) {
		String hypothesis = r.getHypothesis();
		hypothesis = hypothesis.trim();
		if (hypothesis.equals("<unk>"))
			return "";
		return hypothesis;
	}

	private AudioInputStream getAudioInputStream() {
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
	 * @return the configuration
	 * @throws FileNotFoundException
	 *             When the file was not found.
	 * @throws MalformedURLException
	 *             When the file location can't be formulated as a URL
	 */
	private Configuration createConfiguration() throws FileNotFoundException, MalformedURLException {
		Configuration configuration = new Configuration();

		Path grammarDir = this.grammarCreator.getGrammarDirectory();
		String grammarPath = grammarDir.toUri().toURL().toString();

		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

		configuration.setUseGrammar(false);
		if (this.grammarName != "") {
			Path grammarFile = grammarDir.resolve(this.grammarName + ".gram");
			if (!Files.exists(grammarFile))
				throw new FileNotFoundException("Grammar file not found.");
			configuration.setGrammarPath(grammarPath);
			configuration.setGrammarName(this.grammarName);
			configuration.setUseGrammar(true);
		}
		return configuration;
	}

}
