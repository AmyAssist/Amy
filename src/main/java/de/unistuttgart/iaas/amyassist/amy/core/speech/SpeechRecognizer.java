/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

/**
 * Class that translate Aduio-Input into Strings, 
 * powered by CMU Sphinx - https://cmusphinx.github.io/
 * which is Licenced under BSD
 * 
 * @author Kai Menzel
 */
public class SpeechRecognizer implements SpeechIO {
	
	//wakeup-sleep-shutdown-commands
	private String wakeUp;
	private String goSleep;
	private String shutDown;
	
	//Grammar Location
	private String grammarPath;
	private String grammarName;
	
	//Audio Input Source for the Recognition
	private AudioInputStream ais = null;
	
	//Handler who use the translated String for commanding the System
	SpeechInputHandler inputHandler;

	//The Recognizer which Handles the Recognition
	private StreamSpeechRecognizer recognizer;
	
	//-----------------------------------------------------------------------------------------------	

	/**
	 * Creates the Recognizers and Configures them
	 * @param wakeUp call to start the recognition
	 * @param goSleep call to stop the recognition
	 * @param shutDown call to shutdown the whole system
	 * @param grammarPath Path to the folder of the different Grammars
	 * @param grammarName Name of the to Used Grammar (without ".gram")
	 * @param ais set custom AudioInputStream. Set *null* for default microphone input
	 */
	public SpeechRecognizer(String wakeUp, String goSleep, String shutDown, String grammarPath, String grammarName, AudioInputStream ais) {
		this.wakeUp = wakeUp;
		this.goSleep = goSleep;
		this.shutDown = shutDown;
		this.grammarPath = grammarPath;
		this.grammarName = grammarName;
		this.ais = ais;
	}
	
	//-----------------------------------------------------------------------------------------------

	/**
	 * @see java.lang.Runnable#run()
	 * Starts and runs the recognizer
	 * calls makeDecision() with the recognized String
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Create the Recognizer
		try {			
			SpeechRecognizer.this.recognizer = new StreamSpeechRecognizer(createConfiguration());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//check and start audioInputstream
		if(this.ais == null){
			startInput();
		}
		
		//starts Recognition
		this.recognizer.startRecognition(this.ais);
		
		//Boolean to check if we are supposed to listen (sleeping)
		boolean listening = false;
		
		//The result of the Recognition
		String speechRecognitionResult;
		
		while(true){
			if(!listening) System.out.println("I am sleeping");
			if(listening) System.out.println("I am awake");
			
			/**
			 * wait for input from the recognizer
			 */
			SpeechResult speechResult = null;
			while (speechResult == null) {
				speechResult = this.recognizer.getResult();
			}
			
			// Get the hypothesis (Result as String)
			speechRecognitionResult = speechResult.getHypothesis();
			
			
			//check wakeUp/sleep/shutdown
			if(speechRecognitionResult.contains(this.shutDown)){
				//TODO: Shutdown the System
				//System.exit(0);
			}else if(!listening){
				if(speechRecognitionResult.startsWith(this.wakeUp)){
					listening = true;
					if(!speechRecognitionResult.equals(this.wakeUp)){
						makeDecision(speechRecognitionResult);
					}
				}else{
					
				}
			}else{
				if(speechRecognitionResult.equals(this.goSleep)){
					listening = false;
				}else{
					makeDecision(speechRecognitionResult);
				}
			}
			
		}
	}
	
	
	
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * Gives Input to Handler
	 * checks if input is useful
	 * 
	 * @param speech the speechRecognitionResultString
	 */
	public void makeDecision(String speech) {
		String result = speech;
		if (result.replace(" ", "").equals("") || result.equals("<unk>")) {
			return;
		}

		// You said?
		System.out.println("You said: [" + result + "]\n");

		if (result.startsWith(this.wakeUp)) {
			result = result.replaceFirst(this.wakeUp + " ", "");
		}
		
		this.inputHandler.handle(result);

	}
	
	//-----------------------------------------------------------------------------------------------

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO#setSpeechInputHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler)
	 */
	@Override
	public void setSpeechInputHandler(SpeechInputHandler handler) {
		// TODO Auto-generated method stub
		
		this.inputHandler = handler;
	}
	
	//===============================================================================================
	
	/**
	 * starts the default AudioInputStream
	 */
	private void startInput(){
		TargetDataLine mic = null;
		try {
			mic = AudioSystem.getTargetDataLine(this.getFormat());
			mic.open(this.getFormat());
			mic.start();
			this.ais = new AudioInputStream(mic);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * creates Configuration for the recognizers
	 * @return the configuration
	 */
	private Configuration createConfiguration(){
		Configuration configuration = new Configuration();
		
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		configuration.setGrammarPath(this.grammarPath);
		if(this.grammarName == null){
			configuration.setUseGrammar(false);
		}else{
			configuration.setGrammarName(this.grammarName);
			configuration.setUseGrammar(true);			
		}
	
		return configuration;
	}
	
	/**
	 * Returns the AudioFormat for the default AudioInputStream
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

}
