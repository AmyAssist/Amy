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

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.amyassist.amy.core.audio.sound.SoundPlayer.StopReason;

/**
 * Test class for {@link BasicSoundPlayer}
 * 
 * @author Tim Neumann
 */
class BasicSoundPlayerTest {

	private volatile boolean shouldStop;
	private volatile boolean wasShouldStopWhenHookExecuted;
	private volatile StopReason reason;
	private volatile boolean stopHookExecuted;

	@BeforeEach
	public void init() {
		this.shouldStop = false;
		this.wasShouldStopWhenHookExecuted = false;
		this.stopHookExecuted = false;
		this.reason = null;
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.core.audio.sound.BasicSoundPlayer#getAudioStream()}.
	 * 
	 * @throws InterruptedException
	 *             When the thread gets interrupted
	 * @throws IOException
	 *             When an io error occurs
	 */
	@Test
	void testGetAudioStream() throws InterruptedException, IOException {
		byte[] in = new byte[getFormat().getFrameSize() * 100];
		byte[] out = new byte[in.length];

		fillBuffer(in);

		BasicSoundPlayer player = new BasicSoundPlayer(in, getFormat(), 1);
		try (AudioInputStream ais = player.getAudioStream()) {

			player.start();

			int amount = readAll(out, ais);
			Assertions.assertEquals(in.length, amount, "Wrong number of bytes");
			Assertions.assertArrayEquals(in, out, "Wrong data.");
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.core.audio.sound.BasicSoundPlayer#setOnStopHook(java.util.function.Consumer)}.
	 * for {@link StopReason#END_OF_AUDIO}.
	 * 
	 * @throws InterruptedException
	 *             When the thread gets interrupted
	 * @throws IOException
	 *             When an io error occurs
	 */
	@Test
	void testSetOnStopEndOfAudio() throws InterruptedException, IOException {
		byte[] data = new byte[10020];

		fillBuffer(data);

		BasicSoundPlayer player = new BasicSoundPlayer(data, getFormat(), 1);

		player.setOnStopHook(r -> {
			this.stopHookExecuted = true;
			this.wasShouldStopWhenHookExecuted = this.shouldStop;
			this.reason = r;
		});

		try (AudioInputStream ais = player.getAudioStream()) {

			player.start();

			ais.read(data, 0, 10);
			this.shouldStop = true;
			readAll(data, ais);

			while (player.isRunning()) {
				// this is legitimate, deterministic use of sleep
				Thread.sleep(100); // NOSONAR
			}

			Assertions.assertTrue(this.stopHookExecuted, "Did not run stop hook.");
			Assertions.assertTrue(this.wasShouldStopWhenHookExecuted, "Should not stop yet.");
			Assertions.assertEquals(StopReason.END_OF_AUDIO, this.reason, "Wrong stop reason.");
		}

	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.core.audio.sound.BasicSoundPlayer#setOnStopHook(java.util.function.Consumer)}.
	 * for {@link StopReason#STREAM_CLOSED}.
	 * 
	 * @throws InterruptedException
	 *             When the thread gets interrupted
	 * @throws IOException
	 *             When an io error occurs
	 */
	@Test
	void testSetOnStopStreamClosed() throws InterruptedException, IOException {
		byte[] data = new byte[10020];

		fillBuffer(data);

		BasicSoundPlayer player = new BasicSoundPlayer(data, getFormat(), 1);

		player.setOnStopHook(r -> {
			this.stopHookExecuted = true;
			this.wasShouldStopWhenHookExecuted = this.shouldStop;
			this.reason = r;
		});

		try (AudioInputStream ais = player.getAudioStream()) {

			player.start();

			ais.read(data, 0, 10);
			this.shouldStop = true;
			ais.close();

			while (player.isRunning()) {
				// this is legitimate, deterministic use of sleep
				Thread.sleep(100); // NOSONAR
			}

			Assertions.assertTrue(this.stopHookExecuted, "Did not run stop hook.");
			Assertions.assertTrue(this.wasShouldStopWhenHookExecuted, "Should not stop yet.");
			Assertions.assertEquals(StopReason.STREAM_CLOSED, this.reason, "Wrong stop reason.");
		}

	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.core.audio.sound.BasicSoundPlayer#setOnStopHook(java.util.function.Consumer)}.
	 * for {@link StopReason#PLAYER_STOPPED}.
	 * 
	 * @throws InterruptedException
	 *             When the thread gets interrupted
	 * @throws IOException
	 *             When an io error occurs
	 */
	@Test
	void testSetOnStopPlayerStopped() throws InterruptedException, IOException {
		byte[] data = new byte[10020];

		fillBuffer(data);

		BasicSoundPlayer player = new BasicSoundPlayer(data, getFormat(), 1);

		player.setOnStopHook(r -> {
			this.stopHookExecuted = true;
			this.wasShouldStopWhenHookExecuted = this.shouldStop;
			this.reason = r;
		});

		try (AudioInputStream ais = player.getAudioStream()) {
			player.start();

			ais.read(data, 0, 10);
			this.shouldStop = true;
			player.stop();

			while (player.isRunning()) {
				// this is legitimate, deterministic use of sleep
				Thread.sleep(100); // NOSONAR
			}

			Assertions.assertTrue(this.stopHookExecuted, "Did not run stop hook.");
			Assertions.assertTrue(this.wasShouldStopWhenHookExecuted, "Should not stop yet.");
			Assertions.assertEquals(StopReason.PLAYER_STOPPED, this.reason, "Wrong stop reason.");
		}

	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.core.audio.sound.BasicSoundPlayer#setOnStopHook(java.util.function.Consumer)}.
	 * for {@link StopReason#OTHER}.
	 * 
	 * @throws InterruptedException
	 *             When the thread gets interrupted
	 * @throws IOException
	 *             When an io error occurs
	 */
	@Test
	void testSetOnStopPlayerOther() throws InterruptedException, IOException {
		byte[] data = new byte[10020];

		fillBuffer(data);

		BasicSoundPlayer player = new BasicSoundPlayer(data, getFormat(), 1);

		player.setOnStopHook(r -> {
			this.stopHookExecuted = true;
			this.wasShouldStopWhenHookExecuted = this.shouldStop;
			this.reason = r;
		});

		try (AudioInputStream ais = player.getAudioStream()) {
			player.start();

			ais.read(data, 0, 10);
			this.shouldStop = true;
			Thread[] threads = new Thread[Thread.activeCount()];
			Thread.enumerate(threads);
			for (Thread t : threads) {
				if (t.getName().equals("SoundPlayerThread")) {
					t.interrupt();
				}
			}

			while (player.isRunning()) {
				// this is legitimate, deterministic use of sleep
				Thread.sleep(100); // NOSONAR
			}

			Assertions.assertTrue(this.stopHookExecuted, "Did not run stop hook.");
			Assertions.assertTrue(this.wasShouldStopWhenHookExecuted, "Should not stop yet.");
			Assertions.assertEquals(StopReason.OTHER, this.reason, "Wrong stop reason.");
		}

	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.core.audio.sound.BasicSoundPlayer#setOnStopHook(java.util.function.Consumer)}.
	 * when the player is already started.
	 * 
	 * @throws InterruptedException
	 *             When the thread gets interrupted
	 * @throws IOException
	 *             When an io error occurs
	 */
	@Test
	void testSetOnStopPlayerStarted() throws InterruptedException, IOException {
		byte[] data = new byte[10020];

		fillBuffer(data);

		BasicSoundPlayer player = new BasicSoundPlayer(data, getFormat(), 1);

		player.start();

		Assertions.assertThrows(IllegalStateException.class,
				() -> player.setOnStopHook(r -> this.stopHookExecuted = true));

		try (AudioInputStream ais = player.getAudioStream()) {

			readAll(data, ais);

			while (player.isRunning()) {
				// this is legitimate, deterministic use of sleep
				Thread.sleep(100); // NOSONAR
			}

			Assertions.assertFalse(this.stopHookExecuted, "Stop hook should not be run.");
		}

	}

	private void fillBuffer(byte[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) i;
		}
	}

	private int readAll(byte[] buffer, AudioInputStream stream) throws IOException {
		int pos = 0;
		int read = 0;
		do {
			if (buffer.length - pos == 0 && stream.read(buffer, 0, buffer.length) > 0)
				throw new IllegalStateException("Buffer overfilled");
			read = stream.read(buffer, pos, buffer.length - pos);
			pos += read;
		} while (read > 0);
		return pos + 1;
	}

	private AudioFormat getFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
}
