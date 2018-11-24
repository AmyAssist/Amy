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

package io.github.amyassist.amy.core.speech;

import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.natlang.DialogHandler;
import io.github.amyassist.amy.core.service.RunnableService;
import io.github.amyassist.amy.core.speech.data.Sounds;
import io.github.amyassist.amy.core.speech.output.Output;
import io.github.amyassist.amy.core.speech.sphinx.SphinxGrammarCreator;
import io.github.amyassist.amy.core.speech.sphinx.SphinxGrammarName;
import io.github.amyassist.amy.core.speech.sphinx.SphinxRecognizer;
import io.github.amyassist.amy.messagehub.MessageHub;
import io.github.amyassist.amy.messagehub.topics.SmarthomeFunctionTopics;
import io.github.amyassist.amy.messagehub.topics.Topics;
import io.github.amyassist.amy.remotesr.RemoteSR;

/**
 * Service that Controls the Local SpeechRecognition
 * 
 * @author Kai Menzel, Tim Neumann
 */
@Service(SpeechManager.class)
public class SpeechManager implements RunnableService {

	private static final String SPHINX_MAIN_GARMMAR_NAME = "mainGrammar";

	private static final String CONFIG_NAME = "localSpeech.config";
	private static final String PROPERTY_ENABLE = "enable";
	private static final String PROPERTY_KEYWORD_START_SINGLE = "listenOnceKeyword";
	private static final String PROPERTY_KEYWORD_START_MULTI = "wakeupKeyword";
	private static final String PROPERTY_KEYWORD_END_MULTI = "sleepKeyword";
	private static final String PROPERTY_KEYWORD_OUTPUT_STOP = "stopOutputKeyword";
	private static final String MESSAGE_TOPIC_MUTE = Topics.smarthomeAll(SmarthomeFunctionTopics.MUTE);

	@Reference
	private Logger logger;
	@Reference
	private ConfigurationManager configManager;
	@Reference
	private SphinxGrammarCreator sphinxGrammarCreator;
	@SphinxGrammarName(SPHINX_MAIN_GARMMAR_NAME)
	@Reference
	private SphinxRecognizer sphinxSpeechRecognizer;
	@Reference
	private RemoteSR googleSpeechRecognizer;
	@Reference
	private MessageHub messageHub;
	@Reference
	private Output output;

	@Reference
	private DialogHandler dialogHandler;

	private UUID dialogId;

	private boolean recognitionEnabled;
	private String keywordStartSingle;
	private String keywordStartMulti;
	private String keywordEndMulti;
	private String keywordStopOutput;

	private volatile ListeningState currentListeningState = ListeningState.NOT_LISTENING;

	@PostConstruct
	private void init() {
		loadAndCheckProperties();
		if (this.recognitionEnabled) {
			this.dialogId = this.dialogHandler.createDialog(response -> this.voiceOutput(response.getText()));
			this.sphinxGrammarCreator.createGrammar(SPHINX_MAIN_GARMMAR_NAME, this.keywordStartSingle,
					this.keywordStartMulti, this.keywordEndMulti, this.keywordStopOutput);
		}
	}

	/**
	 * check if Speech Recognizer shall be startedF
	 */
	private void loadAndCheckProperties() {
		Properties config = this.configManager.getConfigurationWithDefaults(CONFIG_NAME);
		this.recognitionEnabled = Boolean.parseBoolean(config.getProperty(PROPERTY_ENABLE));
		this.keywordStartSingle = config.getProperty(PROPERTY_KEYWORD_START_SINGLE);
		this.keywordStartMulti = config.getProperty(PROPERTY_KEYWORD_START_MULTI);
		this.keywordEndMulti = config.getProperty(PROPERTY_KEYWORD_END_MULTI);
		this.keywordStopOutput = config.getProperty(PROPERTY_KEYWORD_OUTPUT_STOP);
	}

	/**
	 * @see io.github.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		if (this.recognitionEnabled) {
			this.sphinxSpeechRecognizer.recognizeContinuously(this::keywordRecognized);
		}
	}

	/**
	 * @see io.github.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		if (this.recognitionEnabled) {
			this.currentListeningState = ListeningState.NOT_LISTENING;
			this.sphinxSpeechRecognizer.stopContinuousRecognition();
			this.output.stopOutput();
		}
	}

	private void keywordRecognized(String word) {
		if (this.currentListeningState == ListeningState.NOT_LISTENING) {
			if (word.equals(this.keywordStartSingle)) {
				setListeningState(ListeningState.SINGLE_CALL_LISTENING);
			}
			if (word.equals(this.keywordStartMulti)) {
				setListeningState(ListeningState.MULTI_CALL_LISTENING);
			}
		}

		if (word.equals(this.keywordEndMulti) && this.currentListeningState == ListeningState.MULTI_CALL_LISTENING) {
			setListeningState(ListeningState.NOT_LISTENING);
		}

		if (word.equals(this.keywordStopOutput)) {
			this.output.stopOutput();
		}
	}

	/**
	 * change the ListeningState
	 * 
	 * @param newListeningState
	 *            ListeningState [NOT | SINGLE | MULTI]
	 */
	private void setListeningState(ListeningState newListeningState) {

		switch (newListeningState) {

		case MULTI_CALL_LISTENING:

			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "true");
			this.output.voiceOutput("waking up");
			this.googleSpeechRecognizer.recognizeOnce(this::processNormalInput);
			break;

		case SINGLE_CALL_LISTENING:

			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "true");
			this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
			this.googleSpeechRecognizer.recognizeOnce(this::processNormalInput);
			break;

		case NOT_LISTENING:

			// Depends on Current Listening State
			switch (this.currentListeningState) {
			case MULTI_CALL_LISTENING:
				this.output.voiceOutput("now sleeping", this::unmute);
				break;
			case SINGLE_CALL_LISTENING:
				this.output.soundOutput(Sounds.SINGLE_CALL_STOP_BEEP, this::unmute);
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}

		this.currentListeningState = newListeningState;
	}

	private void unmute() {
		this.messageHub.publish(MESSAGE_TOPIC_MUTE, "false");
	}

	private void processNormalInput(String result) {
		if (this.currentListeningState == ListeningState.NOT_LISTENING)
			return;
		this.logger.info("I understood: {}", result);

		if (result.isEmpty() && !this.dialogHandler.hasDialogUnfinishedIntent(this.dialogId)) {
			this.voiceIOFinished();
		} else {
			try {
				this.dialogHandler.process(result, this.dialogId);
			} catch (RuntimeException e) {
				this.logger.error("Error while processing input", e);
				voiceOutput("Error while processing input.");
			}
		}
	}

	/**
	 * Get's {@link #currentListeningState currentListeningState}
	 * 
	 * @return currentListeningState
	 */
	public ListeningState getCurrentListeningState() {
		return this.currentListeningState;
	}

	/**
	 * Get's {@link #recognitionEnabled recognitionEnabled}
	 * 
	 * @return recognitionEnabled
	 */
	public boolean isRecognitionEnabled() {
		return this.recognitionEnabled;
	}

	private void voiceOutput(String text) {
		this.output.voiceOutput(text, this::voiceIOFinished);
	}

	private void voiceIOFinished() {
		if (this.currentListeningState == ListeningState.NOT_LISTENING)
			return;
		if (this.dialogHandler.hasDialogUnfinishedIntent(this.dialogId)) {
			this.googleSpeechRecognizer.recognizeOnce(this::processNormalInput);
		} else {
			if (this.currentListeningState == ListeningState.MULTI_CALL_LISTENING) {
				this.googleSpeechRecognizer.recognizeOnce(this::processNormalInput);
			} else if (this.currentListeningState == ListeningState.SINGLE_CALL_LISTENING) {
				this.setListeningState(ListeningState.NOT_LISTENING);
			}
		}
	}

	/**
	 * Enum that Describes the current Listening state of the SR System
	 * 
	 * @author Kai Menzel
	 */
	public enum ListeningState {
		/**
		 * Recognition is sleeping
		 */
		NOT_LISTENING,
		/**
		 * Recognition state where amy is listening to a single command
		 */
		SINGLE_CALL_LISTENING,
		/**
		 * Recognition state where amy is listening to an unlimited count of commands
		 */
		MULTI_CALL_LISTENING;
	}

}
