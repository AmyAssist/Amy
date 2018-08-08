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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
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

	@Reference
	private Logger logger;

	@Reference
	private SpeechInputHandler inputHandler;

	@Reference
	private Output tts;

	@Reference
	private AudioManager am;

	@Reference
	private GrammarObjectsCreator grammarData;

	@Reference
	private MessageHub messageHub;

	private SpeechRecognizerManager localRecognition;

	@PostConstruct
	private void init() {
		this.localRecognition = new LocalSpeechRecognizerManager(createNewAudioInputStream(), this.inputHandler,
				this.tts, this.grammarData, this.messageHub);
	}

	@Override
	public void start() {
		this.localRecognition.start();
	}

	@Override
	public void stop() {
		this.localRecognition.stop();
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
			return this.am
					.getInputStreamOfAudioEnvironment(this.am.getAllRegisteredAudioEnvironments().iterator().next());
		} catch (NoSuchElementException e) {
			throw new IllegalStateException("Could not find an audio environment");
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
