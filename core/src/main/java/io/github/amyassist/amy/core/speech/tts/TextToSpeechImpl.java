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

package io.github.amyassist.amy.core.speech.tts;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;

/**
 * Implementation of the TTS Interface
 * 
 * @author Kai Menzel
 */
@Service(TextToSpeech.class)
public class TextToSpeechImpl implements TextToSpeech {

	private static final int DEFAULT_WAIT_TIME_AFTER_SPEECH = 1000;

	@Reference
	private Logger logger;

	private LocalMaryInterface mary;

	private AudioInputStreamWithPauseFactory aisWithPauseFactory;

	private Voice voice;

	@PostConstruct
	private void init() {
		this.aisWithPauseFactory = new AudioInputStreamWithPauseFactory();
		try {
			this.mary = new LocalMaryInterface();
			this.voice = Voice.getVoice("dfki-poppy-hsmm");
			this.mary.setVoice(this.voice.getName());
		} catch (MaryConfigurationException e) {
			this.logger.error("Mary init error");
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @see io.github.amyassist.amy.core.speech.tts.TextToSpeech#getMaryAudio(java.lang.String)
	 */
	@Override
	public AudioInputStream getMaryAudio(String s) {
		return getMaryAudio(s, DEFAULT_WAIT_TIME_AFTER_SPEECH);
	}

	@Override
	public AudioInputStream getMaryAudio(String s, int millisecondsToPausetAfterSpeech) {
		try {
			return this.aisWithPauseFactory.getAudioInputStreamWithPause(this.mary.generateAudio(preProcessing(s)),
					millisecondsToPausetAfterSpeech);
		} catch (SynthesisException e) {
			this.logger.error("output error", e);
			return null;
		}

	}

	/**
	 * @see io.github.amyassist.amy.core.speech.tts.TextToSpeech#getMaryAudioFormat()
	 */
	@Override
	public AudioFormat getMaryAudioFormat() {
		// We need to do this, because the audio format depends on the voice of mary
		return this.voice.dbAudioFormat();
	}

	/**
	 * cleans String of SubString Mary can't pronounce
	 * 
	 * @param s
	 *            String Mary shall say
	 * @return cleaned String Mary shall say
	 */
	private String preProcessing(String s) {
		String text = s;
		text = text.replace("°C", " degree Celsius");
		text = text.replace("°F", " degree Fahrenheit");
		return text;
	}
}
