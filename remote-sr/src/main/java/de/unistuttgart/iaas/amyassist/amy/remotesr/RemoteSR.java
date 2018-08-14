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

package de.unistuttgart.iaas.amyassist.amy.remotesr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Class to initiate the remote SR
 * 
 * @author Benno Krauß
 */
@Service
public class RemoteSR {

	private static final String CONFIG_NAME = "remotesr.config";
	private static final String CHROME_DIRECTORY_CONFIG_KEY = "chrome";

	private static final String MAC_CHROME_PATH = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" + "";
	private static final String WINDOWS_CHROME_PATH = "/Program Files (x86)/Google/Chrome/Application/chrome.exe" + "";

	@Reference
	private Logger logger;

	@Reference
	public ConfigurationManager configurationManager;

	private static final String START_SR_EVENT = "START";

	private SSEClient client = null;

	private RemoteSRListener listener;

	/**
	 * launch the chrome browser, open the remote-SR url immediately and prevent microphone permission dialog by using a
	 * prepared user profile
	 * 
	 * @throws LaunchChromeException
	 */
	public void launchChrome() throws LaunchChromeException {
		try {
			URL fileURL = getClass().getClassLoader().getResource("chrome_profile");
			if (fileURL == null) {
				throw new LaunchChromeException("Couldn't get chrome_profile resource");
			}
			String file = fileURL.getFile();

			String chromePath = configurationManager.getConfiguration(CONFIG_NAME)
					.getProperty(CHROME_DIRECTORY_CONFIG_KEY);

			if (chromePath == null) {
				if (SystemUtils.IS_OS_MAC_OSX) {
					chromePath = MAC_CHROME_PATH;
				} else if (SystemUtils.IS_OS_WINDOWS) {
					chromePath = WINDOWS_CHROME_PATH;
				} else {
					logger.error("No directory for chrome installation found. Set your chrome installation directory "
							+ "in {}.properties", CONFIG_NAME);
				}
			}

			Process process = new ProcessBuilder(chromePath, "http://localhost:8080/rest/remotesr",
					"--user-data-dir=" + file).start();

			watchProcess(process, new BufferedReader(new InputStreamReader(process.getInputStream())));
		} catch (IOException e) {
			throw new LaunchChromeException("Couldn't start Chrome:", e);
		}
	}

	private void watchProcess(Process process, BufferedReader reader) {
		new Thread(() -> {
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					logger.warn("Chrome: {}", line);
				}
				if (!process.isAlive()) {
					logger.warn("Chrome quit unexpectedly");
				}
			} catch (IOException e) {
				logger.warn("Couldn't read output from Chrome:", e);
			}
		}).start();
	}

	void setClient(SSEClient client) {
		if (this.client != null && this.client.isConnected()) {
			logger.warn("New SSE client connected before the old one disconnected");
			this.client.disconnect();
		}
		this.client = client;
	}

	/**
	 * request remote SR
	 * 
	 * @return true if the request was successful
	 */
	public boolean requestSR() {
		return client != null && client.sendEvent(START_SR_EVENT);
	}

	void processResult(SRResult res) {
		if (res != null && res.getText() != null && !res.getText().isEmpty() && listener != null) {
			listener.remoteSRDidRecognizeSpeech(res.getText());
		}
	}

	public void setListener(RemoteSRListener listener) {
		this.listener = listener;
	}

	/**
	 * Exception that Singnals that there was an Error with the Chrome Launch
	 */
	private class LaunchChromeException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param s
		 *            Message of the Exception
		 * 
		 */
		public LaunchChromeException(String s) {
			super(s);
		}

		/**
		 * @param string
		 *            Message of the Exception
		 * @param e
		 *            Error Message
		 */
		public LaunchChromeException(String string, Exception e) {
			super(string, e);
		}
	}
}
