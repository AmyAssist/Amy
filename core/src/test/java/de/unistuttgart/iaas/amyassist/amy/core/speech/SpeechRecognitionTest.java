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

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static uk.org.lidalia.slf4jtest.LoggingEvent.*;

import javax.sound.sampled.AudioInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * TODO: Description
 * @author
 */
/**
 * Test Class for the Speech Recognition Systems extending the to be tested Class to gain acces to protected Variabels
 * 
 * @author Kai Menzel
 */
public class SpeechRecognitionTest extends SpeechRecognizer {

	private String folder = "src/test/resources/de/unistuttgart/iaas/amyassist/amy/core/speech";

	private String grammarDir = "grammars";
	private String mainGram = "mainGrammar";
	private String addGram = "addGrammar";

	private String change = "change true";
	private String unknown = "unknown String";
	private String unknownError = "unknown Error";
	private String known = "known String";
	private String knownReturn = "return String";

	private Grammar mainGrammar;
	private Grammar addGrammar;

	private SpeechRecognizer recognizer;

	private AudioUserInteraction aui;
	private SpeechInputHandler handler;
	private AudioInputStream ais;

	private TestLogger logger;
	private TestLogger ttsLogger;

	/**
	 * Setting up Data to be used in the tests
	 */
	@BeforeEach
	void setUp() {
		this.logger = TestLoggerFactory.getTestLogger(SpeechRecognizer.class);
		this.ttsLogger = TestLoggerFactory.getTestLogger(TextToSpeech.class);

		this.mainGrammar = new Grammar(this.mainGram,
				new File(new File(this.folder, this.grammarDir), this.mainGram + ".gram"));
		this.addGrammar = new Grammar(this.addGram,
				new File(new File(this.folder, this.grammarDir), this.addGram + ".gram"));
		this.mainGrammar.putChangeGrammar(this.change, this.addGrammar);
		this.aui = Mockito.mock(AudioUserInteraction.class);
		this.handler = Mockito.mock(SpeechInputHandler.class);
		this.ais = Mockito.mock(AudioInputStream.class);

		// SpeechInputHandler return
		CompletableFuture<String> completableFuture;
		// known return of SpeechInputHandler
		completableFuture = new CompletableFuture<>();
		completableFuture.complete(this.knownReturn);
		Mockito.when(this.handler.handle(this.known)).thenReturn(completableFuture);

		// unknown return of SpeechInputHandler
		/*
		 * completableFuture = new CompletableFuture<>(); completableFuture.complete(this.unknown);
		 * Mockito.when(this.handler.handle(???)).thenReturn(completableFuture);
		 */

		// no return of SpeechInputHandler
		Mockito.when(this.handler.handle(this.unknownError)).thenReturn(null);

	}

	/**
	 * test the makeDecision Method
	 */
	@Test
	void testMakeDecision() {
		this.recognizer = new MainSpeechRecognizer(this.aui, this.mainGrammar, this.handler, this.ais, false);
		// assertThat(logger.getLoggingEvents(), contains(error("StreamRecognizer can't be instantiated")));

		// empty input
		this.recognizer.makeDecision(" ");

		// unknown input
		// this.recognizer.makeDecision(this.unknown);
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("saying: {}", "unknown command")));
		// assertThat(this.logger.getLoggingEvents(), contains(warn("unknown command")));

		// known input
		this.recognizer.makeDecision(this.known);
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("saying: {}", this.knownReturn)));

		// change input
		this.recognizer.makeDecision(this.change);
		// assertThat(logger.getLoggingEvents(), contains(info("switching Recognizer...")));
		// assertThat(logger.getLoggingEvents(), contains(info("stop current Recognizer to start the next")));

		// error output
		this.recognizer.makeDecision(this.unknownError);
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("saying: {}", "unknown command")));
		// assertThat(this.logger.getLoggingEvents(), contains(error("no handle return")));

	}

	/**
	 * Test the predefined Handling of the MainRecognizer
	 */
	@Test
	void mainSpecificPredefinedInputHandling() {
		this.recognizer = new MainSpeechRecognizer(this.aui, this.mainGrammar, this.handler, this.ais, false);

		assertThat(new Boolean(Constants.SRisListening), equalTo(new Boolean(false)));
		this.recognizer.speechRecognitionResult = Constants.WAKE_UP;
		this.recognizer.predefinedInputHandling();
		assertThat(new Boolean(Constants.SRisListening), equalTo(new Boolean(true)));
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("waking up")));

		this.recognizer.speechRecognitionResult = Constants.GO_SLEEP;
		this.recognizer.predefinedInputHandling();
		assertThat(new Boolean(Constants.SRisListening), equalTo(new Boolean(false)));
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("now sleeping")));

		this.recognizer.speechRecognitionResult = this.known;
		this.recognizer.predefinedInputHandling();
		// test no log?

		assertThat(new Boolean(Constants.SRisListening), equalTo(new Boolean(false)));
		this.recognizer.speechRecognitionResult = Constants.WAKE_UP;
		this.recognizer.predefinedInputHandling();
		assertThat(new Boolean(Constants.SRisListening), equalTo(new Boolean(true)));
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("waking up")));

		this.recognizer.speechRecognitionResult = this.known;
		this.recognizer.predefinedInputHandling();
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("saying: {}", this.knownReturn)));

	}

	/**
	 * Test the predefined Handling of the AdditionalRecognizer
	 */
	@Test
	void additionalSpecificPredefinedInputHandling() {
		this.recognizer = new AdditionalSpeechRecognizer(this.aui, this.mainGrammar, this.handler, this.ais, false);
		Constants.SRisListening = true;

		this.recognizer.speechRecognitionResult = this.known;
		this.recognizer.predefinedInputHandling();
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("saying: {}", this.knownReturn)));

		this.recognizer.speechRecognitionResult = Constants.GO_SLEEP;
		this.recognizer.predefinedInputHandling();
		assertThat(new Boolean(Constants.SRisListening), equalTo(new Boolean(false)));
		// assertThat(this.ttsLogger.getLoggingEvents(), contains(info("now sleeping")));
		// assertThat(logger.getLoggingEvents(), contains(info("stop current Recognizer to start the next")));
	}

	/**
	 * Tests if the Method that stops the current recognizer without starting a new one is really stopping the current
	 * recognizer
	 */
	@Test
	void testStopAllRecognition() {
		this.recognizer = new MainSpeechRecognizer(this.aui, null, this.handler, this.ais, false);
		this.aui.setRecognitionThreadRunning(true);
		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(true)));
		this.recognizer.stop();
		assertThat(new Boolean(this.aui.isRecognitionThreadRunning()), equalTo(new Boolean(false)));
	}

	/**
	 * Test that no error is thrown if Recognizer without Grammar is started
	 */
	@Test
	void testNoGrammarConfiguration() {
		this.recognizer = new MainSpeechRecognizer(this.aui, null, this.handler, this.ais, false);
	}

	// ======================================================================
	// Setup Override
	/**
	 * @Override extending the to be tested Class to gain acces to protected Variabels
	 */
	public SpeechRecognitionTest() {
		super(null, null, null, null, false);
	}

	/**
	 * @Override extending the to be tested Class to gain acces to protected Variabels
	 */
	@Override
	protected void predefinedInputHandling() {
	}

}
