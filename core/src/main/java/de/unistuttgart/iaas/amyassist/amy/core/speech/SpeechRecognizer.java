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
 * Class that translate Aduio-Input into Strings, powered by CMU Sphinx -
 * https://cmusphinx.github.io/ which is Licenced under BSD
 *
 * @author Kai Menzel
 */
public abstract class SpeechRecognizer implements Runnable {

	/**
	 * 
	 */
	protected final Logger logger = LoggerFactory.getLogger(SpeechRecognizer.class);

	/**
	 * this Grammar
	 */
	protected Grammar grammar;

	/**
	 * Grammar to switch to
	 */
	protected Grammar nextGrammar = null;

	/**
	 * Audio Input Source for the Recognition
	 */
	protected AudioInputStream ais = null;

	/**
	 * Handler of all Recognizers and the TTS
	 */
	protected AudioUserInteraction audioUI;

	/**
	 * 
	 */
	protected TextToSpeech tts;

	/**
	 * 
	 */
	boolean soundPlaying = false;

	/**
	 * Boolean to check if we are supposed to listen (sleeping)
	 */
	protected boolean listening = false;

	/**
	 * The result of the Recognition
	 */
	protected String speechRecognitionResult = null;

	/**
	 * Handler who use the translated String for commanding the System
	 */
	protected SpeechInputHandler inputHandler;

	/**
	 * The Recognizer which Handles the Recognition
	 */
	protected StreamSpeechRecognizer recognizer;
	
	private LineListener listener = new LineListener(){
		@Override
		public void update(LineEvent event) {
			if(event.getType() == LineEvent.Type.STOP) {
				((Clip) event.getSource()).close();
				SpeechRecognizer.this.soundPlaying = false;
			}
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
	 */
	public SpeechRecognizer(AudioUserInteraction audioUI, Grammar grammar, SpeechInputHandler inputHandler,
			AudioInputStream ais) {
		this.audioUI = audioUI;
		this.grammar = grammar;
		this.inputHandler = inputHandler;
		this.ais = ais;

		this.tts = TextToSpeech.getTTS();

		// Create the Recognizer
		try {
			SpeechRecognizer.this.recognizer = new StreamSpeechRecognizer(this.createConfiguration());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * @see java.lang.Runnable#run() Starts and runs the recognizer calls
	 *      makeDecision() with the recognized String
	 */
	@Override
	public void run() {

		// starts Recognition
		this.recognizer.startRecognition(this.ais);

		this.logger.info("[INFORMATION] :: Speech Recognition activated");

		// Boolean to check if we are supposed to listen (sleeping)
		this.listening = false;

		loop: while (!Thread.interrupted() && this.audioUI.isRecognitionThreadRunning()) {

			/**
			 * wait for input from the recognizer
			 */
			SpeechResult speechResult = null;
			while (speechResult == null) {
				speechResult = this.recognizer.getResult();
				if (Thread.interrupted()) {
					break loop;
				}
			}

			// Get the hypothesis (Result as String)
			this.speechRecognitionResult = speechResult.getHypothesis();
			if(!this.soundPlaying) {
				predefinedInputHandling();
			}else {
				if(this.speechRecognitionResult.equals(this.audioUI.getSHUTDOWN())) {
					this.tts.stopOutput();
				}
			}
		}
		this.recognizer.stopRecognition();
		this.audioUI.switchGrammar(this.nextGrammar);
	}

	// ===============================================================================================

	/**
	 * 
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

		checkGrammarSwitch(result);

		if (!Thread.interrupted() && this.inputHandler != null) {
			Future<String> handle = this.inputHandler.handle(result);
			try {
				this.say(handle.get());
			} catch (ExecutionException e) {
				if (e.getCause() != null && e.getCause().getClass().equals(IllegalArgumentException.class)) {
					this.say("unknown command");
					this.logger.warn("unknown command");
				} else {
					this.logger.error("unknown error", e);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	// ===============================================================================================

	private void checkGrammarSwitch(String result) {
		if (!this.grammar.getSwitchList().isEmpty()) {
			for (Map.Entry<String, Grammar> entry : this.grammar.getSwitchList().entrySet()) {
				if (result.equalsIgnoreCase(entry.getKey())) {
					this.stop(entry.getValue());
				}
			}
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * Output
	 * 
	 * @param s
	 *            String that shall be said
	 */
	protected void say(String s) {
		this.soundPlaying = true;
		this.tts.stopOutput();
		this.tts.say(this.listener, s);
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * void to stop the Actual Recognizer in preparation to start the next
	 * 
	 * @param switchGrammar
	 *            Grammar to Switch to
	 */
	protected void stop(Grammar switchGrammar) {
		this.audioUI.setRecognitionThreadRunning(false);
		this.nextGrammar = switchGrammar;
	}

	// ===============================================================================================

	/**
	 * creates Configuration for the recognizers
	 *
	 * @return the configuration
	 */
	private Configuration createConfiguration() {
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		if (this.grammar.getFile().toString().endsWith(".gram")) {
			try {
				configuration.setGrammarPath(this.grammar.getFile().getParentFile().toURI().toURL().toString());
			} catch (MalformedURLException e) {
				this.logger.error("", e);
			}
			configuration.setGrammarName(this.grammar.getFile().getName().replace(".gram", ""));
			configuration.setUseGrammar(true);
		} else {
			configuration.setUseGrammar(false);
		}

		return configuration;
	}

	// ===============================================================================================

}
