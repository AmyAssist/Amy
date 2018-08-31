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

package de.unistuttgart.iaas.amyassist.amy.core.speech.output;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import javax.sound.sampled.AudioInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager.OutputBehavior;
import de.unistuttgart.iaas.amyassist.amy.core.audio.LocalAudio;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds;
import de.unistuttgart.iaas.amyassist.amy.core.speech.tts.TextToSpeech;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Tests {@link Output}
 * 
 * @author Tim Neumann
 */
@ExtendWith(FrameworkExtension.class)
class OutputTest {

	@Reference
	private TestFramework framework;

	private AudioManager am;
	private TextToSpeech tts;
	private LocalAudio la;
	private Output output;
	private UUID uuid = new UUID(1, 1);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.tts = this.framework.mockService(TextToSpeech.class);
		this.am = this.framework.mockService(AudioManager.class);
		this.la = this.framework.mockService(LocalAudio.class);

		AudioInputStream ais = mock(AudioInputStream.class);

		when(this.la.isLocalAudioAvailable()).thenReturn(true);
		when(this.la.getLocalAudioEnvironmentIdentifier()).thenReturn(this.uuid);
		when(this.tts.getMaryAudio("Test")).thenReturn(ais);

		this.output = this.framework.setServiceUnderTest(OutputImpl.class);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.speech.output.OutputImpl#voiceOutput(java.lang.String)}.
	 */
	@Test
	void testVoiceOutput() {
		this.output.voiceOutput("Test");
		verify(this.am).playAudio(eq(this.uuid), any(), eq(OutputBehavior.QUEUE));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.speech.output.OutputImpl#soundOutput(de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds)}.
	 */
	@Test
	void testSoundOutput() {
		this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
		verify(this.am).playAudio(eq(this.uuid), any(), eq(OutputBehavior.QUEUE));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.speech.output.OutputImpl#stopOutput()}.
	 */
	@Test
	void testStopOutput() {
		this.output.stopOutput();
		verify(this.am).stopAudioOutput(this.uuid);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.speech.output.OutputImpl#isCurrentlyOutputting()}.
	 */
	@Test
	void testIsCurrentlyOutputting() {
		this.output.isCurrentlyOutputting();
		verify(this.am).isAudioEnvironmentCurrentlyOutputting(this.uuid);
	}

}
