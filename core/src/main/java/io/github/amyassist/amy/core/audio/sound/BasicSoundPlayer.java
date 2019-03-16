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

package io.github.amyassist.amy.core.audio.sound;

import java.lang.Thread.State;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import io.github.amyassist.amy.core.audio.QueuedInputStream;

/**
 * Player for sounds
 * 
 * @author Tim Neumann
 */
public class BasicSoundPlayer implements Runnable, SoundPlayer {

	private final QueuedInputStream stream;
	private final AudioInputStream audioStream;

	private volatile boolean shouldStop = false;
	private byte[] data;
	private int remainingLoopCount;

	private Thread playingThread;

	private Consumer<SoundPlayer.StopReason> listener;

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
	protected BasicSoundPlayer(byte[] audioData, AudioFormat format, int loopCount) {
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

		this.playingThread = new Thread(this, "SoundPlayerThread");
	}

	@Override
	public AudioInputStream getAudioStream() {
		return this.audioStream;
	}

	/**
	 * Does the output work. At the and calls listener.
	 */
	@Override
	public void run() {
		int pos = 0;
		while (!this.shouldStop && !Thread.interrupted() && this.remainingLoopCount != 0 && !this.stream.isClosed()) {
			try {
				if (!this.stream.getQueue().offer(Byte.toUnsignedInt(this.data[pos]), 100,
						TimeUnit.MILLISECONDS)) {
					continue;
				}
				pos++;
				if (pos >= this.data.length) {
					pos = 0;
					if (this.remainingLoopCount != -1) {
						this.remainingLoopCount--;
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		this.stream.setAutoEnding(true);
		runListener();
	}

	private void runListener() {
		if (this.listener != null) {
			if (this.remainingLoopCount == 0) {
				this.listener.accept(StopReason.END_OF_AUDIO);
			} else if (this.stream.isClosed()) {
				this.listener.accept(StopReason.STREAM_CLOSED);
			} else if (this.shouldStop) {
				this.listener.accept(StopReason.PLAYER_STOPPED);
			} else {
				this.listener.accept(StopReason.OTHER);
			}
		}
	}

	@Override
	public void start() {
		this.playingThread.start();
	}

	@Override
	public void stop() {
		this.shouldStop = true;
		this.playingThread.interrupt();
	}

	@Override
	public boolean isRunning() {
		return this.playingThread.isAlive();
	}

	@Override
	public void setOnStopHook(Consumer<StopReason> callback) {
		if (this.playingThread.getState() != State.NEW)
			throw new IllegalStateException("Player is already started");

		this.listener = callback;
	}
}
