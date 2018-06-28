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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to handle the Speech Input and Output
 * 
 * @author Kai Menzel
 */
public class AudioUserInteraction implements SpeechIO {

	private final Logger logger = LoggerFactory.getLogger(AudioUserInteraction.class);

	private boolean voiceOutput = true;

	private boolean recognitionThreadRunning = false;

	private Thread currentRecognizer;

	private SpeechInputHandler inputHandler;

	private AudioInputStream ais;

	/**
	 * The Grammar of the Main Menu (default)
	 */
	Grammar mainGrammar;
	private List<Grammar> switchableGrammars;

	private MainSpeechRecognizer mainRecognizer;
	private Map<String, AdditionalSpeechRecognizer> recognizerList = new HashMap<>();

	// -----------------------------------------------------------------------------------------------

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		createNewAudioInputStream();
		if (this.inputHandler == null) {
			this.logger.error("Not enough Data to start the Recognition - InputHandler not Set");
		} else {
			this.mainRecognizer = new MainSpeechRecognizer(this, this.mainGrammar, this.inputHandler, this.ais,
					this.voiceOutput);
			if (this.switchableGrammars != null && !this.switchableGrammars.isEmpty()) {
				for (Grammar grammar : this.switchableGrammars) {
					if (!this.recognizerList.containsKey(grammar.getName())) {
						this.recognizerList.put(grammar.getName(), new AdditionalSpeechRecognizer(this, grammar,
								this.inputHandler, this.ais, this.voiceOutput));
					}
				}
			}
			this.currentRecognizer = new Thread(this.mainRecognizer);
			this.recognitionThreadRunning = true;
			this.currentRecognizer.start();
		}

	}

	/**
	 * @param grammar
	 *            Grammar to switch to
	 */
	public void switchGrammar(Grammar grammar) {
		if (grammar == null) {
			this.logger.info("all Recognition stopped");
		} else if (grammar.getName().equals(this.mainGrammar.getName())) {
			this.logger.info("Recognizer {} started", this.mainGrammar.getName());
			this.currentRecognizer = new Thread(this.mainRecognizer);
			this.recognitionThreadRunning = true;
			this.currentRecognizer.start();
		} else {
			this.logger.info("Recognizer {} started", this.recognizerList.get(grammar.getName()));
			this.currentRecognizer = new Thread(this.recognizerList.get(grammar.getName()));
			this.recognitionThreadRunning = true;
			this.currentRecognizer.start();
		}

	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * call this before calling start()
	 * 
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO#setSpeechInputHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler)
	 */
	@Override
	public void setSpeechInputHandler(SpeechInputHandler handler) {
		this.inputHandler = handler;
	}

	/**
	 * call this to initiate Data call before start()
	 * 
	 * @param mainGrammar
	 *            The Main Grammar Mary uses
	 * @param swtichableGrammars
	 *            All possible Grammar that can be changed to
	 */
	public void setGrammars(Grammar mainGrammar, List<Grammar> swtichableGrammars) {
		this.mainGrammar = mainGrammar;
		this.switchableGrammars = swtichableGrammars;
	}

	/**
	 * Call this if you want to set a not default AudioInputStream Call this before calling start()
	 * 
	 * @param ais
	 *            AudioInputStream from which shall not be created here by default
	 */
	public void setAudioInputStream(AudioInputStream ais) {
		this.ais = ais;
	}

	/**
	 * Set's {@link #recognitionThreadRunning recognitionThreadRunning}
	 * 
	 * @param recognitionThreadRunning
	 *            recognitionThreadRunning
	 */
	public void setRecognitionThreadRunning(boolean recognitionThreadRunning) {
		this.recognitionThreadRunning = recognitionThreadRunning;
	}

	/**
	 * method to enable or disable the actual Voice TTS Output
	 * 
	 * @param isOutput
	 *            true if voice Output true, default true
	 */
	public void setVoiceOutput(boolean isOutput) {
		this.voiceOutput = isOutput;
	}

	// ===============================================================================================

	/**
	 * Get's {@link #recognitionThreadRunning recognitionThreadRunning}
	 * 
	 * @return recognitionThreadRunning
	 */
	public boolean isRecognitionThreadRunning() {
		return this.recognitionThreadRunning;
	}

	/**
	 * Get's {@link #currentRecognizer currentRecognizer}
	 * 
	 * @return currentRecognizer
	 */
	public Thread getCurrentRecognizer() {
		return this.currentRecognizer;
	}

	/**
	 * Get's {@link #mainGrammar mainGrammar}
	 * 
	 * @return mainGrammar
	 */
	public Grammar getMainGrammar() {
		return this.mainGrammar;
	}

	/**
	 * Get's {@link #switchableGrammars switchableGrammars}
	 * 
	 * @return switchableGrammars
	 */
	public List<Grammar> getSwitchableGrammars() {
		return this.switchableGrammars;
	}

	// ===============================================================================================

	/**
	 * starts the default AudioInputStream
	 */
	private void createNewAudioInputStream() {
		if (this.ais == null) {
			TargetDataLine mic = null;
			try {
				mic = AudioSystem.getTargetDataLine(this.getFormat());
				mic.open(this.getFormat());
				mic.start();
				this.ais = new AudioInputStream(mic);
			} catch (LineUnavailableException e) {
				this.logger.error("Error in createNewAudioInputStream", e);
			}
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
