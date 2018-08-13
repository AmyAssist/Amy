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

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.LocalAudio;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SphinxSpeechRecognizer;
import de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler.MainGrammarSpeechResultHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler.TempGrammarSpeechResultHandler;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;

/**
 * Service that contains all SpeechRecognition corresponding data
 * 
 * @author Kai Menzel
 */
@Service
public class SpeechRecognizerManager {

	// --------------------------------------------------------------
	// Dependencies

	@Reference
	private Logger logger;
	@Reference
	private Output output;
	@Reference
	private GrammarObjectsCreator creator;
	@Reference
	private SpeechInputHandler inputHandler;
	@Reference
	private MessageHub messageHub;
	@Reference
	private AudioManager audioManager;
	@Reference
	private LocalAudio localAudio;
	@Reference
	private MainGrammarSpeechResultHandler mainResultHandler;
	@Reference
	private TempGrammarSpeechResultHandler tempResultHandler;
	@Reference
	private ConfigurationManager configManager;

	private Properties config;

	// --------------------------------------------------------------
	// Fields
	
	private Grammar mainGrammarType = Grammar.GOOGLE;

	private static final String CONFIG_NAME = "localSpeech.config";
	private static final String PROPERTY_ENABLE = "enable";

	// State Data Variables
	private ListeningState currentListeningState = ListeningState.NOT_LISTENING;
	private Grammar currentGrammar = Grammar.NONE;

	private AudioInputStream ais;
	private static final String MESSAGE_TOPIC_MUTE = "home/all/music/mute";

	// Recognizer Threads
	private Thread mainRecognizer;
	private Thread currentTempRecognizer;

	// --------------------------------------------------------------
	// Construcor

	// --------------------------------------------------------------
	// Init

	/**
	 * Creates the Main Recognizer
	 */
	@PostConstruct
	private void init() {
		loadAndCheckProperties();

		if (Boolean.parseBoolean(this.config.getProperty(PROPERTY_ENABLE))) {
			this.mainResultHandler.setRecognizerManager(this);
			this.tempResultHandler.setRecognizerManager(this);
			this.ais = createNewAudioInputStream();
			this.mainRecognizer = new Thread(
					new SphinxSpeechRecognizer(Grammar.MAIN, this.mainResultHandler, this.ais));
		}
	}

	// --------------------------------------------------------------
	// Methods

	/**
	 * check if Speech Recognizer shall be startedF
	 */
	private void loadAndCheckProperties() {
		this.config = this.configManager.getConfigurationWithDefaults(CONFIG_NAME);
		if (this.config.getProperty(PROPERTY_ENABLE) == null)
			throw new IllegalStateException("Property " + PROPERTY_ENABLE + " missing in audio manager config.");
	}

	/**
	 * Change the Listening state
	 * 
	 * @param state
	 *            Multi, Single, Not_Listening
	 */
	public void setListeningState(ListeningState state) {
		switch (state) {
		case MULTI_CALL_LISTENING:
			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "true");
			this.output.voiceOutput("waking up");
			setCurrentGrammar(this.mainGrammarType);
			break;

		case SINGLE_CALL_LISTENING:
			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "true");
			this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
			setCurrentGrammar(this.mainGrammarType);
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
			
			setCurrentGrammar(Grammar.MAIN);
			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "false");
			break;

		default:
			break;
		}
		this.currentListeningState = state;
	}

	/**
	 * Stop all Current Outputs
	 */
	public void stopOutput() {
		this.output.stopOutput();
	}

	/**
	 * Change the Current Grammar
	 * 
	 * @param currentGrammar
	 *            Main Temp None
	 */
	public void setCurrentGrammar(Grammar currentGrammar) {
		// check if Recognizer initiated
		if (this.mainRecognizer != null) {
			switch (currentGrammar) {

			case MAIN:
				if (this.currentGrammar != currentGrammar) {
					this.currentGrammar = currentGrammar;
					this.mainRecognizer.start();
				}
				break;

			case TEMP:
				this.currentGrammar = currentGrammar;
				if (this.currentTempRecognizer != null) {
					this.currentTempRecognizer.interrupt();
				}
				this.currentTempRecognizer = new Thread(
						new SphinxSpeechRecognizer(Grammar.TEMP, this.tempResultHandler, this.ais));
				this.currentTempRecognizer.start();
				break;

			case NONE:
				if (this.currentGrammar != currentGrammar) {
					this.currentGrammar = currentGrammar;
					// Do nothing
				}
				break;

			case GOOGLE:
				if (this.currentGrammar != currentGrammar) {
					this.currentGrammar = currentGrammar;
					this.mainRecognizer.start();
					// TODO start google translation
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * handles the SpeechRecognition Result and transfers it to the Interpreter
	 * 
	 * @param result
	 *            of the SR
	 */
	public void handleSpeechResult(String result) {
		this.logger.info("I understood: {}", result);
		Future<String> handle = this.inputHandler.handle(result);
		try {
			this.output.voiceOutput(handle.get());
		} catch (ExecutionException e) {
			if (e.getCause() != null && e.getCause().getClass().equals(IllegalArgumentException.class)) {
				this.output.voiceOutput("unknown command");
			} else {
				this.logger.error("unknown error", e);
			}
		} catch (InterruptedException e) {
			this.logger.error("[Recognition Stopped] Error with SpeechInputhandler Return", e);
			setCurrentGrammar(Grammar.NONE);
			Thread.currentThread().interrupt();
		}
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

	// --------------------------------------------------------------
	// Getter

	/**
	 * Getter
	 * 
	 * @return Current ListeningState
	 */
	public ListeningState getListeningState() {
		return this.currentListeningState;
	}

	/**
	 * Getter
	 * 
	 * @return current Active Grammar State
	 */
	public Grammar getCurrentGrammarState() {
		return this.currentGrammar;
	}

	/**
	 * Getter
	 * 
	 * @return true if System is Currently Outputting
	 */
	public boolean isSoundPlaying() {
		return this.output.isCurrentlyOutputting();
	}

	// --------------------------------------------------------------
	// Setter
	
	/**
	 * Set's {@link #mainGrammarType mainGrammarType}
	 * @param mainGrammarType  mainGrammarType
	 */
	public void setMainGrammarType(Grammar mainGrammarType) {
		this.mainGrammarType = mainGrammarType;
	}

	// --------------------------------------------------------------
	// Other

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
