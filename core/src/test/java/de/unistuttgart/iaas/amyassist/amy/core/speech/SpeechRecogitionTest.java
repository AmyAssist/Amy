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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.spi.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.Module.SetupContext;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Test Class for the SpeechRecognition System
 * 
 * @author Kai Menzel
 */
class SpeechRecogitionTest {
	
	private static SpeechRecogitionTest test;
	
	private String folder = "src/test/resources/de/unistuttgart/iaas/amyassist/amy/core/speech";

	private String grammarDir = "grammars";
	private String mainGram = "mainGrammar";
	private String addGram = "addGrammar";
	
	private String streamDir = "streams";
	private String mainStream = "mainStream.wav";
	
	private AudioUserInteraction aui;
	private Grammar mainGrammar;
	private Grammar addGrammar;
	private List<Grammar> switchGrammar;
	private TestInputHandler handler;
	
	private boolean soundPlaying = false;
	
	public static void main(String[] args) {
		test = new SpeechRecogitionTest();
		test.setUp();
		test.overallTest();
	}
	
	@BeforeEach
	void setUp() {
		this.aui = new AudioUserInteraction();
		this.aui.setVoiceOutput(false);
		this.mainGrammar = new Grammar(this.mainGram, new File(new File(this.folder, this.grammarDir), this.mainGram + ".gram"));
		this.addGrammar = new Grammar(this.addGram, new File(new File(this.folder, this.grammarDir), this.addGram + ".gram"));
		this.handler = new TestInputHandler(this);
		this.switchGrammar = new ArrayList<>();
		this.mainGrammar.putChangeGrammar("change true", this.addGrammar);
		this.switchGrammar.add(this.addGrammar);
	}
	
	@SuppressWarnings("boxing")
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

	void overallTest() {
		this.aui.setSpeechInputHandler(this.handler);
		this.aui.setGrammars(this.mainGrammar, this.switchGrammar);
		
		this.handler.addCommand("hello world");
		this.handler.addCommand("change true");
		this.handler.addCommand("hello world is dead");
		this.handler.addCommand("hello world");
		
//		assertThat(this.handler.i, equalTo(0));
		System.out.println(0 + "::" + (this.handler.i == 0));
		
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(new File(this.folder, this.streamDir), this.mainStream));
//			InputStream ais = SpeechRecogitionTest.class.getResourceAsStream(this.folder + this.streamDir + this.mainStream);//new File(new File(this.folder, this.streamDir), this.mainStream));
			ais.skip(44);
			this.aui.setAudioInputStream(ais);
			/*
			AudioFormat format = ais.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip;
			try {
				clip = (Clip) AudioSystem.getLine(info);
				clip.addLineListener(this.listener);
				clip.open(ais);
				clip.start();
				this.soundPlaying = true;
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			while(soundPlaying) {
				Thread.yield();
			}
			*/
			this.aui.run();
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private LineListener listener = event -> {
		if (event.getType() == LineEvent.Type.STOP) {
			((Clip) event.getSource()).close();
			SpeechRecogitionTest.this.soundPlaying = false;
		}
	};
	
	private class TestInputHandler implements SpeechInputHandler{
		SpeechRecogitionTest test;

		private ArrayList<String> command = new ArrayList<>();
		private int i = 0;
		
		/**
		 * 
		 */
		public TestInputHandler(SpeechRecogitionTest test) {
			// TODO Auto-generated constructor stub
			this.test = test;
		}

		/**
		 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler#handle(java.lang.String)
		 */
		@Override
		public Future<String> handle(String speechInput) {
//			assertThat(speechInput, equalTo(this.command.get(this.i++)));
			System.out.println(speechInput + "::" + speechInput.equals(this.command.get(this.i)) + "::" + this.command.get(this.i));
			i++;
			
			return null;
		}
		
		/**
		 * add a String for the Return assert Check
		 * @param s String to check
		 */
		public void addCommand(String s) {
			this.command.add(s);
		}
		
	}

}
