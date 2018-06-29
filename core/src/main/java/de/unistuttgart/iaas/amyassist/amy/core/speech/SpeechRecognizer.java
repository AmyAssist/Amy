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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 * Class that translate Audio-Input into Strings, powered by CMU Sphinx - https://cmusphinx.github.io/ which is Licenced
 * under BSD
 *
 * @author Kai Menzel
 */
public abstract class SpeechRecognizer implements Runnable {

	/**
	 * logger for all the Speech Recognition classes
	 */
	protected final Logger logger = LoggerFactory.getLogger(SpeechRecognizer.class);

	// Grammar of the Current running Recognizer
	private Grammar grammar;

	// The Grammar to switch to, null for Ending all Recognition
	private Grammar nextGrammar = null;

	// The Input Stream Source
	private AudioInputStream ais = null;

	// True if there is Sound Output right now
	private boolean soundPlaying = false;

	// Check if Answer shall be voiced throgh tts
	private boolean voiceOutput;

	// Handler who use the translated String for commanding the System
	private SpeechInputHandler inputHandler;

	// The Recognizer which Handles the Recognition
	private StreamSpeechRecognizer recognizer;

	/**
	 * Handler of all Recognizers and the TTS
	 */
	protected AudioUserInteraction audioUI;

	/**
	 * The TextToSpeech Output Object
	 */
	protected TextToSpeech tts;

	/**
	 * The result of the Recognition
	 */
	protected String speechRecognitionResult = null;

	// -----------------------------------------------------------------------------------------------

	/**
	 * listens to the Voice Output
	 */
	private LineListener listener = event -> {
		if (event.getType() == LineEvent.Type.STOP) {
			((Clip) event.getSource()).close();
			SpeechRecognizer.this.soundPlaying = false;
		}
	};

	// -----------------------------------------------------------------------------------------------

	/**
	 * Creates the Recognizers and Configures them
	 * 
	 * @param audioUI
	 *            Handler of all recognizers and the TTS
	 *
	 * @param grammar
	 *            Grammar to use in this Recognizer
	 * @param inputHandler
	 *            Handler which will handle the input
	 * @param ais
	 *            set custom AudioInputStream.
	 * @param voiceOutput
	 *            true if the TTS shall voice the Answer
	 */
	public SpeechRecognizer(AudioUserInteraction audioUI, Grammar grammar, SpeechInputHandler inputHandler,
			AudioInputStream ais, boolean voiceOutput) {
		this.audioUI = audioUI;
		this.grammar = grammar;
		this.inputHandler = inputHandler;
		this.ais = ais;
		this.voiceOutput = voiceOutput;

		this.tts = TextToSpeech.getTTS();

		// Create the Recognizer
		try {
			SpeechRecognizer.this.recognizer = new StreamSpeechRecognizer(this.createConfiguration());
		} catch (IOException e) {
			this.logger.error("StreamRecognizer can't be instantiated", e);
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

		while (this.audioUI.isRecognitionThreadRunning() && !Thread.interrupted() && this.ais != null) {

			// wait for input from the recognizer
			SpeechResult speechResult = getSpeechResult();

			/**
			 * If Thread is not Interrupted, get and use SpeechRecognitionResult
			 */
			if (speechResult != null) {
				// Get the hypothesis (Result as String)
				this.speechRecognitionResult = speechResult.getHypothesis();

				if (!this.soundPlaying) {
					predefinedInputHandling();
				} else {
					if (this.speechRecognitionResult.equals(Constants.SHUT_UP)) {
						this.tts.stopOutput();
					}
				}
			}
		}

		this.recognizer.stopRecognition();
		this.audioUI.switchGrammar(this.nextGrammar);
	}

	// ===============================================================================================

	/**
	 * Handles the Recognizer Specific Actions that trigger before giving the input to the inputHandler. Mainly waking
	 * up and going to Sleep
	 */
	protected abstract void predefinedInputHandling();

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

		if (!checkGrammarSwitch(result) && !Thread.interrupted() && this.inputHandler != null) {
			Future<String> handle = this.inputHandler.handle(result);
			String unknown = "unknown command";
			try {
				this.say(handle.get());
			} catch (ExecutionException e) {
				if (e.getCause() != null && e.getCause().getClass().equals(IllegalArgumentException.class)) {
					this.say(unknown);
					this.logger.warn(unknown);
				} else {
					this.logger.error("unknown error", e);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (NullPointerException e) {
				this.say(unknown);
				this.logger.error("no handle return", e);
			}
		}
	}

	// ===============================================================================================

	/**
	 * check if the Result is a keyword for a specific GrammarSwitch
	 * 
	 * @param result
	 *            SpeechRecognitionResult
	 * @return true if switch will be initialized
	 */
	private boolean checkGrammarSwitch(String result) {
		if (!this.grammar.getSwitchList().isEmpty()) {
			for (Map.Entry<String, Grammar> entry : this.grammar.getSwitchList().entrySet()) {
				if (result.equalsIgnoreCase(entry.getKey())) {
					this.logger.info("switching Recognizer...");
					this.stop(entry.getValue());
					return true;
				}
			}
		}
		return false;
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * wait for input from the recognizer
	 * 
	 * @return Result of VoiceInput or null if ThreadIsInterrupted
	 */
	private SpeechResult getSpeechResult() {
		SpeechResult speechResult = null;
		while (speechResult == null) {
			speechResult = this.recognizer.getResult();
			if (!this.audioUI.isRecognitionThreadRunning() || Thread.interrupted()) {
				if (Thread.interrupted()) {
					stop();
				}
				speechResult = null;
				break;
			}
		}
		return speechResult;
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * Output
	 * 
	 * @param s
	 *            String that shall be said
	 */
	protected void say(String s) {
		if (this.voiceOutput) {
			this.soundPlaying = true;
			this.tts.say(this.listener, s);
		} else {
			this.tts.log(s);
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * void to stop the Actual Recognizer in preparation to start the next
	 * 
	 * @param switchGrammar
	 *            Grammar to Switch to
	 */
	protected void stop(Grammar switchGrammar) {
		this.logger.info("stop current Recognizer to start the next");
		this.audioUI.setRecognitionThreadRunning(false);
		this.nextGrammar = switchGrammar;
	}

	/**
	 * BE CAREFUL void to stop the Current Recognizer without starting a new one
	 */
	public void stop() {
		this.logger.info("stop the Recognition");
		this.audioUI.setRecognitionThreadRunning(false);
		this.nextGrammar = null;
		Constants.setSRListening(false);
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
				this.logger.error("", e);
			}
		}
		return configuration;
	}

	// ===============================================================================================

}
