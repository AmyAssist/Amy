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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;
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
	private SpeechInputHandler inputHandler;
	@Reference
	private MessageHub messageHub;
	@Reference
	private MainGrammarSpeechResultHandler mainResultHandler;
	@Reference
	private TempGrammarSpeechResultHandler tempResultHandler;

	// --------------------------------------------------------------
	// Fields

	private Grammar keyWordGrammar = Grammar.MAIN;
	private Grammar startGrammar = Grammar.GOOGLE;
	private ListeningState currentListeningState = ListeningState.NOT_LISTENING;
	private Grammar currentGrammar = null;

	private static final String MESSAGE_TOPIC_MUTE = "home/all/music/mute";

	// --------------------------------------------------------------
	// Construcor

	// --------------------------------------------------------------
	// Init

	@PostConstruct
	private void init() {
		this.tempResultHandler.setRecognizerManager(this);
		this.mainResultHandler.setRecognizerManager(this);

		Grammar.GOOGLE.setResultHandler(this.tempResultHandler);
		Grammar.TEMP.setResultHandler(this.tempResultHandler);
		Grammar.MAIN.setResultHandler(this.mainResultHandler);
	}

	// --------------------------------------------------------------
	// Methods

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
			setCurrentGrammar(this.startGrammar);
			break;

		case SINGLE_CALL_LISTENING:

			this.messageHub.publish(MESSAGE_TOPIC_MUTE, "true");
			this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
			setCurrentGrammar(this.startGrammar);
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
			setCurrentGrammar(this.keyWordGrammar);
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
		this.currentGrammar = currentGrammar;
		if (currentGrammar != null) {
			currentGrammar.requestSR();
		}

	}

	/**
	 * Request the NextRecognitionResult
	 */
	public void nextRecognitionRequest() {
		if (this.currentGrammar != null) {
			this.currentGrammar.requestSR();
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

		Grammar returnGrammar = Grammar.GOOGLE;

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
			setCurrentGrammar(null);
			Thread.currentThread().interrupt();
		}

		if (this.currentListeningState == ListeningState.SINGLE_CALL_LISTENING) {
			setListeningState(ListeningState.NOT_LISTENING);
		} else {
			if (this.currentGrammar != null) {
				returnGrammar.requestSR();
			}
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
	 * Set's {@link #startGrammar startGrammar}
	 * 
	 * @param startGrammar
	 *            startGrammar
	 */
	public void setStartGrammar(Grammar startGrammar) {
		this.startGrammar = startGrammar;
	}

	/**
	 * Set's {@link #keyWordGrammar keyWordGrammar}
	 * 
	 * @param keyWordGrammar
	 *            keyWordGrammar
	 */
	public void setKeyWordGrammar(Grammar keyWordGrammar) {
		this.keyWordGrammar = keyWordGrammar;
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
