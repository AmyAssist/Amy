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

package de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.LocalAudio;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.NLProcessingManager;
import de.unistuttgart.iaas.amyassist.amy.core.service.DeploymentContainerService;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR;
import de.unistuttgart.iaas.amyassist.amy.remotesr.RemoteSR.LaunchChromeException;

/**
 * Class that Creates all Grammar Objects sets the MainGrammar and the List of all other Grammars
 * 
 * @author Kai Menzel
 */

@Service(RecognizerCreator.class)
public class RecognizerCreator implements DeploymentContainerService {

	// --------------------------------------------------------------
	// Dependencies

	@Reference
	private Environment environment;
	@Reference
	private NLProcessingManager nlProcessingManager;
	@Reference
	private RemoteSR googleRecognition;
	@Reference
	private AudioManager audioManager;
	@Reference
	private LocalAudio localAudio;
	@Reference
	private ConfigurationManager configManager;

	// --------------------------------------------------------------
	// Fields

	private Properties config;

	private static final String CONFIG_NAME = "localSpeech.config";
	private static final String PROPERTY_ENABLE = "enable";

	private boolean recognitionDisabled = false;

	private String mainGrammarName = "mainGrammar";
	// private String tempGrammarName = "tempGrammar"

	private AudioInputStream ais = null;

	// --------------------------------------------------------------
	// Construcor

	// --------------------------------------------------------------
	// Init

	/**
	 * Call this after all register and before process
	 */
	@Override
	public void deploy() {
		Path grammarFolder = this.environment.getWorkingDirectory().resolve("resources").resolve("sphinx-grammars");
		Path mainGrammarFile = grammarFolder.resolve(this.mainGrammarName + ".gram");
		// Path tempGrammarFile = grammarFolder.resolve(this.tempGrammarName + ".gram")

		// Create mainGrammar.gram
		try {
			Files.createDirectories(grammarFolder);
		} catch (IOException e) {
			throw new IllegalStateException("Can't create parent directories of the grammar file", e);
		}

		try (BufferedWriter bw = Files.newBufferedWriter(mainGrammarFile, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			bw.write(this.nlProcessingManager.getGrammarFileString(this.mainGrammarName));
		} catch (IOException e) {
			throw new IllegalStateException("Can't write grammar file", e);
		}

		loadAndCheckProperties();

		if (Boolean.parseBoolean(this.config.getProperty(PROPERTY_ENABLE))) {
			try {
				this.googleRecognition.launchChrome();
			} catch (LaunchChromeException e) {
				e.printStackTrace();
				this.recognitionDisabled = true;
				return;
			}
			Grammar.GOOGLE.initiateAsRemoteRecognizer(this.googleRecognition);
			// Grammar.TEMP.initiateAsSphinxRecognizer(tempGrammarFile.toFile(), getAudioinputStream())
			Grammar.MAIN.initiateAsSphinxRecognizer(mainGrammarFile.toFile(), getAudioinputStream());
		} else {
			this.recognitionDisabled = true;
		}
	}

	// --------------------------------------------------------------
	// Methods

	private AudioInputStream getAudioinputStream() {
		if (this.ais == null) {
			this.ais = createNewAudioInputStream();
		}
		return this.ais;
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
	 * Check if the Recognition System is Disabled
	 * 
	 * @return true if Recognition is disabled
	 */
	public boolean isRecognitionDisabled() {
		return this.recognitionDisabled;
	}

	// --------------------------------------------------------------
	// Setter

}
