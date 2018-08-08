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

package de.unistuttgart.iaas.amyassist.amy.utility.audio.sound;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A class containing a sound, which can be played.
 * 
 * @author Tim Neumann
 */
public class Sound {
	private static final int BUFFER_SIZE_INCR = 100;

	private byte[] data;
	private int frameLength;

	private AudioFormat format;

	/**
	 * Creates a new sound and reads the data for it from the given {@link AudioInputStream}
	 * 
	 * @param audioData
	 *            The stream delivering the audio data for this sound
	 * @throws IOException
	 *             if an input or output error occurs while reading from the stream.
	 */
	public Sound(AudioInputStream audioData) throws IOException {
		this.format = audioData.getFormat();
		if (audioData.getFrameLength() != AudioSystem.NOT_SPECIFIED) {
			this.data = new byte[(int) (audioData.getFrameLength() * audioData.getFormat().getFrameSize())];
			audioData.read(this.data, 0, this.data.length);
		} else {
			int bytesReadTotal = 0;
			int bytesRead;
			byte[] tmp = new byte[BUFFER_SIZE_INCR];
			do {
				tmp = Arrays.copyOf(tmp, tmp.length + BUFFER_SIZE_INCR);
				bytesRead = audioData.read(tmp, bytesReadTotal, tmp.length - bytesReadTotal);
				if (bytesRead > 0) {
					bytesReadTotal += bytesRead;
				}
			} while (bytesRead != -1);

			this.frameLength = (int) Math.ceil(bytesReadTotal / this.format.getFrameSize());

			int byteLength = this.frameLength * this.format.getFrameSize();

			if (bytesReadTotal < byteLength) {
				int bytesToAdd = byteLength - bytesReadTotal;
				if (bytesReadTotal + bytesToAdd > tmp.length) {
					tmp = Arrays.copyOf(tmp, bytesReadTotal + bytesToAdd);
				}

				for (int i = bytesReadTotal; i < byteLength; i++) {
					tmp[i] = 0;
					bytesReadTotal++;
				}
			}

			this.data = Arrays.copyOf(tmp, byteLength);
		}
		audioData.close();
	}

	/**
	 * Creates a new sound from the file at the given URL.
	 * 
	 * @param audioFile
	 *            The URL of the file to load
	 * @throws UnsupportedAudioFileException
	 *             When the file at the URL is not in a supported format
	 * @throws IOException
	 *             if an input or output error occurs while reading the file
	 */
	public Sound(URL audioFile) throws UnsupportedAudioFileException, IOException {
		AudioSystem.getAudioInputStream(audioFile);
	}

	/**
	 * Creates a new sound from the file at the given Path.
	 * 
	 * @param audioFile
	 *            The Path of the file to load
	 * @throws UnsupportedAudioFileException
	 *             When the file at the Path is not in a supported format
	 * @throws IOException
	 *             if an input or output error occurs while reading the file
	 */
	public Sound(Path audioFile) throws UnsupportedAudioFileException, IOException {
		AudioSystem.getAudioInputStream(audioFile.toFile());
	}

	/**
	 * Get a sound player that will play this audio once.
	 * 
	 * @return The {@link SoundPlayerImpl}.
	 */
	public SoundPlayer getSinglePlayer() {
		return new SoundPlayerImpl(this.data, this.format, 1);
	}

	/**
	 * Get a sound player that will play this audio the given amount of times.
	 * 
	 * @param loopCount
	 *            How often to loop. Must be > 0
	 * @return The {@link SoundPlayerImpl}
	 */
	public SoundPlayer getLoopPlayer(int loopCount) {
		if (loopCount < 0)
			throw new IllegalArgumentException("The loop count must be > 0");
		return new SoundPlayerImpl(this.data, this.format, loopCount);
	}

	/**
	 * Get a sound player that will play this audio for ever.
	 * 
	 * @return The {@link SoundPlayerImpl}
	 */
	public SoundPlayer getInfiniteLoopPlayer() {
		return new SoundPlayerImpl(this.data, this.format, -1);
	}

	/**
	 * Get's the length of the sound, expressed in sample frames rather than bytes.
	 * 
	 * @return the length in sample frames
	 */
	public int getFrameLength() {
		return this.data.length / this.format.getFrameSize();
	}
}
