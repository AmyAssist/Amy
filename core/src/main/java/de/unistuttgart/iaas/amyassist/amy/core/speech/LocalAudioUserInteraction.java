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

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.UUID;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.LocalAudio;
import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.IDialogHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.LocalSpeechRecognizerManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;

/**
 * Manager of the Local Speech Recognition System
 * 
 * @author Kai Menzel
 */
@Service(LocalAudioUserInteraction.class)
public class LocalAudioUserInteraction implements AudioUserInteraction {

	private static final String CONFIG_NAME = "localSpeech.config";
	private static final String PROPERTY_ENABLE = "enable";

	@Reference
	private Logger logger;

	@Reference
	private IDialogHandler dialogHandler;

	@Reference
	private Output tts;

	@Reference
	private AudioManager am;

	@Reference
	private LocalAudio la;

	@Reference
	private GrammarObjectsCreator grammarData;

	@Reference
	private MessageHub messageHub;

	@Reference
	private ConfigurationManager configurationManager;

	private Properties config;

	private SpeechRecognizerManager localRecognition;

	@PostConstruct
	private void init() {
		loadAndCheckProperties();
		UUID dialog;
		if (Boolean.parseBoolean(this.config.getProperty(PROPERTY_ENABLE))) {
			dialog = this.dialogHandler.createDialog(this.tts::voiceOutput);

			this.localRecognition = new LocalSpeechRecognizerManager(createNewAudioInputStream(), input -> {
				this.dialogHandler.process(input, dialog);
			}, this.tts, this.grammarData, this.messageHub);
		}
	}

	private void loadAndCheckProperties() {
		this.config = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		if (this.config.getProperty(PROPERTY_ENABLE) == null)
			throw new IllegalStateException("Property " + PROPERTY_ENABLE + " missing in audio manager config.");
	}

	@Override
	public void start() {
		if (this.localRecognition != null) {
			this.localRecognition.start();
		}
	}

	@Override
	public void stop() {
		if (this.localRecognition != null) {
			this.localRecognition.stop();
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
			return this.am.getInputStreamOfAudioEnvironment(this.la.getLocalAudioEnvironmentIdentifier());
		} catch (NoSuchElementException e) {
			throw new IllegalStateException("Could not get local audio environment", e);
		}
	}
}
