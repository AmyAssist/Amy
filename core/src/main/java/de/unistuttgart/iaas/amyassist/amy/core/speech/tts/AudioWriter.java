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

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a runnable responsible for writing audio from an {@link AudioInputStream} to a {@link SourceDataLine}
 * for playback.
 * 
 * @author Tim Neumann
 */
public class AudioWriter implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(AudioWriter.class);

	private AudioInputStream ais;
	private SourceDataLine sdl;
	private int millisToWait;

	/**
	 * Creates a new AudioWriter with the given audio source for the given output line.
	 * 
	 * @param audioSource
	 *            The audio source for this audioWriter
	 * @param outputLine
	 *            The outputLine for this audioWriter
	 * @param pauseAfterSpeech
	 *            The amount of milliseconds to pause after the speech ends.
	 */
	public AudioWriter(AudioInputStream audioSource, SourceDataLine outputLine, int pauseAfterSpeech) {
		this.ais = audioSource;
		this.sdl = outputLine;
		this.millisToWait = pauseAfterSpeech;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			moveBytes();
		} catch (IOException e) {
			this.logger.error("Error reading from MaryTTS InputStream", e);
		} finally {
			closeAis();
		}
		waitBeforeStop();
	}

	private void waitBeforeStop() {
		long startWait = System.currentTimeMillis();
		long endWait = startWait + this.millisToWait;

		long remainingMillis = this.millisToWait;

		do {
			try {
				Thread.sleep(remainingMillis);
			} catch (InterruptedException e) {
				// ignore because we need to make sure that many millis are waited, even though the thread may get
				// interrupted.
			}
			remainingMillis = endWait - System.currentTimeMillis();
		} while (remainingMillis > 0);
	}

	private void moveBytes() throws IOException {
		byte[] buffer = new byte[1024];
		int readBytes = 0;

		while (!Thread.currentThread().isInterrupted() && readBytes >= 0) {
			readBytes = this.ais.read(buffer, 0, 1024);

			if (readBytes > 0) {
				this.sdl.write(buffer, 0, readBytes);
			}
		}
	}

	private void closeAis() {
		try {
			this.ais.close();
		} catch (IOException e) {
			this.logger.error("Error closing MaryTTS InputStream", e);
		}
	}

}
