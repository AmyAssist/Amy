/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 * Class that translate Aduio-Input into Strings, powered by CMU Sphinx -
 * https://cmusphinx.github.io/ which is Licenced under BSD
 * 
 * @author Kai Menzel
 */
@Service
public class SpeechRecognizer implements SpeechIO {

	// wakeup-sleep-shutdown-commands
	private String wakeUp;
	private String goSleep;
	private String shutDown;

	// Grammar Location
	private String grammarPath;
	private String grammarName;
	
	//Sound return from the recognizer, 
	public enum SOUND{
		WAKEUP, GOSLEEP, ACCEPT
	}
	public static boolean soundPlayed = false;

	// Audio Input Source for the Recognition
	private AudioInputStream ais = null;

	/**
	 * Handler who use the translated String for commanding the System
	 */
	private SpeechInputHandler inputHandler;

	// The Recognizer which Handles the Recognition
	private StreamSpeechRecognizer recognizer;

	// -----------------------------------------------------------------------------------------------

	/**
	 * Creates the Recognizers and Configures them
	 * 
	 * @param wakeUp
	 *            call to start the recognition
	 * @param goSleep
	 *            call to stop the recognition
	 * @param shutDown
	 *            call to shutdown the whole system
	 * @param grammarPath
	 *            Path to the folder of the different Grammars
	 * @param grammarName
	 *            Name of the to Used Grammar (without ".gram")
	 * @param ais
	 *            set custom AudioInputStream. Set *null* for default microphone
	 *            input
	 */
	public SpeechRecognizer(String wakeUp, String goSleep, String shutDown, String grammarPath, String grammarName,
			AudioInputStream ais) {
		this.wakeUp = wakeUp;
		this.goSleep = goSleep;
		this.shutDown = shutDown;
		this.grammarPath = grammarPath;
		this.grammarName = grammarName;
		this.ais = ais;
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * @see java.lang.Runnable#run() Starts and runs the recognizer calls
	 *      makeDecision() with the recognized String
	 */
	@Override
	public void run() {
		// Create the Recognizer
		try {
			SpeechRecognizer.this.recognizer = new StreamSpeechRecognizer(createConfiguration());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// check and start audioInputstream
		if (this.ais == null) {
			startInput();
		}

		// starts Recognition
		this.recognizer.startRecognition(this.ais);

		// Boolean to check if we are supposed to listen (sleeping)
		boolean listening = false;

		// The result of the Recognition
		String speechRecognitionResult;

		loop: while (!Thread.interrupted()) {
			if (!listening)
				System.out.println("I am sleeping");
			if (listening)
				System.out.println("I am awake");

			/**
			 * wait for input from the recognizer
			 */
			SpeechResult speechResult = null;
			while (speechResult == null || SpeechRecognizer.soundPlayed) {
				speechResult = this.recognizer.getResult();
				if (Thread.interrupted()) {
					break loop;
				}
			}

			// Get the hypothesis (Result as String)
			speechRecognitionResult = speechResult.getHypothesis();

			// check wakeUp/sleep/shutdown
			if(speechRecognitionResult.equals(this.wakeUp)){
				listening = true;
				(new Thread(new Output(getFormat(), SOUND.WAKEUP))).start();
			}else if(speechRecognitionResult.startsWith(this.wakeUp + " ")){
				listening = true;
				(new Thread(new Output(getFormat(), SOUND.ACCEPT))).start();
				makeDecision(speechRecognitionResult.replaceFirst(this.wakeUp + " ", ""));
			}else if (listening){
				if(speechRecognitionResult.equals(this.goSleep)){
					listening = false;
					(new Thread(new Output(getFormat(), SOUND.GOSLEEP))).start();
				}else{
					(new Thread(new Output(getFormat(), SOUND.ACCEPT))).start();
					makeDecision(speechRecognitionResult);
				}
			}

		}
		this.recognizer.stopRecognition();
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * Gives Input to Handler checks if input is useful
	 * 
	 * @param speech
	 *            the speechRecognitionResultString
	 */
	public void makeDecision(String speech) {
		String result = speech;
		if (result.replace(" ", "").equals("") || result.equals("<unk>")) {
			return;
		}

		// You said?
		System.out.println("You said: [" + result + "]");

		

		Future<String> handle = this.inputHandler.handle(result);
		try {
			System.out.println(handle.get());
			System.out.println();
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() != null && e.getCause().getClass().equals(IllegalArgumentException.class)) {
				System.out.println("Unknown command");
			} else {
				e.printStackTrace();
			}
		}
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO#setSpeechInputHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler)
	 */
	@Override
	public void setSpeechInputHandler(SpeechInputHandler handler) {
		this.inputHandler = handler;
	}

	//===============================================================================================
	
		
		
	// ===============================================================================================

	/**
	 * starts the default AudioInputStream
	 */
	private void startInput() {
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

	/**
	 * creates Configuration for the recognizers
	 * 
	 * @return the configuration
	 */
	private Configuration createConfiguration() {
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		configuration.setGrammarPath(this.grammarPath);
		if (this.grammarName == null) {
			configuration.setUseGrammar(false);
		} else {
			configuration.setGrammarName(this.grammarName);
			configuration.setUseGrammar(true);
		}

		return configuration;
	}

	/**
	 * Returns the AudioFormat for the default AudioInputStream
	 * 
	 * @return fitting AudioFormat
	 */
	public AudioFormat getFormat() {
		float sampleRate = 16000.0f;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	
	// ===============================================================================================
	
	private class Output implements Runnable {
		
		AudioFormat af;
		private Clip c;
		private SpeechRecognizer.SOUND soundType;
		
		public Output(AudioFormat af, SpeechRecognizer.SOUND soundType){
			this.af = af;
			this.soundType = soundType;
		}
		
		public void run() {
			SpeechRecognizer.soundPlayed = true;
			double frequency1 = 0;
			double frequency2 = 0;
			int lenght = 250;
			switch(soundType){
			case WAKEUP:
				frequency1 = 329.23;
				frequency2 = 440.00;
				lenght = 300;
				break;
			case GOSLEEP:
				frequency1 = 329.23;
				frequency2 = 261.63;
				break;
			case ACCEPT:
				frequency1 = 440.00;
				frequency2 = 392.00;
				break;
			default:
				break;
			}
			try {
				play(frequency1);
				Thread.sleep(lenght);
				play(frequency2);
				Thread.sleep(750);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SpeechRecognizer.soundPlayed = false;
			this.c.close();
		}
		
		/**
		 * creates Sinus Wave sound with requested frequency
		 * @param frequency requested frequency
		 * @param af Audioformat
		 * @return Sinus Wave Sound
		 */
		private byte[] getSinusTone(double frequency, AudioFormat af) {
	        byte sample_size = (byte) (af.getSampleSizeInBits() / 8);
	        byte[] data = new byte[(int) af.getSampleRate() * sample_size];
	        double step_width = (2 * Math.PI) / af.getSampleRate();
	        double x = 0;

	        for (int i = 0; i < data.length/4; i += sample_size) {
	            int sample_max_value = (int) Math.pow(2, af.getSampleSizeInBits()) / 2 - 1;
	            int value = (int) (sample_max_value * Math.sin(frequency * x));
	            for (int j = 0; j < sample_size; j++) {
	                byte sample_byte = (byte) ((value >> (8 * j)) & 0xff);
	                data[i + j] = sample_byte;
	            }
	            x += step_width;
	        }
	        return data;
	    }
		
		/**
		 * opens Outputline and playes sound
		 * @param frequenzy frequency of requested SinusWaveound
		 */
		private void play(double frequenzy) {
	        byte[] data = getSinusTone(frequenzy, this.af);
	       
	        try {
	            this.c = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

	            this.c.open(this.af, data, 0, data.length);
	            this.c.start();
	            
	        } catch (LineUnavailableException ex) {
	            ex.printStackTrace();
	        }
	    }
	}

}
