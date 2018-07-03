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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.LocalMainGrammarResultHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.LocalSwitchableGrammarResultHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.tts.Output;

/**
 * Class that manages the Recognizers belonging to a given AudioInputStream
 * 
 * @author Kai Menzel
 */
public class SpeechRecognizerManager implements SpeechRecognitionManagerInterface {

	private final Logger logger = LoggerFactory.getLogger(SpeechRecognizerManager.class);

	private AudioInputStream ais;
	private SpeechInputHandler inputHandler;
	private Output output;
	private GrammarObjectsCreator grammarData;

	private SpeechRecognizer mainRecognizer;
	private Map<String, SpeechRecognizer> recognizerList = new HashMap<>();

	private boolean soundPlaying = false;
	private boolean srListening = false;

	private Thread currentRecognizer;

	private boolean recognitionThreadRunning;

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
	public SpeechRecognizerManager(AudioInputStream ais, SpeechInputHandler inputHandler, Output output,
			GrammarObjectsCreator grammarData) {
		this.ais = ais;
		this.inputHandler = inputHandler;
		this.output = output;
		this.grammarData = grammarData;

		this.mainRecognizer = new SpeechRecognizer(this.grammarData.getMainGrammar(),
				new LocalMainGrammarResultHandler(this, this.grammarData.getMainGrammar()), this.ais);

		if (this.grammarData.getSwitchableGrammars() != null && !this.grammarData.getSwitchableGrammars().isEmpty()) {
			for (Grammar grammar : this.grammarData.getSwitchableGrammars()) {
				if (!this.recognizerList.containsKey(grammar.getName())) {
					this.recognizerList.put(grammar.getName(), new SpeechRecognizer(grammar,
							new LocalSwitchableGrammarResultHandler(this, grammar), this.ais));
				}
			}
		}
		this.currentRecognizer = new Thread(this.mainRecognizer);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.service.SpeechRecognizerHandler#start()
	 */
	@Override
	public void start() {
		this.recognitionThreadRunning = true;
		this.currentRecognizer.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.service.SpeechRecognizerHandler#stop()
	 */
	@Override
	public void stop() {
		this.recognitionThreadRunning = false;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#handleCommand(java.lang.String)
	 */
	@Override
	public void handleCommand(String result) {
		Future<String> handle = this.inputHandler.handle(result);
		try {
			voiceOutput(handle.get());
		} catch (ExecutionException e) {
			if (e.getCause() != null && e.getCause().getClass().equals(IllegalArgumentException.class)) {
				voiceOutput("unknown command");
			} else {
				this.logger.error("unknown error", e);
			}
		} catch (InterruptedException e) {
			this.logger.error("[Recognition Stopped] Error with SpeechInputhandler Return", e);
			stop();
			Thread.currentThread().interrupt();
		}

	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#handleGrammarSwitch(de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar)
	 */
	@Override
	public void handleGrammarSwitch(Grammar grammar) {
		if (grammar == null || grammar.getName().equals(this.grammarData.getMainGrammar().getName())) {
			switchGrammar(this.mainRecognizer);
		} else {
			switchGrammar(this.recognizerList.get(grammar.getName()));
		}
	}

	private void switchGrammar(SpeechRecognizer recognizer) {
		this.currentRecognizer.interrupt();
		this.currentRecognizer = new Thread(recognizer);
		this.currentRecognizer.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#handleListeningState(boolean)
	 */
	@Override
	public void handleListeningState(boolean listening) {
		this.srListening = listening;
		notify(listening);
	}

	@SuppressWarnings("unused")
	private void notify(boolean listening) {
		// TODO notify all plugins about the listening change
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#isListening()
	 */
	@Override
	public boolean isListening() {
		return this.srListening;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#voiceOutput(java.lang.String)
	 */
	@Override
	public void voiceOutput(String outputString) {
		this.output.output(this.listener, outputString);

	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#stopOutput()
	 */
	@Override
	public void stopOutput() {
		this.output.stopOutput();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#setSoundPlaying(boolean)
	 */
	@Override
	public void setSoundPlaying(boolean outputActive) {
		this.soundPlaying = outputActive;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#isSoundPlaying()
	 */
	@Override
	public boolean isSoundPlaying() {
		return this.soundPlaying;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#setRecognitionThreadRunning(boolean)
	 */
	@Override
	public void setRecognitionThreadRunning(boolean recognitionRunning) {
		this.recognitionThreadRunning = recognitionRunning;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognitionManagerInterface#isRecognitionThreadRunning()
	 */
	@Override
	public boolean isRecognitionThreadRunning() {
		return this.recognitionThreadRunning;
	}

	/**
	 * listens to the Voice Output
	 */
	private LineListener listener = event -> {
		if (event.getType() == LineEvent.Type.STOP) {
			((Clip) event.getSource()).close();
			setSoundPlaying(false);
		}
	};

}
