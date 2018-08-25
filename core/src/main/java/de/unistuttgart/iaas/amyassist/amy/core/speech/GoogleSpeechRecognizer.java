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

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR.LaunchChromeException;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSRListener;

/**
 * Calls the remote SR Service
 * 
 * @author Kai Menzel
 */
@Service(GoogleSpeechRecognizer.class)
public class GoogleSpeechRecognizer implements RemoteSRListener, SpeechRecognizer {

	private static final int WAITING_FOR_CHROME_TO_START_TIME = 5000;
	private static final int MAX_WAIT_TIME = 50000;

	@Reference
	private Logger logger;
	@Reference
	private RemoteSR recognizer;
	@Reference
	private SpeechResultPreHandler resultHandler;

	@PostConstruct
	private void init() {
		this.recognizer.setListener(this);
	}

	/**
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#requestRecognition()
	 */
	@Override
	public void requestRecognition() {

		if (!this.recognizer.requestSR()) {

			try {
				this.recognizer.launchChrome();

				this.logger.info("Connecting to RemoteSR.");
				long t = 1;
				while (!this.recognizer.requestSR()) {
					this.logger.info("connecting...");

					Thread.sleep(t * WAITING_FOR_CHROME_TO_START_TIME);

					if (t++ > MAX_WAIT_TIME / WAITING_FOR_CHROME_TO_START_TIME) {
						requestRecognition();
						break;
					}
				}
				this.logger.info("Connected");

			} catch (LaunchChromeException | InterruptedException e) {
				this.logger.error("Error while waiting for Chrome:", e);
				Thread.currentThread().interrupt();
			}

		}

		this.logger.info("waiting for speech input");
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSRListener#remoteSRDidRecognizeSpeech(java.lang.String)
	 */
	@Override
	public void remoteSRDidRecognizeSpeech(String message) {
		if (message.isEmpty()) {
			requestRecognition();
		} else {
			this.resultHandler.handle(message);
		}
	}

}
