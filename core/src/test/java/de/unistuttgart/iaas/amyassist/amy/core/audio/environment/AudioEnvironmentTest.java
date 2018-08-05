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

package de.unistuttgart.iaas.amyassist.amy.core.audio.environment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioOutput;
import de.unistuttgart.iaas.amyassist.amy.core.audio.QueuedInputStream;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests the audio environment.
 * 
 * @author Tim Neumann
 */
class AudioEnvironmentTest {
	private AudioEnvironment ae;
	private BlockingQueue<Integer> iq;
	private BlockingQueue<Byte> oq;
	private Random rng;
	private Logger logger = TestLoggerFactory.getTestLogger(AudioEnvironmentTest.class);

	/**
	 * @throws java.lang.Exception
	 *             When anything goes wrong
	 */
	@BeforeEach
	void setUp() throws Exception {
		long seed = (long) (Math.random() * Integer.MAX_VALUE);
		this.logger.info("Seed: {}", seed);
		this.rng = new Random(seed);
		this.iq = new LinkedBlockingQueue<>();
		this.oq = new LinkedBlockingQueue<>();
		this.ae = new TestingEnvironment(getAudioFormat(), this.iq, this.oq);
		this.ae.start();
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#stopOutput()}.
	 * 
	 * @throws InterruptedException
	 *             When the test is interrupted
	 */
	@SuppressWarnings("resource")
	@Test
	void testStopOutput() throws InterruptedException {
		QueuedInputStream qis = new QueuedInputStream();
		AudioOutput ao = new AudioOutput(new AudioInputStream(qis, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		this.ae.playAudio(ao, AudioManager.OutputBehavior.QUEUE);

		byte[] bytes = new byte[2000 * getAudioFormat().getFrameSize()];
		byte[] bytesA = new byte[2000 * getAudioFormat().getFrameSize()];

		this.rng.nextBytes(bytes);
		this.rng.nextBytes(bytesA);

		putBytesIntoQis(qis, bytes, false);

		Thread.sleep(10);

		this.ae.stopOutput();

		Thread.sleep(10);

		putBytesIntoQis(qis, bytesA, true);

		for (int i = 0; i < bytes.length; i++) {
			Byte b = this.oq.poll(20, TimeUnit.MILLISECONDS);
			if (b == null) {
				assertThat("Stopped to late", i, greaterThan(bytes.length - EnvironmentOutputWorker.BYTE_BUFFER_SIZE));
				if (i < bytes.length - EnvironmentOutputWorker.BYTE_BUFFER_SIZE) {
					Assertions.fail("Stopped to early");
				}
			} else {
				byte bR = b.byteValue();
				Assertions.assertEquals(bytes[i], bR, "Wrong byte at index " + i);
			}
		}

		assertThat("Stopped to late", this.oq.size(), lessThan(EnvironmentOutputWorker.BYTE_BUFFER_SIZE));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#playAudio(de.unistuttgart.iaas.amyassist.amy.core.audio.AudioOutput, de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager.OutputBehavior)}.
	 * with no previous audio being played.
	 * 
	 * @throws InterruptedException
	 *             When Test get's interrupted
	 */
	@Test
	@SuppressWarnings("resource")
	void testPlayAudioNoPrev() throws InterruptedException {
		QueuedInputStream qis = new QueuedInputStream();
		AudioOutput ao = new AudioOutput(new AudioInputStream(qis, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		this.ae.playAudio(ao, AudioManager.OutputBehavior.QUEUE);

		byte[] bytes = new byte[1000 * getAudioFormat().getFrameSize()];

		this.rng.nextBytes(bytes);

		putBytesIntoQis(qis, bytes, true);

		for (int i = 0; i < bytes.length; i++) {
			Byte b = this.oq.take();
			byte bR = b.byteValue();
			Assertions.assertEquals(bytes[i], bR, "Wrong byte at index " + i);
		}

		Thread.sleep(10);

		Assertions.assertTrue(qis.isClosed(), "Input stream was not closed.");
	}

	private void putBytesIntoQis(QueuedInputStream qis, byte[] bytes, boolean endStream) {
		for (int i = 0; i < bytes.length; i++) {
			tryToPutToQueue(Byte.toUnsignedInt(bytes[i]), qis);
		}
		if (endStream) {
			tryToPutToQueue(-1, qis);
		}
	}

	private void tryToPutToQueue(int value, QueuedInputStream qis) {

		try {
			qis.getQueue().put(value);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#getAudioInputStream()}.
	 */
	@Test
	void testGetAudioInputStream() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#isCurrentlyOutputting()}.
	 */
	@SuppressWarnings("resource")
	@Test
	void testIsCurrentlyOutputting() {
		QueuedInputStream qis = new QueuedInputStream();
		AudioOutput ao = new AudioOutput(new AudioInputStream(qis, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		this.ae.playAudio(ao, AudioManager.OutputBehavior.QUEUE);

		byte[] bytes1 = new byte[100 * getAudioFormat().getFrameSize()];
		byte[] bytes2 = new byte[100 * getAudioFormat().getFrameSize()];

		this.rng.nextBytes(bytes1);
		this.rng.nextBytes(bytes2);

		putBytesIntoQis(qis, bytes1, false);

		Assertions.assertTrue(this.ae.isCurrentlyOutputting(), "Should be outputting");

		putBytesIntoQis(qis, bytes2, true);

	}

	private AudioFormat getAudioFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
}
