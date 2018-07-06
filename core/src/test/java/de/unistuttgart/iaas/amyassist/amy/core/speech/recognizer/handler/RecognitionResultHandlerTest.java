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

package de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.LocalSpeechRecognizerManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Class that tests the internal resulthandler
 * 
 * @author Kai Menzel
 */
public class RecognitionResultHandlerTest {

	@Reference
	private TestFramework framework;

	private RecognitionResultHandler resultHandler;

	private SpeechRecognitionResultManager manager;

	private Grammar grammar;

	@BeforeEach
	private void setup() {
		this.manager = Mockito.mock(LocalSpeechRecognizerManager.class);
		this.grammar = Mockito.mock(Grammar.class);

		Mockito.when(this.manager.isRecognitionThreadRunning()).thenReturn(true);
	}

	@Test
	private void test() {
		this.resultHandler = new LocalMainGrammarResultHandler(this.manager, this.grammar);
		assertThat(this.resultHandler.isRecognitionThreadRunning(), equalTo(true));
	}

}
