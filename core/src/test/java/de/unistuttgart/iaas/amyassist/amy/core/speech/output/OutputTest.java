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

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.UUID;

import javax.sound.sampled.AudioInputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager;
import de.unistuttgart.iaas.amyassist.amy.core.audio.AudioManager.OutputBehavior;
import de.unistuttgart.iaas.amyassist.amy.core.audio.LocalAudio;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.Sound;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.SoundFactory;
import de.unistuttgart.iaas.amyassist.amy.core.audio.sound.SoundPlayer;
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
	private SoundFactory sf;
	private UUID uuid = new UUID(1, 1);

	private AudioInputStream mockedAis;

	/**
	 * @throws java.lang.Exception
	 *             When an error occurs
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.tts = this.framework.mockService(TextToSpeech.class);
		this.am = this.framework.mockService(AudioManager.class);
		this.la = this.framework.mockService(LocalAudio.class);
		this.sf = this.framework.mockService(SoundFactory.class);

		this.mockedAis = Mockito.mock(AudioInputStream.class);

		when(this.la.isLocalAudioAvailable()).thenReturn(true);
		when(this.la.getLocalAudioEnvironmentIdentifier()).thenReturn(this.uuid);
		when(this.tts.getMaryAudio("Test")).thenReturn(this.mockedAis);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.speech.output.OutputImpl#voiceOutput(java.lang.String)}.
	 * 
	 * @throws IOException
	 *             When an io error occurs
	 */
	@SuppressWarnings("resource")
	@Test
	void testVoiceOutput() throws IOException {
		AudioInputStream anotherMockedAis = Mockito.mock(AudioInputStream.class);
		Sound s = Mockito.mock(Sound.class);
		SoundPlayer sp = Mockito.mock(SoundPlayer.class);
		when(sp.getAudioStream()).thenReturn(anotherMockedAis);
		when(s.getSinglePlayer()).thenReturn(sp);
		when(this.sf.loadSound(this.mockedAis)).thenReturn(s);

		this.output = this.framework.setServiceUnderTest(OutputImpl.class);

		this.output.voiceOutput("Test");
		verify(this.am).playAudio(this.uuid, anotherMockedAis, OutputBehavior.QUEUE);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.speech.output.OutputImpl#soundOutput(de.unistuttgart.iaas.amyassist.amy.core.speech.data.Sounds)}.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testSoundOutput() throws Exception {
		@SuppressWarnings("resource")
		AudioInputStream anotherMockedAis = Mockito.mock(AudioInputStream.class);
		Sound s = Mockito.mock(Sound.class);
		SoundPlayer sp = Mockito.mock(SoundPlayer.class);
		when(sp.getAudioStream()).thenReturn(anotherMockedAis);
		when(s.getSinglePlayer()).thenReturn(sp);
		when(this.sf.loadSound(Sounds.SINGLE_CALL_START_BEEP.getUrl())).thenReturn(s);

		this.output = this.framework.setServiceUnderTest(OutputImpl.class);

		this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
		verify(this.am).playAudio(this.uuid, anotherMockedAis, OutputBehavior.QUEUE);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.speech.output.OutputImpl#stopOutput()}.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testStopOutput() throws Exception {

		Sound s1 = Mockito.mock(Sound.class);
		SoundPlayer sp1 = Mockito.mock(SoundPlayer.class);
		when(sp1.getAudioStream()).thenReturn(this.mockedAis);
		when(s1.getSinglePlayer()).thenReturn(sp1);
		when(this.sf.loadSound(this.mockedAis)).thenReturn(s1);

		Sound s2 = Mockito.mock(Sound.class);
		SoundPlayer sp2 = Mockito.mock(SoundPlayer.class);
		when(sp2.getAudioStream()).thenReturn(this.mockedAis);
		when(s2.getSinglePlayer()).thenReturn(sp2);
		when(this.sf.loadSound(Sounds.SINGLE_CALL_START_BEEP.getUrl())).thenReturn(s2);

		Sound s3 = Mockito.mock(Sound.class);
		SoundPlayer sp3 = Mockito.mock(SoundPlayer.class);
		when(sp3.getAudioStream()).thenReturn(this.mockedAis);
		when(s3.getSinglePlayer()).thenReturn(sp3);
		when(this.sf.loadSound(Sounds.SINGLE_CALL_STOP_BEEP.getUrl())).thenReturn(s3);

		this.output = this.framework.setServiceUnderTest(OutputImpl.class);

		this.output.voiceOutput("Test");
		this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
		this.output.soundOutput(Sounds.SINGLE_CALL_STOP_BEEP);
		this.output.stopOutput();
		verify(sp1).stop();
		verify(sp2).stop();
		verify(sp3).stop();
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.core.speech.output.OutputImpl#isCurrentlyOutputting()}.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testIsCurrentlyOutputting() throws Exception {
		Sound s1 = Mockito.mock(Sound.class);
		SoundPlayer sp1 = Mockito.mock(SoundPlayer.class);
		when(sp1.getAudioStream()).thenReturn(this.mockedAis);
		when(sp1.isRunning()).thenReturn(true);
		when(s1.getSinglePlayer()).thenReturn(sp1);
		when(this.sf.loadSound(this.mockedAis)).thenReturn(s1);

		Sound s2 = Mockito.mock(Sound.class);
		SoundPlayer sp2 = Mockito.mock(SoundPlayer.class);
		when(sp2.getAudioStream()).thenReturn(this.mockedAis);
		when(sp2.isRunning()).thenReturn(false);
		when(s2.getSinglePlayer()).thenReturn(sp2);
		when(this.sf.loadSound(Sounds.SINGLE_CALL_START_BEEP.getUrl())).thenReturn(s2);

		Sound s3 = Mockito.mock(Sound.class);
		SoundPlayer sp3 = Mockito.mock(SoundPlayer.class);
		when(sp3.getAudioStream()).thenReturn(this.mockedAis);
		when(sp3.isRunning()).thenReturn(false);
		when(s3.getSinglePlayer()).thenReturn(sp3);
		when(this.sf.loadSound(Sounds.SINGLE_CALL_STOP_BEEP.getUrl())).thenReturn(s3);

		this.output = this.framework.setServiceUnderTest(OutputImpl.class);

		this.output.voiceOutput("Test");
		this.output.soundOutput(Sounds.SINGLE_CALL_START_BEEP);
		this.output.soundOutput(Sounds.SINGLE_CALL_STOP_BEEP);
		Assertions.assertTrue(this.output.isCurrentlyOutputting());
		verify(sp1).isRunning();

	}
}
