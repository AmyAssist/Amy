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

package de.unistuttgart.iaas.amyassist.amy.core.speech.tts;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

/**
 * This class outputs Strings as Voice using MaryTTS.
 * 
 * @see <a href="https://github.com/marytts/marytts">MaryTTS</a>
 * 
 * @author Tim Neumann, Kai Menzel
 */
@Service(TextToSpeech.class)
public class TextToSpeech implements Output {

	@Reference
	private Logger logger;

	private LocalMaryInterface mary;

	private SourceDataLine outputLine;

	private Thread currentAudioWriterThread;
	private Queue<Thread> nextAudioWriterThreads;

	/**
	 * @return The {@link AudioFormat} used by the TTS
	 */
	protected AudioFormat getAudioFormat() {
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
	}

	@PostConstruct
	private void init() {
		this.nextAudioWriterThreads = new ConcurrentLinkedQueue<>();
		this.currentAudioWriterThread = new Thread();
		try {
			this.mary = new LocalMaryInterface();
			this.mary.setVoice("dfki-poppy-hsmm");
			this.outputLine = AudioSystem.getSourceDataLine(this.getAudioFormat());
			this.outputLine.open(this.getAudioFormat());
			this.outputLine.start();
		} catch (MaryConfigurationException | LineUnavailableException e) {
			this.logger.error("initialization error", e);
			throw new IllegalStateException(e);
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * Method to Voice and Log output the input String
	 * 
	 * @param s
	 *            String that shall be said
	 */
	@Override
	public void output(String s) {
		this.logger.info("saying: {}", s);
		speak(preProcessing(s));
	}

	/**
	 * This method stops the output immediately.
	 * 
	 * The thread, will continue to live for a bit by sleeping. Therefore {@link #isCurrentlyOutputting()} will also
	 * keep returning true for a moment. This is to make sure there is a small break between outputs.
	 */
	@Override
	public void stopOutput() {
		if (isCurrentlyOutputting()) {
			this.currentAudioWriterThread.interrupt();
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.tts.Output#isCurrentlyOutputting()
	 */
	@Override
	public boolean isCurrentlyOutputting() {
		this.logger.info("TTS Writer alive: {} and state: {} ", this.currentAudioWriterThread.isAlive(),
				this.currentAudioWriterThread.getState());
		return this.currentAudioWriterThread.isAlive();
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * outputs Speech translated from given String
	 * 
	 * @param s
	 *            String that shall be said
	 */
	private void speak(String s) {
		stopOutput();

		System.out.println("speak");

		try {
			this.nextAudioWriterThreads
					.add(new Thread(new AudioWriter(this.mary.generateAudio(s), this.outputLine, 3000)));
			new Thread(() -> startNextAudioWriterThread()).run();
		} catch (SynthesisException e) {
			this.logger.error("output error", e);
		}

	}

	/**
	 * Waits for the current AudioWriterThread to finish and then starts the next AudioWriterThread from the queue.
	 * 
	 * This is needed because the thread will sleep a bit after stopOutput() is called. See {@link #stopOutput()} for
	 * more information.
	 * 
	 * This method waits until the last thread is finished, therefore this should only be called from a separate Thread.
	 */
	private void startNextAudioWriterThread() {
		System.out.println("startNext");

		try {
			this.currentAudioWriterThread.join();
		} catch (InterruptedException e) {
			this.logger.warn("Was interrupted while waiting for old thread to finnish", e);
		}
		this.currentAudioWriterThread = this.nextAudioWriterThreads.poll();
		if (this.currentAudioWriterThread == null)
			throw new IllegalStateException("Can't start next audio writer, because queue is empty.");

		System.out.println("starting next");
		this.currentAudioWriterThread.start();

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