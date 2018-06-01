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
import java.util.List;
import java.util.logging.Level;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

/**
 * TODO: Description
 * @author Tim Neumann
 */
public class SpeechRecognizer implements SpeechIO {
	
	//wakeup-sleep-shutdown
	private String wakeUp;
	private String goSleep;
	private String shutDown;
	
	public static enum GRAMMAR{
		MAIN, NO
	}

	//All different Recognizers (each can have a different Grammparserar)
//	private StreamSpeechRecognizer mainGrammer_recognizer;
//	private StreamSpeechRecognizer keyGrammar_recognition;
//	private StreamSpeechRecognizer noGrammer_recognizer;
	
	private LiveSpeechRecognizer recognizer;
	
	String speechRecognitionResult;

	/**
	 * 
	 */
	public SpeechRecognizer(String wakeUp, String goSleep, String shutDown, String p_grammarPath) {
		this.wakeUp = wakeUp;
		this.goSleep = goSleep;
		this.shutDown = shutDown;
		
		try {
//			SpeechRecognizer.this.noGrammer_recognizer = new StreamSpeechRecognizer(createConfiguration(p_grammarPath, null));
//			SpeechRecognizer.this.keyGrammar_recognition = new StreamSpeechRecognizer(createConfiguration(p_grammarPath, "keywordGrammar"));
//			SpeechRecognizer.this.mainGrammer_recognizer = new StreamSpeechRecognizer(createConfiguration(p_grammarPath, "mainGrammar"));
			recognizer = new LiveSpeechRecognizer(createConfiguration(p_grammarPath, "grammar"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		recognizer.startRecognition(true);
		boolean listening = false;
		
		while(true){
			System.out.println("Hello");
			if(listening) System.out.println("Im awake");
			
			SpeechResult speechResult = null;
			while (speechResult == null) {
				speechResult = this.recognizer.getResult();
			}
			
			// Get the hypothesis
			speechRecognitionResult = speechResult.getHypothesis();
			
			if(speechRecognitionResult.contains(shutDown)){
				System.exit(0);
			}else if(!listening){
				if(speechRecognitionResult.startsWith(wakeUp)){
					listening = true;
					makeDecision(speechRecognitionResult);
				}else{
					
				}
			}else{
				if(speechRecognitionResult.contains(goSleep)){
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

	}
	
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * Takes a decision based on the given result
	 * 
	 * @param speechWords
	 */
	public void makeDecision(String speech) {

		if (speech.replace(" ", "").equals("") || speech.equals("<unk>")) {
			return;
		}

		// You said?
		System.out.println("You said: [" + speech + "]\n");

		if (speech.startsWith(this.wakeUp)) {
			speech.replaceFirst(this.wakeUp + " ", "");
		}
		
		System.out.println(speech);
//		Main.handleInput(speech.split(" "));

	}
	
	//-----------------------------------------------------------------------------------------------
	
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

}
