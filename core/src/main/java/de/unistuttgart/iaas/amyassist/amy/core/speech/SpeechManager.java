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

import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogHandler;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;
import de.unistuttgart.iaas.amyassist.amy.core.speech.sphinx.SphinxGrammarCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.sphinx.SphinxGrammarName;
import de.unistuttgart.iaas.amyassist.amy.core.speech.sphinx.SphinxRecognizer;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.LocationTopics;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.RoomTopics;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.SmarthomeFunctionTopics;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR;

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
	private static final String MESSAGE_TOPIC_MUTE = SmarthomeFunctionTopics.MUTE.getTopicString(LocationTopics.ALL,
			RoomTopics.ALL);

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
	private NLProcessingManager nlProcessingManager;
	@Reference
	private DialogHandler dialogHandler;

	private UUID dialogId;

	private boolean recognitionEnabled;

	private Properties config;

	private volatile ListeningState currentListeningState = ListeningState.NOT_LISTENING;

	@PostConstruct
	private void init() {
		loadAndCheckProperties();
		if (Boolean.parseBoolean(this.config.getProperty(PROPERTY_ENABLE))) {
			this.recognitionEnabled = true;

			this.dialogId = this.dialogHandler.createDialog(this::voiceOutput);

			this.sphinxGrammarCreator.createGrammar(SPHINX_MAIN_GARMMAR_NAME,
					this.nlProcessingManager.getGrammarFileString(SPHINX_MAIN_GARMMAR_NAME));
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		if (this.recognitionEnabled) {
			this.sphinxSpeechRecognizer.recognizeContinuously(this::keywordRecognized);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		this.currentListeningState = ListeningState.NOT_LISTENING;
		this.sphinxSpeechRecognizer.stopContinuousRecognition();
		this.output.stopOutput();
	}

	private void keywordRecognized(String word) {
		switch (word) {
		case Constants.SINGLE_CALL_START:
			if (this.currentListeningState == ListeningState.NOT_LISTENING) {
				setListeningState(ListeningState.SINGLE_CALL_LISTENING);
			}
			break;
		case Constants.MULTI_CALL_START:
			if (this.currentListeningState == ListeningState.NOT_LISTENING) {
				setListeningState(ListeningState.MULTI_CALL_LISTENING);
			}
			break;
		case Constants.MULTI_CALL_STOP:
			if (this.currentListeningState == ListeningState.MULTI_CALL_LISTENING) {
				setListeningState(ListeningState.NOT_LISTENING);
			}
			break;
		case Constants.SHUT_UP:
			this.output.stopOutput();
			break;
		default:
			// Do nothing.
			break;
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
				this.output.voiceOutput("now sleeping");
				break;
			case SINGLE_CALL_LISTENING:
				this.output.soundOutput(Sounds.SINGLE_CALL_STOP_BEEP);
				break;
			default:
				break;
			}
			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "false");
			break;

		default:
			break;
		}

		this.currentListeningState = newListeningState;
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

	/**
	 * check if Speech Recognizer shall be startedF
	 */
	private void loadAndCheckProperties() {
		this.config = this.configManager.getConfigurationWithDefaults(CONFIG_NAME);
		if (this.config.getProperty(PROPERTY_ENABLE) == null)
			throw new IllegalStateException("Property " + PROPERTY_ENABLE + " missing in audio manager config.");
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
