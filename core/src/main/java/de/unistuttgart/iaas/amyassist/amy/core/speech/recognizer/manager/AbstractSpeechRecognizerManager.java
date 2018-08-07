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

package de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognizer;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.RecognitionResultHandler;

/**
 * Class that manages the Recognizers belonging to a given AudioInputStream
 * 
 * @author Kai Menzel
 */
public abstract class AbstractSpeechRecognizerManager
		implements SpeechRecognitionResultManager, SpeechRecognizerManager {

	private final Logger logger = LoggerFactory.getLogger(AbstractSpeechRecognizerManager.class);

	private SpeechInputHandler inputHandler;
	private Output output;
	private String mainGrammarName;

	private SpeechRecognizer mainRecognizer;
	private Map<String, SpeechRecognizer> recognizerList = new HashMap<>();

	private boolean multiCallActive = false;
	private boolean singleCallActive = false;

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
	public AbstractSpeechRecognizerManager(AudioInputStream ais, SpeechInputHandler inputHandler, Output output,
			GrammarObjectsCreator grammarData) {
		this.inputHandler = inputHandler;
		this.output = output;
		this.mainGrammarName = grammarData.getMainGrammar().getName();

		createRecognizers(grammarData, ais);

		this.currentRecognizer = new Thread(this.mainRecognizer);
	}

	private void createRecognizers(GrammarObjectsCreator grammarData, AudioInputStream ais) {
		this.mainRecognizer = new SpeechRecognizer(grammarData.getMainGrammar(),
				getMainResultHandler(grammarData.getMainGrammar()), ais);

		if (grammarData.getSwitchableGrammars() != null && !grammarData.getSwitchableGrammars().isEmpty()) {
			for (Grammar grammar : grammarData.getSwitchableGrammars()) {
				if (!this.recognizerList.containsKey(grammar.getName())) {
					this.recognizerList.put(grammar.getName(),
							new SpeechRecognizer(grammar, getResultHandler(grammar), ais));
				}
			}
		}
	}

	/**
	 * Get the correct ResultHandler for the environment
	 * 
	 * @param grammar
	 *            Grammar of the Recognizer the Handler is created for
	 * @return ResultHandler for the Recognizer
	 */
	protected abstract RecognitionResultHandler getMainResultHandler(Grammar grammar);

	/**
	 * Get the correct ResultHandler for the environment
	 * 
	 * @param grammar
	 *            Grammar of the Recognizer the Handler is created for
	 * @return ResultHandler for the Recognizer
	 */
	protected abstract RecognitionResultHandler getResultHandler(Grammar grammar);

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager#stop()
	 */
	@Override
	public void start() {
		this.recognitionThreadRunning = true;
		this.currentRecognizer.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager#stop()
	 */
	@Override
	public void stop() {
		this.recognitionThreadRunning = false;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#handleCommand(java.lang.String)
	 */
	@Override
	public void handleCommand(String result) {
		this.logger.info("I understood: {}", result);
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
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#handleGrammarSwitch(de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar)
	 */
	@Override
	public void handleGrammarSwitch(Grammar grammar) {
		if (grammar == null || grammar.getName().equals(this.mainGrammarName)) {
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
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#handleMultiCallListeningState(boolean)
	 */
	@Override
	public void handleMultiCallListeningState(boolean listening) {
		this.multiCallActive = listening;
		if (listening) {
			this.voiceOutput("waking up");
		} else {
			this.voiceOutput("now sleeping");
		}
		/**
		 * notify(listening);
		 */
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#isMultiCallActive()
	 */
	@Override
	public boolean isMultiCallActive() {
		return this.multiCallActive;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#handleSingleCallListeningState(boolean)
	 */
	@Override
	public void handleSingleCallListeningState(boolean singleCommand) {
		this.singleCallActive = singleCommand;
		if (singleCommand) {
			this.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
		} else {
			this.soundOutput(Sounds.SINGLE_CALL_STOP_BEEP);
		}
		/**
		 * notify(listening);
		 */
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#isSingleCallActive()
	 */
	@Override
	public boolean isSingleCallActive() {
		return this.singleCallActive;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#voiceOutput(java.lang.String)
	 */
	@Override
	public void voiceOutput(String outputString) {
		this.output.voiceOutput(outputString); // TODO:
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#soundOutput(Sounds)
	 */
	@Override
	public void soundOutput(Sounds sound) {
		this.output.soundOutput(sound);// TODO:
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#stopOutput()
	 */
	@Override
	public void stopOutput() {
		this.output.stopOutput();// TODO:
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#isSoundPlaying()
	 */
	@Override
	public boolean isSoundPlaying() {
		return this.output.isCurrentlyOutputting();// TODO:
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#setRecognitionThreadRunning(boolean)
	 */
	@Override
	public void setRecognitionThreadRunning(boolean recognitionRunning) {
		this.recognitionThreadRunning = recognitionRunning;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#isRecognitionThreadRunning()
	 */
	@Override
	public boolean isRecognitionThreadRunning() {
		return this.recognitionThreadRunning;
	}

}
