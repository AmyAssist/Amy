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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Class to handle the Speech Input and Output
 * @author Kai Menzel
 */
public class AudioUserInteraction implements SpeechIO {
	
	private static AudioUserInteraction audioUI;
	
	private AudioUserInteraction() {}
	
	/**
	 * @return The audioUI Object
	 */
	public static AudioUserInteraction getAudioUI() {
		if(audioUI == null) {
			audioUI = new AudioUserInteraction();
		}
		return audioUI;
	}
	
	//===============================================================================================
	
	private final String WAKEUP = "amy wake up";
	private final String GOSLEEP = "go to sleep";
	private final String SHUTDOWN = "amy shut up";
	
	// -----------------------------------------------------------------------------------------------

	private boolean recognitionThreadRunning = false;
	
	private Thread currentRecognizer;
	
	private SpeechInputHandler inputHandler;
	
	private AudioInputStream ais;

	private Grammar mainGrammar;
	private ArrayList<Grammar> switchableGrammars;
	
	private MainSpeechRecognizer mainRecognizer;
	private HashMap<String, AdditionalSpeechRecognizer> recognizerList = new HashMap<>();
	
	
	// -----------------------------------------------------------------------------------------------

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		createNewAudioInputStream();
		this.mainRecognizer = new MainSpeechRecognizer(audioUI, this.mainGrammar, this.inputHandler, this.ais);
		if(this.switchableGrammars != null && !this.switchableGrammars.isEmpty()){
			for (Grammar grammar : this.switchableGrammars) {
				if(!this.recognizerList.containsKey(grammar.getName())){
					this.recognizerList.put(grammar.getName(),
							new AdditionalSpeechRecognizer(audioUI, grammar, this.inputHandler, this.ais));					
				}
			}
		}
		this.currentRecognizer = new Thread(this.mainRecognizer);
		this.recognitionThreadRunning = true;
		this.currentRecognizer.start();
	}
	
	/**
	 * @param grammar Grammar to switch to
	 */
	public void switchGrammar(Grammar grammar){
		this.currentRecognizer.interrupt();
		if(grammar == null || grammar.getName().equals(this.mainGrammar.getName())){
			this.currentRecognizer = new Thread(this.mainRecognizer);
		}else{
			this.currentRecognizer = new Thread(this.recognizerList.get(grammar.getName()));
		}
		this.recognitionThreadRunning = true;
		this.currentRecognizer.start();
		
	}
	
	// -----------------------------------------------------------------------------------------------

	/**
	 * call this before calling start()
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO#setSpeechInputHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler)
	 */
	@Override
	public void setSpeechInputHandler(SpeechInputHandler handler) {
		this.inputHandler = handler;
	}
	
	/**
	 * call this to initiate Data
	 * call before start()
	 * @param mainGrammar The Main Grammar Mary uses
	 * @param swtichableGrammars All possible Grammar that can be changed to
	 */
	public void setGrammars(Grammar mainGrammar, ArrayList<Grammar> swtichableGrammars){
		this.mainGrammar = mainGrammar;
		this.switchableGrammars = swtichableGrammars;		
	}
	
	/**
	 * Call this if you want to set a not default AudioInputStream
	 * Call this before calling start()
	 * @param ais AudioInputStream from which shall not be created here by default
	 */
	public void setAudioInputStream(AudioInputStream ais){
		this.ais = ais;
	}
	
	//===============================================================================================
	
	/**
	 * Get's {@link #WAKEUP wAKEUP}
	 * @return  wAKEUP
	 */
	public String getWAKEUP() {
		return this.WAKEUP;
	}

	/**
	 * Get's {@link #GOSLEEP gOSLEEP}
	 * @return  gOSLEEP
	 */
	public String getGOSLEEP() {
		return this.GOSLEEP;
	}

	/**
	 * Get's {@link #SHUTDOWN sHUTDOWN}
	 * @return  sHUTDOWN
	 */
	public String getSHUTDOWN() {
		return this.SHUTDOWN;
	}

	/**
	 * Get's {@link #recognitionThreadRunning recognitionThreadRunning}
	 * @return  recognitionThreadRunning
	 */
	public boolean isRecognitionThreadRunning() {
		return this.recognitionThreadRunning;
	}

	/**
	 * Get's {@link #currentRecognizer currentRecognizer}
	 * @return  currentRecognizer
	 */
	public Thread getCurrentRecognizer() {
		return this.currentRecognizer;
	}

	/**
	 * Get's {@link #mainGrammar mainGrammar}
	 * @return  mainGrammar
	 */
	public Grammar getMainGrammar() {
		return this.mainGrammar;
	}

	/**
	 * Get's {@link #switchableGrammars switchableGrammars}
	 * @return  switchableGrammars
	 */
	public List<Grammar> getSwitchableGrammars() {
		return this.switchableGrammars;
	}

	/**
	 * Set's {@link #recognitionThreadRunning recognitionThreadRunning}
	 * @param recognitionThreadRunning  recognitionThreadRunning
	 */
	public void setRecognitionThreadRunning(boolean recognitionThreadRunning) {
		this.recognitionThreadRunning = recognitionThreadRunning;
	}

	/**
	 * Set's {@link #currentRecognizer currentRecognizer}
	 * @param currentRecognizer  currentRecognizer
	 */
	private void setCurrentRecognizer(Thread currentRecognizer) {
		this.currentRecognizer = currentRecognizer;
	}

	//===============================================================================================
	
	/**
	 * starts the default AudioInputStream
	 */
	private void createNewAudioInputStream() {
		if(this.ais == null){
    		TargetDataLine mic = null;
    		try {
    			mic = AudioSystem.getTargetDataLine(this.getFormat());
    			mic.open(this.getFormat());
    			mic.start();
    			this.ais = new AudioInputStream(mic);
    		} catch (LineUnavailableException e) {
    			e.printStackTrace();
    		}
		}
	}
		
	/**
	 * Returns the AudioFormat for the default AudioInputStream
	 * @return fitting AudioFormat
	 */
	public AudioFormat getFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	
		
	// -----------------------------------------------------------------------------------------------
	// ===============================================================================================

}
