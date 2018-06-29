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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.org.lidalia.slf4jtest.LoggingEvent.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Test Class for the AudioUserIneraction
 * 
 * @author Kai Menzel
 */
class AudioUserInteractionTest {

	private String folder = "src/test/resources/de/unistuttgart/iaas/amyassist/amy/core/speech";

	private String grammarDir = "grammars";
	private String mainGram = "mainGrammar";
	private String addGram = "addGrammar";

	private AudioUserInteraction aui;
	private Grammar mainGrammar;
	private Grammar addGrammar;
	private List<Grammar> switchGrammar;
	private SpeechInputHandler handler;

	private AudioInputStream ais;

	/**
	 * creates data to insert into the test Cases
	 */
	@BeforeEach
	void setUp() {
		this.ais = Mockito.mock(AudioInputStream.class);
		this.aui = new AudioUserInteraction();
		this.aui.setVoiceOutput(false);
		this.mainGrammar = new Grammar(this.mainGram,
				new File(new File(this.folder, this.grammarDir), this.mainGram + ".gram"));
		this.addGrammar = new Grammar(this.addGram,
				new File(new File(this.folder, this.grammarDir), this.addGram + ".gram"));
		this.handler = Mockito.mock(SpeechInputHandler.class);
		this.switchGrammar = new ArrayList<>();
		this.mainGrammar.putChangeGrammar("change true", this.addGrammar);
		this.switchGrammar.add(this.addGrammar);
	}

	/**
	 * test the various methods which set or get Data
	 */
	@Test
	void testGetterSetter() {
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(false));
		assertThat(this.aui.getCurrentRecognizer(), equalTo(null));
		assertThat(this.aui.getMainGrammar(), equalTo(null));
		assertThat(this.aui.getSwitchableGrammars(), equalTo(null));

		this.aui.setSpeechInputHandler(this.handler);
		this.aui.setAudioInputStream(null);

		this.aui.setRecognitionThreadRunning(true);
		this.aui.setGrammars(this.mainGrammar, null);

		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(true));
		assertThat(this.aui.getCurrentRecognizer(), equalTo(null));
		assertThat(this.aui.getMainGrammar(), equalTo(this.mainGrammar));
		assertThat(this.aui.getSwitchableGrammars(), equalTo(null));

		this.aui.setGrammars(this.mainGrammar, this.switchGrammar);

		assertThat(this.aui.getMainGrammar(), equalTo(this.mainGrammar));
		assertThat(this.aui.getSwitchableGrammars(), equalTo(this.switchGrammar));
	}

	/**
	 * Tests the run method of the AudioUserInteraction
	 */
	@Test
	void testRun() {
		TestLogger logger = TestLoggerFactory.getTestLogger(AudioUserInteraction.class);
		TestLogger srLogger = TestLoggerFactory.getTestLogger(SpeechRecognizer.class);

		// SetUp
		this.aui = new AudioUserInteraction();
		this.aui.setAudioInputStream(this.ais);
		this.aui.setVoiceOutput(false);
		this.aui.setGrammars(this.mainGrammar, this.switchGrammar);
		this.aui.setRecognitionThreadRunning(false);

		// test trying to start without enough input
		this.aui.run();
		// assertThat(logger.getLoggingEvents(),
		// contains(error("Not enough Data to start the Recognition - InputHandler not Set")));
		assertNull(this.aui.getCurrentRecognizer());
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(false));

		this.aui.setSpeechInputHandler(this.handler);
		this.aui.run();
		// assertThat(srLogger.getLoggingEvents(), contains(error("StreamRecognizer can't be instantiated")));
		assertNotNull(this.aui.getCurrentRecognizer());
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(true));
	}

	/**
	 * Tests the grammar switch fuction
	 */
	@Test
	void testGrammarSwitch() {

		// SetUp
		this.aui.setGrammars(this.mainGrammar, this.switchGrammar);
		this.aui.setRecognitionThreadRunning(false);

		/*
		 * test Grammar switch
		 */
		// check that no thread is running
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(false));

		// Test switching to no new grammar (ending recognition)
		this.aui.switchGrammar(null);
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(false));
		// assertThat(logger.getLoggingEvents(), contains(info("all Recognition stopped")));

		// Test switching to the mainGrammar
		this.aui.switchGrammar(this.mainGrammar);
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(true));
		// assertThat(logger.getLoggingEvents(),
		// contains(info("Switching to Recognizer {}", this.mainGrammar.getName())));

		// setup
		this.aui.setRecognitionThreadRunning(false);
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(false));

		// Test switching to an additional grammar
		this.aui.switchGrammar(this.addGrammar);
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(true));
		// assertThat(logger.getLoggingEvents(),
		// contains(info("Switching to Recognizer {}", this.addGrammar.getName())));
	}

}
