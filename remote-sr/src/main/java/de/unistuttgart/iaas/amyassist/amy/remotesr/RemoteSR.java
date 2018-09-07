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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer;

/**
 * Class to initiate the remote SR
 * 
 * @author Benno Krauß, Tim Neumann
 */
@Service(RemoteSR.class)
public class RemoteSR implements SpeechRecognizer, RunnableService {

	private static final String CONFIG_NAME = "remotesr.config";
	private static final String CHROME_DIRECTORY_CONFIG_KEY = "chromeCmd";
	private static final String ENABLED_CONFIG_KEY = "enabled";

	@Reference
	private Logger logger;

	@Reference
	private ConfigurationManager configurationManager;

	private static final String START_SR_EVENT = "START";

	private SSEClient client = null;

	private Process chromeProcess;

	private volatile Consumer<String> listener = null;
	private volatile boolean currentlyRecognizing = false;
	private volatile boolean stop = false;

	private boolean isEnabled() {
		return Boolean.parseBoolean(
				this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME).getProperty(ENABLED_CONFIG_KEY));
	}

	/**
	 * Throw an exception if remoteSR is disabled via config
	 * 
	 * @throws IllegalStateException
	 *             the exception
	 */
	private void checkEnabled() {
		if (!isEnabled())
			throw new IllegalStateException("RemoteSR not enabled");
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#recognizeOnce(java.util.function.Consumer)
	 */
	@Override
	public void recognizeOnce(Consumer<String> resultHandler) {
		this.checkEnabled();
		if (this.currentlyRecognizing)
			throw new IllegalStateException("Already recognizing");
		this.listener = resultHandler;

		this.tryToRecognize();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#recognizeContinuously(java.util.function.Consumer)
	 */
	@Override
	public void recognizeContinuously(Consumer<String> resultHandler) {
		throw new UnsupportedOperationException("Continuous recognition is not supported");
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#stopContinuousRecognition()
	 */
	@Override
	public void stopContinuousRecognition() {
		throw new UnsupportedOperationException("Continuous recognition is not supported");
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer#isCurrentlyRecognizing()
	 */
	@Override
	public boolean isCurrentlyRecognizing() {
		return this.currentlyRecognizing;
	}

	/**
	 * launch the chrome browser, open the remote-SR url immediately and prevent microphone permission dialog by using a
	 * prepared user profile
	 *
	 * @throws LaunchChromeException
	 *             Exception Signaling that something went wrong with launching Chrome
	 */
	private void launchChrome() throws LaunchChromeException {
		this.checkEnabled();
		try {
			String file = generateTempProfile();

			Properties config = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);

			String chromePath = config.getProperty(CHROME_DIRECTORY_CONFIG_KEY);

			this.chromeProcess = new ProcessBuilder(chromePath, "http://localhost:8080/rest/remotesr",
					"--user-data-dir=" + file).start();

			watchProcess(new BufferedReader(
					new InputStreamReader(this.chromeProcess.getInputStream(), StandardCharsets.UTF_8)));
		} catch (IOException e) {
			throw new LaunchChromeException("Couldn't start Chrome:", e);
		}
	}

	/**
	 *
	 * @return file path of chrome profile in temp directory
	 * @throws LaunchChromeException
	 *             if any error occurs
	 */
	private String generateTempProfile() throws LaunchChromeException {
		try {
			URL chromeProfileJarDir = getClass().getClassLoader().getResource("chrome_profile");
			if (chromeProfileJarDir == null)
				throw new LaunchChromeException("Couldn't get chrome_profile resource");

			Path tempDir = Files.createTempDirectory(getClass().getSimpleName());

			copyFromJar(chromeProfileJarDir.toURI(), tempDir);

			return tempDir.toAbsolutePath().toString();

		} catch (IOException | URISyntaxException e) {
			throw new LaunchChromeException("Couldn't copy chrome_profile:", e);
		}
	}

	/**
	 * Copy a directory from our jar recursively to a directory outside of the jar
	 * 
	 * @param sourceURI
	 *            URI of the source directory
	 * @param target
	 *            target directory path
	 * @throws IOException
	 *             if any IO error occurs
	 */
	private void copyFromJar(final URI sourceURI, final Path target) throws IOException {
		try (FileSystem fs = (sourceURI.getScheme().equals("jar"))
				? FileSystems.newFileSystem(sourceURI, Collections.<String, String> emptyMap())
				: null) {
			final Path jarPath = Paths.get(sourceURI);

			Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
						throws IOException {
					Path currentTarget = target.resolve(jarPath.relativize(dir).toString());
					Files.createDirectories(currentTarget);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
					Files.copy(file, target.resolve(jarPath.relativize(file).toString()),
							StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	private void watchProcess(BufferedReader reader) {
		new Thread(() -> {
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					this.logger.warn("Chrome: {}", line);
				}
				this.chromeProcess.waitFor();
				if (!this.stop) {
					this.logger.warn("Chrome quit unexpectedly");
					launchChrome();
				}
			} catch (IOException e) {
				this.logger.warn("Couldn't read output from Chrome:", e);
			} catch (LaunchChromeException e) {
				this.logger.warn("Couldn't restart Chrome: ", e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}, "Chrome watcher thread").start();

		executeAfterSeconds(() -> {
			if (this.client == null || !this.client.isConnected()) {
				// No client connected right now. Assume something went wrong. Restart chrome.
				stop();
				start();
			}
		}, 5);
	}

	private void executeAfterSeconds(Runnable r, int seconds) {
		new Timer("RemoteSpeechTimer").schedule(new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		}, seconds * 1_000L);
	}

	/**
	 * Set the sse client
	 * 
	 * @param client
	 *            The client to set
	 */
	void setClient(SSEClient client) {
		this.checkEnabled();
		if (this.client != null && this.client.isConnected()) {
			this.logger.warn("New SSE client connected before the old one disconnected");
			this.client.disconnect();
		}
		this.client = client;
	}

	/**
	 * request remote SR
	 *
	 * @return true if the request was successful
	 */
	boolean tryToRecognize() {
		this.checkEnabled();
		if (this.client == null) {
			this.logger.error("Could not start recognition, because I have no client.");
			return false;
		} else if (!this.client.isConnected()) {
			this.logger.error("Could not start recognition, because the client is not connected.");
			return false;
		}
		this.currentlyRecognizing = true;
		return this.client.sendEvent(START_SR_EVENT);
	}

	/**
	 * Processes a result from the speech recognition.
	 * 
	 * @param res
	 *            The result.
	 */
	void processResult(SRResult res) {
		this.checkEnabled();
		if (res != null && res.getText() != null && this.listener != null) {
			this.currentlyRecognizing = false;
			this.listener.accept(res.getText());
		} else {
			tryToRecognize();
		}
	}

	/**
	 * Exception which signals that there was an Error with the Chrome Launch
	 */
	public class LaunchChromeException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param s
		 *            Message of the Exception
		 * 
		 */
		LaunchChromeException(String s) {
			super(s);
		}

		/**
		 * @param string
		 *            Message of the Exception
		 * @param e
		 *            Error Message
		 */
		LaunchChromeException(String string, Exception e) {
			super(string, e);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		if (!isEnabled())
			return;

		try {
			launchChrome();
		} catch (LaunchChromeException e) {
			throw new IllegalStateException("Unable to launch chrome", e);
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		if (!isEnabled())
			return;

		this.stop = true;
		this.chromeProcess.destroy();
	}

}
