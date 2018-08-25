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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.LocalSpeechInterpreter.ListeningState;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Constants;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;

/**
 * Handles some Predefined results of the stt
 * 
 * @author Kai Menzel
 */
@Service
public class SpeechResultPreHandler {

	@Reference
	private Logger logger;
	@Reference
	private Output output;
	@Reference
	private SpeechInputHandler inputHandler;

	private LocalSpeechInterpreter localSpeechInterpreter;

	/**
	 * Set's {@link #localSpeechInterpreter localSpeechInterpreter}
	 * 
	 * @param localSpeechInterpreter
	 *            localSpeechInterpreter
	 */
	public void setLocalSpeechInterpreter(LocalSpeechInterpreter localSpeechInterpreter) {
		this.localSpeechInterpreter = localSpeechInterpreter;
	}

	/**
	 * Method that handles the direct SR output
	 * 
	 * @param result
	 *            speechRecognitionResult
	 */
	public void handle(String result) {
		if (this.localSpeechInterpreter.getCurrentRecognizer() != null && !this.output.isCurrentlyOutputting()) {
			if (this.localSpeechInterpreter.getCurrentListeningState() == ListeningState.NOT_LISTENING) {
				switch (result.toLowerCase()) {
				case Constants.MULTI_CALL_START:
					this.localSpeechInterpreter.setListeningState(ListeningState.MULTI_CALL_LISTENING);
					break;
				case Constants.SINGLE_CALL_START:
					this.localSpeechInterpreter.setListeningState(ListeningState.SINGLE_CALL_LISTENING);
					break;
				default:
					break;
				}
			} else {
				switch (result.toLowerCase()) {
				case Constants.SHUT_UP:
					break;
				case Constants.MULTI_CALL_START:
					break;
				case Constants.SINGLE_CALL_START:
					break;
				case Constants.MULTI_CALL_STOP:
					this.localSpeechInterpreter.setListeningState(ListeningState.NOT_LISTENING);
					sendToNlProcessor(result);
					break;
				default:
					sendToNlProcessor(result);
					break;
				}
			}
		} else if (result.equalsIgnoreCase(Constants.SHUT_UP)) {
			this.output.stopOutput();
		}
		this.localSpeechInterpreter.nextRecognitionRequest();
	}

	/**
	 * send result to the nlprocessor;
	 * 
	 * @param result
	 *            SpeechRecognitionResult
	 */
	private void sendToNlProcessor(String result) {

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
			this.localSpeechInterpreter.setCurrentRecognizer(null);
			Thread.currentThread().interrupt();
		}

		if (this.localSpeechInterpreter.getCurrentListeningState() == ListeningState.SINGLE_CALL_LISTENING) {
			// IF Dialog is finished
			this.localSpeechInterpreter.setListeningState(ListeningState.NOT_LISTENING);
		}
	}
}
