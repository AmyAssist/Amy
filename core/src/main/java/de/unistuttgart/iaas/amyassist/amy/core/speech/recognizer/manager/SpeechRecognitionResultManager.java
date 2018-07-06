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

import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;

/**
 * Interface between the Result of the Recognition and the System
 * 
 * @author Kai Menzel
 */
public interface SpeechRecognitionResultManager {

	/**
	 * Method called when listening State of the SpeechRecognition is changed
	 * 
	 * @param srListening
	 *            true if SpeechRecognition is Currently Listening
	 */
	void handleListeningState(boolean srListening);

	/**
	 * Method to check if the SpeechRecognition is Currently Listening
	 * 
	 * @return true if SR System is Listening
	 */
	boolean isListening();

	/**
	 * Switch to the given Grammar
	 * 
	 * @param grammar
	 *            Grammar to switch to
	 */
	void handleGrammarSwitch(Grammar grammar);

	/**
	 * Handle the Result of the Recognition as Command for the Main System
	 * 
	 * @param result
	 *            Command for the Main System
	 */
	void handleCommand(String result);

	/**
	 * Voice the given String
	 * 
	 * @param outputString
	 *            String to voice
	 */
	void voiceOutput(String outputString);

	/**
	 * stop the voice output
	 */
	void stopOutput();

	/**
	 * Set boolean if the output is Currently Active
	 * 
	 * @param outputActive
	 *            true if sound Playing
	 */
	void setSoundPlaying(boolean outputActive);

	/**
	 * Method to check if output is currently active
	 * 
	 * @return true if sound Playing
	 */
	boolean isSoundPlaying();

	/**
	 * Method to set if the Current RecognitionThread should be running
	 * 
	 * @param recognitionRunning
	 *            false if the current recognizer shall be stopped
	 */
	void setRecognitionThreadRunning(boolean recognitionRunning);

	/**
	 * Method to check if Recognition Thread is Running
	 * 
	 * @return if the Recognition Thread should be running
	 */
	boolean isRecognitionThreadRunning();

}
