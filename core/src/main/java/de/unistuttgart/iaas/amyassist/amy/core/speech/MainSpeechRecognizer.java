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
/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 *
 * Copyright (c) 2018 the Amy project authors.
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
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioInputStream;

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
public class MainSpeechRecognizer implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(MainSpeechRecognizer.class);

	// this Grammar
	private Grammar grammar;

	// Grammar to switch to
	private Grammar nextGrammar = null;

	// Audio Input Source for the Recognition
	private AudioInputStream ais = null;
	
	//Handler of all Recognizers and the TTS
	private AudioUserInteraction audioUI;
	
	private TextToSpeech tts;
	private boolean soundPlaying = false;

	/**
	 * Handler who use the translated String for commanding the System
	 */
	private SpeechInputHandler inputHandler;

	// The Recognizer which Handles the Recognition
	private StreamSpeechRecognizer recognizer;

	// -----------------------------------------------------------------------------------------------

	/**
	 * Creates the Recognizers and Configures them
	 * @param audioUI Handler of all recognizers and the TTS
	 *
	 * @param grammar
	 *            Grammar to use in this Recognizer
	 * @param inputHandler
	 *            Handler which will handle the input
	 * @param ais
	 *            set custom AudioInputStream. 
	 */
	public MainSpeechRecognizer(AudioUserInteraction audioUI, Grammar grammar, SpeechInputHandler inputHandler, AudioInputStream ais) {
		this.audioUI = audioUI;
		this.grammar = grammar;
		this.inputHandler = inputHandler;
		this.ais = ais;
		
		this.tts = TextToSpeech.getTTS();

		// Create the Recognizer
		try {
			MainSpeechRecognizer.this.recognizer = new StreamSpeechRecognizer(this.createConfiguration());
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
		
		System.out.println("[INFORMATION] :: Speech Recognition activated");

		// Boolean to check if we are supposed to listen (sleeping)
		boolean listening = false;

		// The result of the Recognition
		String speechRecognitionResult;

		loop: while (!Thread.interrupted() && this.audioUI.isRecognitionThreadRunning()) {

			
			/**
			 * wait for input from the recognizer
			 */
			SpeechResult speechResult = null;
			while (speechResult == null || this.soundPlaying) {
				speechResult = this.recognizer.getResult();
				if (Thread.interrupted()) {
					break loop;
				}
			}
			if(true) {
    			System.out.println(speechResult.getResult().getActiveTokens());
    			System.out.println(speechResult.getResult().toCreateLattice());
    			System.out.println(speechResult.getResult().getBestPronunciationResult());
			}
			
			// Get the hypothesis (Result as String)
			speechRecognitionResult = speechResult.getHypothesis();
			

			 if(speechRecognitionResult.equals(this.audioUI.getSHUTDOWN())) {
				 this.tts.stopOutput();
			 } 	// check wakeUp/sleep/shutdown
			 else if (speechRecognitionResult.equals(this.audioUI.getWAKEUP())) {
				listening = true;
//				this.audioUI.say("waking up");
				say("waking up");
			} else if (speechRecognitionResult.startsWith(this.audioUI.getWAKEUP() + " ")) {
				listening = true;
				this.makeDecision(speechRecognitionResult.replaceFirst(this.audioUI.getWAKEUP() + " ", ""));
			} else if (listening) {
				if (speechRecognitionResult.equals(this.audioUI.getGOSLEEP())) {
					listening = false;
					say("now sleeping");
				} else {
					this.makeDecision(speechRecognitionResult);
				}
			}

		}
		this.recognizer.stopRecognition();
		this.audioUI.switchGrammar(this.nextGrammar);
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * Gives Input to Handler checks if input is useful
	 *
	 * @param speech
	 *            the speechRecognitionResultString
	 */
	public void makeDecision(String speech) {
		String result = speech;
		if (result.replace(" ", "").equals("") || result.equals("<unk>"))
			return;

		if (!this.grammar.getSwitchList().isEmpty()) {
			for (Map.Entry<String, Grammar> entry : this.grammar.getSwitchList().entrySet()) {
				if (result.equalsIgnoreCase(entry.getKey())) {
					this.stop(entry.getValue());
				}
			}
		}

		if (!Thread.interrupted() && this.inputHandler != null) {
			Future<String> handle = this.inputHandler.handle(result);
			try {
				say(handle.get());
				// System.out.println(handle.get());
				// System.out.println();
			} catch (InterruptedException | ExecutionException e) {
				if (e.getCause() != null && e.getCause().getClass().equals(IllegalArgumentException.class)) {
					say("unknown command");
					// System.out.println("Unknown command");
				} else {
					e.printStackTrace();
				}
			}
		}
	}

	// -----------------------------------------------------------------------------------------------

	private void say(String s) {
		this.tts.stopOutput();
		this.tts.say(s);
	}
	
	// ===============================================================================================
	
	private void stop(Grammar switchGrammar) {
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
				logger.error("", e);
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
