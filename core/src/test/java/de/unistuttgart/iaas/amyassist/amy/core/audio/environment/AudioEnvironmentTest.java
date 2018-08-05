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

import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 *             When anything goes wrong
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

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
	 * @throws java.lang.Exception
	 *             When anything goes wrong
	 */
	@AfterEach
	void tearDown() throws Exception {
		this.ae.stop();
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

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#playAudio(de.unistuttgart.iaas.amyassist.amy.core.audio.AudioOutput, de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager.OutputBehavior)}.
	 * with when audio being played and the behavior is queue.
	 * 
	 * @throws InterruptedException
	 *             When Test get's interrupted
	 */
	@SuppressWarnings("resource")
	@Test
	void testPlayAudioQueue() throws InterruptedException {
		QueuedInputStream qis1 = new QueuedInputStream();
		QueuedInputStream qis2 = new QueuedInputStream();
		AudioOutput ao1 = new AudioOutput(new AudioInputStream(qis1, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		AudioOutput ao2 = new AudioOutput(new AudioInputStream(qis2, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		this.ae.playAudio(ao1, AudioManager.OutputBehavior.QUEUE);

		byte[] bytes1 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes2 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes3 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes4 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes5 = new byte[50 * getAudioFormat().getFrameSize()];

		this.rng.nextBytes(bytes1);
		this.rng.nextBytes(bytes2);
		this.rng.nextBytes(bytes3);
		this.rng.nextBytes(bytes4);
		this.rng.nextBytes(bytes5);

		putBytesIntoQis(qis1, bytes1, false);

		this.ae.playAudio(ao2, AudioManager.OutputBehavior.QUEUE);

		putBytesIntoQis(qis1, bytes2, false);

		putBytesIntoQis(qis2, bytes4, false);

		putBytesIntoQis(qis1, bytes3, true);

		putBytesIntoQis(qis2, bytes5, true);

		byte[] bytes = ArrayUtils.addAll(bytes1, bytes2);
		bytes = ArrayUtils.addAll(bytes, bytes3);
		bytes = ArrayUtils.addAll(bytes, bytes4);
		bytes = ArrayUtils.addAll(bytes, bytes5);

		for (int i = 0; i < bytes.length; i++) {
			Byte b = this.oq.take();
			byte bR = b.byteValue();
			Assertions.assertEquals(bytes[i], bR, "Wrong byte at index " + i);
		}

		Thread.sleep(10);

		Assertions.assertTrue(qis1.isClosed(), "Input stream 1 was not closed.");
		Assertions.assertTrue(qis2.isClosed(), "Input stream 2 was not closed.");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#playAudio(de.unistuttgart.iaas.amyassist.amy.core.audio.AudioOutput, de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager.OutputBehavior)}.
	 * with when audio being played and audio is in the queue and the behavior is queue.
	 * 
	 * @throws InterruptedException
	 *             When Test get's interrupted
	 */
	@SuppressWarnings("resource")
	@Test
	void testPlayAudioQueue2() throws InterruptedException {
		QueuedInputStream qis1 = new QueuedInputStream();
		QueuedInputStream qis2 = new QueuedInputStream();
		QueuedInputStream qis3 = new QueuedInputStream();
		AudioOutput ao1 = new AudioOutput(new AudioInputStream(qis1, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		AudioOutput ao2 = new AudioOutput(new AudioInputStream(qis2, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		AudioOutput ao3 = new AudioOutput(new AudioInputStream(qis3, getAudioFormat(), AudioSystem.NOT_SPECIFIED));

		byte[] bytes1 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes2 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes3 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes4 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes5 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes6 = new byte[50 * getAudioFormat().getFrameSize()];

		this.rng.nextBytes(bytes1);
		this.rng.nextBytes(bytes2);
		this.rng.nextBytes(bytes3);
		this.rng.nextBytes(bytes4);
		this.rng.nextBytes(bytes5);
		this.rng.nextBytes(bytes6);

		putBytesIntoQis(qis1, bytes1, false);
		putBytesIntoQis(qis1, bytes2, true);
		putBytesIntoQis(qis2, bytes3, false);
		putBytesIntoQis(qis2, bytes4, true);
		putBytesIntoQis(qis3, bytes5, false);
		putBytesIntoQis(qis3, bytes6, true);

		this.ae.playAudio(ao1, AudioManager.OutputBehavior.QUEUE);
		this.ae.playAudio(ao2, AudioManager.OutputBehavior.QUEUE);
		this.ae.playAudio(ao3, AudioManager.OutputBehavior.QUEUE);

		byte[] bytes = ArrayUtils.addAll(bytes1, bytes2);
		bytes = ArrayUtils.addAll(bytes, bytes3);
		bytes = ArrayUtils.addAll(bytes, bytes4);
		bytes = ArrayUtils.addAll(bytes, bytes5);
		bytes = ArrayUtils.addAll(bytes, bytes6);

		for (int i = 0; i < bytes.length; i++) {
			Byte b = this.oq.take();
			byte bR = b.byteValue();
			Assertions.assertEquals(bytes[i], bR, "Wrong byte at index " + i);
		}

		Thread.sleep(10);

		Assertions.assertTrue(qis1.isClosed(), "Input stream 1 was not closed.");
		Assertions.assertTrue(qis2.isClosed(), "Input stream 2 was not closed.");
		Assertions.assertTrue(qis3.isClosed(), "Input stream 3 was not closed.");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.audio.environment.AudioEnvironment#playAudio(de.unistuttgart.iaas.amyassist.amy.core.audio.AudioOutput, de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager.OutputBehavior)}.
	 * with when audio being played and audio is in the queue and the behavior is queue_priority.
	 * 
	 * @throws InterruptedException
	 *             When Test get's interrupted
	 */
	@SuppressWarnings("resource")
	@Test
	void testPlayAudioPrioQueue() throws InterruptedException {
		QueuedInputStream qis1 = new QueuedInputStream();
		QueuedInputStream qis2 = new QueuedInputStream();
		QueuedInputStream qis3 = new QueuedInputStream();
		AudioOutput ao1 = new AudioOutput(new AudioInputStream(qis1, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		AudioOutput ao2 = new AudioOutput(new AudioInputStream(qis2, getAudioFormat(), AudioSystem.NOT_SPECIFIED));
		AudioOutput ao3 = new AudioOutput(new AudioInputStream(qis3, getAudioFormat(), AudioSystem.NOT_SPECIFIED));

		byte[] bytes1 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes2 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes3 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes4 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes5 = new byte[50 * getAudioFormat().getFrameSize()];
		byte[] bytes6 = new byte[50 * getAudioFormat().getFrameSize()];

		this.rng.nextBytes(bytes1);
		this.rng.nextBytes(bytes2);
		this.rng.nextBytes(bytes3);
		this.rng.nextBytes(bytes4);
		this.rng.nextBytes(bytes5);
		this.rng.nextBytes(bytes6);

		this.ae.playAudio(ao1, AudioManager.OutputBehavior.QUEUE);
		this.ae.playAudio(ao2, AudioManager.OutputBehavior.QUEUE);

		Thread.sleep(10); // Needed, so that ao3 is not added before ao1 has started to play.

		putBytesIntoQis(qis1, bytes1, false);

		this.ae.playAudio(ao3, AudioManager.OutputBehavior.QUEUE_PRIORITY);

		putBytesIntoQis(qis1, bytes2, true);
		putBytesIntoQis(qis3, bytes3, false);
		putBytesIntoQis(qis3, bytes4, true);
		putBytesIntoQis(qis2, bytes5, false);
		putBytesIntoQis(qis2, bytes6, true);

		byte[] bytes = ArrayUtils.addAll(bytes1, bytes2);
		bytes = ArrayUtils.addAll(bytes, bytes3);
		bytes = ArrayUtils.addAll(bytes, bytes4);
		bytes = ArrayUtils.addAll(bytes, bytes5);
		bytes = ArrayUtils.addAll(bytes, bytes6);

		for (int i = 0; i < bytes.length; i++) {
			Byte b = this.oq.take();
			byte bR = b.byteValue();
			Assertions.assertEquals(bytes[i], bR, "Wrong byte at index " + i);
		}

		Thread.sleep(10);

		Assertions.assertTrue(qis1.isClosed(), "Input stream 1 was not closed.");
		Assertions.assertTrue(qis2.isClosed(), "Input stream 2 was not closed.");
		Assertions.assertTrue(qis3.isClosed(), "Input stream 3 was not closed.");
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
