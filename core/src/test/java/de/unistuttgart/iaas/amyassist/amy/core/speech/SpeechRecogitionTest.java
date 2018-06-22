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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TODO: Description
 * 
 * @author
 */
class SpeechRecogitionTest {
	
	AudioUserInteraction aui;
	Grammar mainGrammar;
	Grammar addGrammar;
	List<Grammar> switchGrammar;
	SpeechInputHandler handler;
	
	@BeforeEach
	void setUp() {
		this.aui = new AudioUserInteraction();
		this.mainGrammar = new Grammar("mainGrammar", new File("resources/mainGrammar"));
		this.addGrammar = new Grammar("addGrammar", new File("resources/addGrammar"));
		this.handler = new testInputHandler();
		this.switchGrammar = new ArrayList<>();
	}
	
	@SuppressWarnings("boxing")
	@Test
	void testGetterSetter() {
		assertThat(this.aui.getWAKEUP(), equalTo(Constants.WAKEUP));
		assertThat(this.aui.getGOSLEEP(), equalTo(Constants.GOSLEEP));
		assertThat(this.aui.getSHUTDOWN(), equalTo(Constants.SHUTDOWN));
		
		assertThat(this.aui.isRecognitionThreadRunning(), equalTo(false));
		assertThat(this.aui.getCurrentRecognizer(), equalTo(null));
		assertThat(this.aui.getMainGrammar(), equalTo(null));
		assertThat(this.aui.getSwitchableGrammars(), equalTo(null));
		
		this.aui.setSpeechInputHandler(this.handler);
		this.aui.setAudioInputStream(null);
		
		this.switchGrammar.add(this.addGrammar);
		
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

	@Test
	void overallTest() {
		
		
		
	}
	
	private class testInputHandler implements SpeechInputHandler{

		private String command = null;
		
		/**
		 * 
		 */
		public testInputHandler() {
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler#handle(java.lang.String)
		 */
		@Override
		public Future<String> handle(String speechInput) {
			// TODO Auto-generated method stub
			setCommand(speechInput);
			return null;
		}
		/**
		 * Get's {@link #command command}
		 * @return  command
		 */
		public String getCommand() {
			return this.command;
		}
		/**
		 * Set's {@link #command command}
		 * @param command  command
		 */
		public void setCommand(String command) {
			this.command = command;
		}
		
	}

}
