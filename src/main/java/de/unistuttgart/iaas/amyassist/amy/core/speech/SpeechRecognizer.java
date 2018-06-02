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
	
	//Audio Input Source for the Recognition
	static AudioInputStream ais = null;
	
	//Handler who use the translated String for commanding the System
	SpeechInputHandler inputHandler;
	SpeechCommandHandler commandHandler = new SpeechCommandHandler();
	

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
		SpeechRecognizer.ais = ais;
		
		//Create the Recognizers
		try {			
			SpeechRecognizer.this.recognizer = new StreamSpeechRecognizer(createConfiguration(grammarPath, grammarName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		//check and start audioInputstream
		while(SpeechRecognizer.ais == null){
			startInput();
			Thread.yield();
		}
		
		//starts Recognition
		this.recognizer.startRecognition(SpeechRecognizer.ais);
		
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
				if(speechRecognitionResult.contains(this.goSleep)){
					listening = false;
				}else{
					makeDecision(speechRecognitionResult);
				}
			}
			
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO#setSpeechInputHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler)
	 */
	@Override
	public void setSpeechInputHandler(SpeechInputHandler handler) {
		// TODO Auto-generated method stub
		
		this.inputHandler = handler;

	}
	
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * Gives Input to Handler
	 * checks if input is useful
	 * 
	 * @param speech the speechRecognitionResultString
	 */
	public void makeDecision(String speech) {
		if (speech.replace(" ", "").equals("") || speech.equals("<unk>")) {
			return;
		}

		// You said?
		System.out.println("You said: [" + speech + "]\n");

		if (speech.startsWith(this.wakeUp)) {
			speech = speech.replaceFirst(this.wakeUp + " ", "");
		}
		
		//TODO: give speech result to Handler
//		this.commandHandler.handleSpeechInput(speech);

	}
	
	//===============================================================================================
	
	/**
	 * starts the default AudioInputStream
	 */
	private void startInput(){
		TargetDataLine mic = null;
		try {
			mic = AudioSystem.getTargetDataLine(SpeechRecognizer.getFormat());
			mic.open(SpeechRecognizer.getFormat());
			mic.start();
			SpeechRecognizer.ais = new AudioInputStream(mic);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * creates Configuration for the recognizers
	 * @param grammarPath Path to the folder of the grammar
	 * @param grammarName the name of the grammar (without .gram)
	 * @return the configuration
	 */
	private Configuration createConfiguration(String grammarPath, String grammarName){
		Configuration configuration = new Configuration();
		
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		configuration.setGrammarPath(grammarPath);
		if(grammarName == null){
			configuration.setUseGrammar(false);
		}else{
			configuration.setGrammarName(grammarName);
			configuration.setUseGrammar(true);			
		}
	
		return configuration;
	}
	
	/**
	 * Returns the Audioformat for the default AudioInputStream
	 * @return
	 */
	public static AudioFormat getFormat() {
		float sampleRate = 16000.0f;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

}
