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
import static uk.org.lidalia.slf4jtest.LoggingEvent.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	private TestInputHandler handler;

	/**
	 * creates data to insert into the test Cases
	 */
	@BeforeEach
	void setUp() {
		this.aui = new AudioUserInteraction();
		this.aui.setVoiceOutput(false);
		this.mainGrammar = new Grammar(this.mainGram,
				new File(new File(this.folder, this.grammarDir), this.mainGram + ".gram"));
		this.addGrammar = new Grammar(this.addGram,
				new File(new File(this.folder, this.grammarDir), this.addGram + ".gram"));
		this.handler = new TestInputHandler();
		this.switchGrammar = new ArrayList<>();
		this.mainGrammar.putChangeGrammar("change true", this.addGrammar);
		this.switchGrammar.add(this.addGrammar);
	}

	/**
	 * test the various methods which set or get Data
	 */
	@Test
	void testGetterSetter() {
		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(false)));
		assertThat(this.aui.getCurrentRecognizer(), equalTo(null));
		assertThat(this.aui.getMainGrammar(), equalTo(null));
		assertThat(this.aui.getSwitchableGrammars(), equalTo(null));

		this.aui.setSpeechInputHandler(this.handler);
		this.aui.setAudioInputStream(null);

		this.aui.setRecognitionThreadRunning(true);
		this.aui.setGrammars(this.mainGrammar, null);

		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(true)));
		assertThat(this.aui.getCurrentRecognizer(), equalTo(null));
		assertThat(this.aui.getMainGrammar(), equalTo(this.mainGrammar));
		assertThat(this.aui.getSwitchableGrammars(), equalTo(null));

		this.aui.setGrammars(this.mainGrammar, this.switchGrammar);

		assertThat(this.aui.getMainGrammar(), equalTo(this.mainGrammar));
		assertThat(this.aui.getSwitchableGrammars(), equalTo(this.switchGrammar));
	}

	/**
	 * Tests the semantic Components of the AudioUserInteraction
	 */
	@Test
	void testRun() {
		TestLogger logger = TestLoggerFactory.getTestLogger(AudioUserInteraction.class);
		
		//SetUp
		this.aui.setGrammars(this.mainGrammar, this.switchGrammar);
		this.aui.setRecognitionThreadRunning(false);

		/*
		 * test Grammar switch
		 */
		//check that no thread is running
		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(false)));

		//Test switching to no new grammar (ending recognition)
		this.aui.switchGrammar(null);
		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(false)));
//		assertThat(logger.getLoggingEvents(),
//				contains(info("all Recognition stopped")));

		//Test switching to the mainGrammar
		this.aui.switchGrammar(this.mainGrammar);
		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(true)));
//		assertThat(logger.getLoggingEvents(),
//				contains(info("Recognizer {} started", this.mainGrammar.getName())));
		
		//setup
		this.aui.setRecognitionThreadRunning(false);
		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(false)));

		//Test switching to an additional grammar
		this.aui.switchGrammar(this.addGrammar);
		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(true)));
//		assertThat(logger.getLoggingEvents(),
//				contains(info("Recognizer {} started", this.addGrammar.getName())));
		
		/*
		 * test run()
		 */
		//test trying to start without enough input
		this.aui.run();
		System.out.println(logger.getLoggingEvents().toString());
//		assertThat(logger.getLoggingEvents(),
//				contains(error("Not enough Data to start the Recognition - InputHandler not Set")));
	}

	private class TestInputHandler implements SpeechInputHandler {

		/**
		 * 
		 */
		public TestInputHandler() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler#handle(java.lang.String)
		 */
		@Override
		public Future<String> handle(String speechInput) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
