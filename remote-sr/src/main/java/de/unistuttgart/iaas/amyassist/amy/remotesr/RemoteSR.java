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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Properties;

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
@Service(RemoteSR.class)
public class RemoteSR {

	private static final String CONFIG_NAME = "remotesr.config";
	private static final String CHROME_DIRECTORY_CONFIG_KEY = "chrome";
	private static final String CHROME_DIRECTORY_DEFAULT_MAC = "chrome_default_mac";
	private static final String CHROME_DIRECTORY_DEFAULT_WINDOWS = "chrome_default_windows";

	@Reference
	private Logger logger;

	@Reference
	private ConfigurationManager configurationManager;

	private static final String START_SR_EVENT = "START";

	private SSEClient client = null;

	private RemoteSRListener listener;

	/**
	 * launch the chrome browser, open the remote-SR url immediately and prevent microphone permission dialog by using a
	 * prepared user profile
	 *
	 * @throws LaunchChromeException
	 *             Exception Signaling that something went wrong with launching Chrome
	 */
	public void launchChrome() throws LaunchChromeException {
		try {
			String file = generateTempProfile();

			Properties config = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);

			String chromePath = config.getProperty(CHROME_DIRECTORY_CONFIG_KEY);

			if (chromePath == null || chromePath.isEmpty()) {
				if (SystemUtils.IS_OS_MAC_OSX) {
					chromePath = config.getProperty(CHROME_DIRECTORY_DEFAULT_MAC);
				} else if (SystemUtils.IS_OS_WINDOWS) {
					chromePath = config.getProperty(CHROME_DIRECTORY_DEFAULT_WINDOWS);
				} else {
					this.logger
							.error("No directory for chrome installation found. Set your chrome installation directory "
									+ "in {}.properties", CONFIG_NAME);
				}
			}

			Process process = new ProcessBuilder(chromePath, "http://localhost:8080/rest/remotesr",
					"--user-data-dir=" + file).start();

			watchProcess(process, new BufferedReader(new InputStreamReader(process.getInputStream(),
					StandardCharsets.UTF_8)));
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
			if (chromeProfileJarDir == null) {
				throw new LaunchChromeException("Couldn't get chrome_profile resource");
			}

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

		FileSystem fs = null;
		try {
			if (sourceURI.getScheme().equals("jar")) {
				fs = FileSystems.newFileSystem(sourceURI, Collections.<String, String> emptyMap());
			}

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
		} finally {
			if (fs != null) {
				fs.close();
			}
		}
	}

	private void watchProcess(Process process, BufferedReader reader) {
		new Thread(() -> {
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					this.logger.warn("Chrome: {}", line);
				}
				process.waitFor();
				this.logger.warn("Chrome quit unexpectedly");
				launchChrome();

			} catch (IOException e) {
				this.logger.warn("Couldn't read output from Chrome:", e);
			} catch (LaunchChromeException e) {
				this.logger.warn("Couldn't restart Chrome: ", e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}).start();
	}

	void setClient(SSEClient client) {
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
	public boolean requestSR() {
		return this.client != null && client.sendEvent(START_SR_EVENT);
	}

	void processResult(SRResult res) {
		if (res != null && res.getText() != null && this.listener != null) {
			this.listener.remoteSRDidRecognizeSpeech(res.getText());
		}
	}

	public void setListener(RemoteSRListener listener) {
		this.listener = listener;
	}

	/**
	 * Exception that Singnals that there was an Error with the Chrome Launch
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

}
