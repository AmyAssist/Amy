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

package de.unistuttgart.iaas.amyassist.amy.core.speech.manager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.Sound;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.speech.LocalSpeechInterpreter;
import de.unistuttgart.iaas.amyassist.amy.core.speech.LocalSpeechInterpreter.ListeningState;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechRecognizer;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SphinxSpeechRecognizer;
import de.unistuttgart.iaas.amyassist.amy.core.speech.output.Output;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Tests the Speech Recognizer Management system
 * 
 * @author Kai Menzel
 */
@ExtendWith(FrameworkExtension.class)
public class SpeechManagerTest {

	@Reference
	private TestFramework testFramework;

	private LocalSpeechInterpreter manager;
	
	private SphinxSpeechRecognizer recognizer;

	@BeforeEach
	private void preCondition() {
		Mockito.mock(Output.class);
		Mockito.mock(MessageHub.class);
//		Output output = this.testFramework.setServiceUnderTest(Output.class);
//		MessageHub messageHub = this.testFramework.mockService(MessageHub.class);
		this.recognizer  = this.testFramework.mockService(SphinxSpeechRecognizer.class);
		Mockito.mock(Sound.class);

		this.manager = new LocalSpeechInterpreter();
	}

	@Test
	private void testListeningState() {

		assertThat(this.manager.getCurrentListeningState(), equalTo(ListeningState.NOT_LISTENING));

		this.manager.setListeningState(ListeningState.MULTI_CALL_LISTENING);
		assertThat(this.manager.getCurrentListeningState(), equalTo(ListeningState.MULTI_CALL_LISTENING));

		this.manager.setListeningState(ListeningState.SINGLE_CALL_LISTENING);
		assertThat(this.manager.getCurrentListeningState(), equalTo(ListeningState.SINGLE_CALL_LISTENING));

		this.manager.setListeningState(ListeningState.NOT_LISTENING);
		assertThat(this.manager.getCurrentListeningState(), equalTo(ListeningState.NOT_LISTENING));
	}

	@Test
	private void testCurrentRecognizer() {
		assertNull(this.manager.getCurrentRecognizer());

		this.manager.setCurrentRecognizer(null);
		this.manager.nextRecognitionRequest();
		assertNull(this.manager.getCurrentRecognizer());

		this.manager.setCurrentRecognizer(this.recognizer);
		assertThat(this.manager.getCurrentRecognizer(), equalTo(this.recognizer));

	}

}
