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

package de.unistuttgart.iaas.amyassist.amy.core.speech.data;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.output.OutputImpl;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognizer;
import de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler.MainGrammarSpeechResultHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.result.handler.TempGrammarSpeechResultHandler;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;

/**
 * Service that contains all SpeechRecognition corresponding data
 * 
 * @author Kai Menzel
 */
@Service
public class SpeechRecognitionStateVariables {

	@Reference
	private Logger logger;

	@Reference
	private OutputImpl output;

	@Reference
	private GrammarObjectsCreator creator;

	@Reference
	private SpeechInputHandler inputHandler;

	@Reference
	private MessageHub messageHub;

	// State Data Variables
	private ListeningState currentListeningState = ListeningState.NOT_LISTENING;
	private Grammar currentGrammar = Grammar.NONE;

	private AudioInputStream ais = createNewAudioInputStream();

	// Predefined ResultHandler
	private MainGrammarSpeechResultHandler mainResultHandler;
	private TempGrammarSpeechResultHandler tempResultHandler;

	// Predefined Recognizers
	private Thread mainRecognizer;

	// -------------------------------------------------------

	/**
	 * Setter
	 * 
	 * @param mainResultHandler
	 *            handles Result of Main Recognizer
	 */
	public void setMainResultHandler(MainGrammarSpeechResultHandler mainResultHandler) {
		this.mainResultHandler = mainResultHandler;
	}

	/**
	 * Setter
	 * 
	 * @param tempResultHandler
	 *            handles Result of Temp Recognizer
	 */
	public void setTempResultHandler(TempGrammarSpeechResultHandler tempResultHandler) {
		this.tempResultHandler = tempResultHandler;
	}

	/**
	 * Creates the Main Recognizer
	 */
	public void init() {
		this.mainRecognizer = new Thread(new SpeechRecognizer(Grammar.MAIN, this.mainResultHandler, this.ais));
	}

	// -------------------------------------------------------

	/**
	 * Change the Listening state
	 * 
	 * @param state
	 *            Multi, Single, Not_Listening
	 */
	public void setListeningState(ListeningState state) {
		switch (state) {
		case MULTI_CALL_LISTENING:
			this.messageHub.publish("home/all/music/mute", "true");
			this.output.voiceOutput("waking up");
			break;

		case SINGLE_CALL_LISTENING:
			this.messageHub.publish("home/all/music/mute", "true");
			this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
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

			this.messageHub.publish("home/all/music/mute", "false");
			break;

		default:
			break;
		}
		this.currentListeningState = state;
	}

	/**
	 * Getter
	 * 
	 * @return Current ListeningState
	 */
	public ListeningState getListeningState() {
		return this.currentListeningState;
	}

	/**
	 * Change the Current Grammar
	 * 
	 * @param currentGrammar
	 *            Main Temp None
	 */
	public void setCurrentGrammar(Grammar currentGrammar) {
		this.currentGrammar = currentGrammar;
		switch (currentGrammar) {
		case MAIN:
			this.mainRecognizer.start();
			break;
		case TEMP:
			(new Thread(new SpeechRecognizer(Grammar.TEMP, this.tempResultHandler, this.ais))).start();
			break;
		case NONE:
			break;
		case GOOGLE:
			// TODO start google translation
			break;
		default:
			break;
		}
	}

	/**
	 * Getter
	 * 
	 * @return current Active Grammar State
	 */
	public Grammar getCurrentGrammarState() {
		return this.currentGrammar;
	}

	// -------------------------------------------------------

	/**
	 * Getter
	 * 
	 * @return true if System is Currently Outputting
	 */
	public boolean isSoundPlaying() {
		return this.output.isCurrentlyOutputting();
	}

	/**
	 * Stop all Current Outputs
	 */
	public void stopOutput() {
		this.output.stopOutput();
	}

	// -------------------------------------------------------

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

	// -------------------------------------------------------

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

	// -------------------------------------------------------

	/**
	 * starts the default AudioInputStream
	 * 
	 * @return AudioInputStream from the local mic
	 */
	private AudioInputStream createNewAudioInputStream() {
		TargetDataLine mic = null;
		try {
			mic = AudioSystem.getTargetDataLine(this.getFormat());
			mic.open(this.getFormat());
			mic.start();
			return new AudioInputStream(mic);
		} catch (LineUnavailableException e) {
			throw new RuntimeExceptionRecognizerCantBeCreated("AudioInputStream can't be created", e);
		}
	}

	/**
	 * Returns the AudioFormat for the default AudioInputStream
	 * 
	 * @return fitting AudioFormat
	 */
	private AudioFormat getFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
}
