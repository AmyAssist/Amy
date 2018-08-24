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

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;

/**
 * Service that Controls the Local SpeechRecognition
 * 
 * @author Kai Menzel
 */
@Service(LocalSpeechInterpreter.class)
public class LocalSpeechInterpreter implements RunnableService, Runnable {

	@Reference
	private Logger logger;
	@Reference
	private ConfigurationManager configManager;
	@Reference
	private SphinxSpeechRecognizer sphinxSpeechRecognizer;
	@Reference
	private GoogleSpeechRecognizer googleSpeechRecognizer;
	@Reference
	private MessageHub messageHub;
	@Reference
	private Output output;
	@Reference
	private SpeechResultPreHandler resultHandler;

	private static final String CONFIG_NAME = "localSpeech.config";
	private static final String PROPERTY_ENABLE = "enable";
	private static final String MESSAGE_TOPIC_MUTE = "home/all/music/mute";

	private boolean recognitionEnabled;

	private SpeechRecognizer defaultKeywordRecognizer;
	private SpeechRecognizer defaultStartRecognizer;
	private Properties config;

	private SpeechRecognizer currentRecognizer;

	private ListeningState currentListeningState = ListeningState.NOT_LISTENING;

	@PostConstruct
	private void init() {
		loadAndCheckProperties();
		if (Boolean.parseBoolean(this.config.getProperty(PROPERTY_ENABLE))) {
			this.recognitionEnabled = true;

			this.resultHandler.setLocalSpeechInterpreter(this);
			
			this.defaultKeywordRecognizer = this.sphinxSpeechRecognizer;
			this.defaultStartRecognizer = this.googleSpeechRecognizer;
		}
	}

	/**
	 * change the ListeningState
	 * 
	 * @param newListeningState
	 *            ListeningState [NOT | SINGLE | MULTI]
	 */
	public void setListeningState(ListeningState newListeningState) {

		switch (newListeningState) {

		case MULTI_CALL_LISTENING:

			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "true");
			this.output.voiceOutput("waking up");
			setCurrentRecognizer(this.defaultStartRecognizer);
			break;

		case SINGLE_CALL_LISTENING:

			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "true");
			this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
			setCurrentRecognizer(this.defaultStartRecognizer);
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
			setCurrentRecognizer(this.defaultKeywordRecognizer);
			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "false");
			break;

		default:
			break;
		}

		this.currentListeningState = newListeningState;
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
	 * Set's {@link #currentRecognizer currentRecognizer}
	 * 
	 * @param currentRecognizer
	 *            currentRecognizer
	 */
	public void setCurrentRecognizer(SpeechRecognizer currentRecognizer) {
		this.currentRecognizer = currentRecognizer;
	}

	/**
	 * Get's {@link #currentRecognizer currentRecognizer}
	 * 
	 * @return currentRecognizer
	 */
	public SpeechRecognizer getCurrentRecognizer() {
		return this.currentRecognizer;
	}

	/**
	 * Request the NextRecognitionResult
	 */
	public void nextRecognitionRequest() {
		if (this.currentRecognizer != null) {
			this.currentRecognizer.requestRecognition();
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		if (this.recognitionEnabled) {
			this.currentRecognizer = this.defaultKeywordRecognizer;
			(new Thread(this)).start();
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		this.currentRecognizer = null;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		nextRecognitionRequest();
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
