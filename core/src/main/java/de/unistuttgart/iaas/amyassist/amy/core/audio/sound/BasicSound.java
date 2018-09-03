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

package de.unistuttgart.iaas.amyassist.amy.core.audio.sound;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * The implementatio of {@link Sound}
 * 
 * @author Tim Neumann
 */
public class BasicSound implements Sound {
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
	public BasicSound(AudioInputStream audioData) throws IOException {
		this.format = audioData.getFormat();
		if (audioData.getFrameLength() != AudioSystem.NOT_SPECIFIED) {
			this.data = new byte[(int) (audioData.getFrameLength() * audioData.getFormat().getFrameSize())];
			int bytesRead = 0;
			int bytesReadTotal = 0;
			do {
				bytesRead = audioData.read(this.data, bytesReadTotal, this.data.length - bytesReadTotal);
				if (bytesRead < 0)
					throw new IllegalStateException("Stream not as long as specified.");
				bytesReadTotal += bytesRead;
			} while (bytesReadTotal < this.data.length);
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

			this.frameLength = (int) Math.ceil((double) bytesReadTotal / this.format.getFrameSize());

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

	@Override
	public SoundPlayer getSinglePlayer() {
		return new BasicSoundPlayer(this.data, this.format, 1);
	}

	@Override
	public SoundPlayer getLoopPlayer(int loopCount) {
		if (loopCount < 0)
			throw new IllegalArgumentException("The loop count must be > 0");
		return new BasicSoundPlayer(this.data, this.format, loopCount);
	}

	@Override
	public SoundPlayer getInfiniteLoopPlayer() {
		return new BasicSoundPlayer(this.data, this.format, -1);
	}

	@Override
	public int getFrameLength() {
		return this.data.length / this.format.getFrameSize();
	}

	@Override
	public AudioFormat getFormat() {
		return this.format;
	}

}
