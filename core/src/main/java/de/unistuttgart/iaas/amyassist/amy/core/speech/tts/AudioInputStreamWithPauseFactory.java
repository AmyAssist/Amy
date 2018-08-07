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
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * A audio input stream which contains a pause at the end
 * 
 * @author Tim Neumann
 */
public class AudioInputStreamWithPauseFactory {

	/**
	 * Creates a new audio input stream with pause from the given audio input stream.
	 * 
	 * @param stream
	 *            The original stream.
	 * @param pause
	 *            The milliseconds to pause at the end.
	 * @return The new stream.
	 */
	public AudioInputStream getAudioInputStreamWithPause(AudioInputStream stream, int pause) {
		int newFrameCount = calcNewNumFrames(pause, stream.getFormat());
		int numZeros = newFrameCount * stream.getFormat().getFrameSize();
		InputStream is = new InputStreamWithZeros(stream, numZeros);
		long completeFrameCount = stream.getFrameLength() + newFrameCount;
		if (stream.getFrameLength() == AudioSystem.NOT_SPECIFIED) {
			completeFrameCount = AudioSystem.NOT_SPECIFIED;
		}
		return new AudioInputStream(is, stream.getFormat(), completeFrameCount);
	}

	private int calcNewNumFrames(int pause, AudioFormat format) {
		double pauseInSeconds = pause / 1000.0;
		long frames = Math.round(format.getFrameRate() * pauseInSeconds);
		if (frames > Integer.MAX_VALUE || frames < 0) // If negative we had an overflow.
			return Integer.MAX_VALUE;
		return (int) frames;
	}

	private class InputStreamWithZeros extends InputStream {

		private InputStream is;
		private int numZeros;
		private int zerosAppended;

		/**
		 * 
		 * Creates a new input stream from the given stream with the number of zeros appended before end of stream
		 * 
		 * @param stream
		 *            The original stream.
		 * @param numberOfZeros
		 *            The number of zeros to append
		 */
		public InputStreamWithZeros(InputStream stream, int numberOfZeros) {
			this.is = stream;
			this.numZeros = numberOfZeros;
			this.zerosAppended = 0;
		}

		/**
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			int tmp = this.is.read();
			if ((this.zerosAppended == 0 && tmp == -1)
					|| (this.zerosAppended > 0 && this.zerosAppended < this.numZeros))
				return 0;
			return tmp;
		}

		/**
		 * @see java.io.InputStream#read(byte[], int, int)
		 */
		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (b == null)
				throw new NullPointerException();
			else if (off < 0 || len < 0 || len > b.length - off)
				throw new IndexOutOfBoundsException();
			else if (len == 0)
				return 0;

			if (this.zerosAppended > 0 && this.zerosAppended < this.numZeros)
				return zerosToBuffer(b, off, len);

			int numRead = this.is.read(b, off, len);

			if (numRead == -1)
				return zerosToBuffer(b, off, len);

			return numRead;
		}

		private int zerosToBuffer(byte[] b, int off, int len) {
			int numOfZerosToReturn = Math.min(len - off, this.numZeros - this.zerosAppended);
			for (int i = 0; i < numOfZerosToReturn; i++) {
				b[off + i] = 0;
				this.zerosAppended++;
			}
			return numOfZerosToReturn;
		}

	}
}
