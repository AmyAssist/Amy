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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.unistuttgart.iaas.amyassist.amy.utility.audio.QueuedInputStream;

/**
 * Player for sounds
 * 
 * @author Tim Neumann
 */
public class SoundPlayerImpl implements Runnable, SoundPlayer {

	private QueuedInputStream stream;
	private AudioInputStream audioStream;
	private byte[] data;
	private int pos;
	private int remainingLoopCount;

	Thread playingThread;

	/**
	 * Creates a new SoundPlayer for the given audioData with the given format and the given frameLength.
	 * 
	 * @param audioData
	 *            The audio data of the sound. This must fit the format.
	 * @param format
	 *            The audio format of the sound.
	 * @param loopCount
	 *            The amount of times to loop. Can be -1 to indicate infinite looping.
	 */
	protected SoundPlayerImpl(byte[] audioData, AudioFormat format, int loopCount) {
		if (loopCount == 0)
			throw new IllegalArgumentException("Can't loop 0 times.");
		if (loopCount < -1)
			throw new IllegalArgumentException("Loop counts below -1 are undefined.");
		if ((audioData.length % format.getFrameSize()) != 0)
			throw new IllegalArgumentException("The audioData does not fit the format.");

		this.stream = new QueuedInputStream();
		this.data = audioData;

		int singleFrameLength = audioData.length / format.getFrameSize();
		int frameLength = loopCount * singleFrameLength;

		if (loopCount < 0) {
			frameLength = AudioSystem.NOT_SPECIFIED;
		}

		this.audioStream = new AudioInputStream(this.stream, format, frameLength);
		this.remainingLoopCount = loopCount;
		this.pos = 0;

		this.playingThread = new Thread(this, "SoundPlayerThread");
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.audio.sound.SoundPlayer#getAudioStream()
	 */
	@Override
	public AudioInputStream getAudioStream() {
		return this.audioStream;
	}

	/**
	 * Does the output work.
	 */
	@Override
	public void run() {
		while (!Thread.interrupted() && this.remainingLoopCount != 0) {
			try {
				this.stream.getQueue().put(Byte.toUnsignedInt(this.data[this.pos]));
				this.pos++;
				if (this.pos >= this.data.length) {
					this.pos = 0;
					if (this.remainingLoopCount != -1) {
						this.remainingLoopCount--;
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		this.stream.setAutoEnding(true);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.audio.sound.SoundPlayer#start()
	 */
	@Override
	public void start() {
		this.playingThread.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.audio.sound.SoundPlayer#stop()
	 */
	@Override
	public void stop() {
		this.playingThread.interrupt();
	}

}
