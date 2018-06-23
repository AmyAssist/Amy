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

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.junit.jupiter.api.Test;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Test Class for the Text To Speech Output class
 * 
 * @author Kai Menzel
 */
class TextToSpeechTest {

	@SuppressWarnings("javadoc")
	TextToSpeech tts;

	@SuppressWarnings("boxing")
	private LineListener listener = event -> {
		if (event.getType() == LineEvent.Type.STOP) {
			assertThat(TextToSpeechTest.this.tts.getOutputClip().isActive(), equalTo(false));
			((Clip) event.getSource()).close();
			assertThat(TextToSpeechTest.this.tts.getOutputClip().isOpen(), equalTo(false));
		}
		if (event.getType() == LineEvent.Type.OPEN) {
			assertThat(TextToSpeechTest.this.tts.getOutputClip().isOpen(), equalTo(true));
		}
		if (event.getType() == LineEvent.Type.START) {
			assertThat(TextToSpeechTest.this.tts.getOutputClip().isActive(), equalTo(true));
		}
		if (event.getType() == LineEvent.Type.CLOSE) {
			assertThat(TextToSpeechTest.this.tts.getOutputClip().isOpen(), equalTo(false));
		}
	};

	@SuppressWarnings("boxing")
	@Test
	void test() {
		this.tts = TextToSpeech.getTTS();
//		TestLogger logger = TestLoggerFactory.getTestLogger(TextToSpeech.class);
		assertThat(this.tts == null, equalTo(false));
		this.tts.log("hello");
//		assertThat(logger.getLoggingEvents(), contains(info("hello")));
		// this.tts.say(this.listener, "hello");
//		assertThat(logger.getLoggingEvents(), contains(info("hello")));
	}

	@SuppressWarnings("boxing")
	@Test
	void testWithBreak() {
		this.tts = TextToSpeech.getTTS();
		assertThat(this.tts == null, equalTo(false));
		// this.tts.say(this.listener, "hello world");
		this.tts.stopOutput();
	}

}
