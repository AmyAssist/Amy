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

package de.unistuttgart.iaas.amyassist.amy.core.output;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.output.tts.TextToSpeech;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;

/**
 * This class outputs
 * 
 * @author Tim Neumann, Kai Menzel
 */
@Service(OutputImpl.class)
public class OutputImpl implements Output, Runnable, RunnableService {

	private static final int WAIT_TIME_AFTER_SPEECH = 1000;
	private static final int BYTE_BUFFER_SIZE = 1024;
	private static final int QUEUE_SIZE = 10;

	@Reference
	private Logger logger;
	@Reference
	private TextToSpeech tts;
	@Nonnull
	private SourceDataLine outputLine;
	@Nonnull
	private BlockingQueue<OutputObject> queue;

	private volatile boolean isTalking = false;
	private volatile boolean stop = false;

	private Thread thread;

	@PostConstruct
	private void init() {
		this.queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
		try {

			// We need to do this, because the audio format depends on the voice of mary
			AudioFormat audioFormat = this.tts.getMaryAudioFormat();

			if (!AudioSystem.isLineSupported(new DataLine.Info(SourceDataLine.class, audioFormat))) {
				this.logger.error("The Audio System does not support the required ");
			} else {
				this.outputLine = AudioSystem.getSourceDataLine(audioFormat);
				this.outputLine.open(audioFormat);
				this.outputLine.start();
			}
		} catch (LineUnavailableException e) {
			this.logger.error("initialization error", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void start() {
		this.thread = new Thread(this, "TextToSpeech");
		this.thread.start();
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				OutputObject output = this.queue.take();
				this.isTalking = true;
				this.writeAudio(output);
				Thread.sleep(WAIT_TIME_AFTER_SPEECH);
				this.isTalking = false;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			this.logger.info("Text to speech stopped");
		}
	}

	private void writeAudio(OutputObject output) {
		switch (output.getType()) {
		case VOICE:
			try {
				try {
					moveBytes(this.tts.getMaryAudio(output.getMessage()), this.outputLine);					
				}catch (NullPointerException e) {
					this.logger.error("TextToSpeech missing", e);
				}
			} catch (IOException e) {
				this.logger.error("Error from MaryTTS InputStream", e);
			}
			break;
		case SOUND:
			try {
				moveBytes(AudioSystem.getAudioInputStream(
						this.getClass().getClassLoader().getResource(output.getMessage())), this.outputLine);
			} catch (IOException e) {
				this.logger.error("Error from Beep Sound", e);
			} catch (UnsupportedAudioFileException e) {
				this.logger.error("Beep output error", e);
			}
			break;
		default:
			break;
		}

	}

	private void moveBytes(AudioInputStream ais, SourceDataLine sdl) throws IOException {
		byte[] buffer = new byte[BYTE_BUFFER_SIZE];
		int readBytes = 0;

		while (!this.stop && !Thread.currentThread().isInterrupted() && readBytes >= 0) {
			readBytes = ais.read(buffer, 0, BYTE_BUFFER_SIZE);

			if (readBytes > 0) {
				sdl.write(buffer, 0, readBytes);
			}
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
	public synchronized void voiceOutput(String s) {
		this.logger.info("saying: {}", s);
		this.stop = false;
		this.speak(new OutputObject(OutputType.VOICE, preProcessing(s)));
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.output.Output#soundOutput(de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds)
	 */
	@Override
	public void soundOutput(Sounds sound) {
		this.stop = false;
		this.speak(new OutputObject(OutputType.SOUND, sound.getFileAsString()));
	}

	/**
	 * This method stops the output immediately.
	 * 
	 * The thread, will continue to live for a bit by sleeping. Therefore {@link #isCurrentlyOutputting()} will also
	 * keep returning true for a moment. This is to make sure there is a small break between outputs.
	 */
	@Override
	public synchronized void stopOutput() {
		this.queue.clear();
		this.stop = true;
	}

	@Override
	public boolean isCurrentlyOutputting() {
		return this.isTalking;
	}

	/**
	 * outputs Speech translated from given String
	 * 
	 * @param output
	 *            String that shall be said
	 */
	private void speak(OutputObject output) {
		boolean offer = this.queue.offer(output);
		if (!offer) {
			this.logger.warn("the text '{}' could not be outputted by the TTS, because the queue is full", output);
		}
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

	@Override
	public void stop() {
		this.thread.interrupt();
	}

}