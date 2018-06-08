/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	public static final String wakeUp = "amy wake up";
	public static final String goSleep = "go to sleep";
	public static final String shutdown = "amy shutdown";
	
	
	/**
	 * Information for the Recognizers
	 */
	public static boolean soundPlaying = false;
	public static boolean threadRunning = false;
	
	private static Thread currentRecognizer;
	
	private SpeechInputHandler inputHandler;
	
	private AudioInputStream ais;
	
	private static TextToSpeech output = new TextToSpeech();
	private static Thread tts;
	
	private static Grammar mainGrammar;
	private ArrayList<Grammar> switchableGrammars;
	
	private static MainSpeechRecognizer mainRecognizer;
	private static HashMap<String, SpeechRecognizer> recognizerList = new HashMap<>();
	
	// -----------------------------------------------------------------------------------------------

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		createNewAudioInputStream();
		AudioUserInteraction.mainRecognizer = new MainSpeechRecognizer(AudioUserInteraction.mainGrammar, this.inputHandler, this.ais);
		if(this.switchableGrammars != null && !this.switchableGrammars.isEmpty()){
			for (Grammar grammar : this.switchableGrammars) {
				if(!AudioUserInteraction.recognizerList.containsKey(grammar.getName())){
					AudioUserInteraction.recognizerList.put(grammar.getName(), new SpeechRecognizer(grammar, this.inputHandler, this.ais));					
				}
			}
		}
		AudioUserInteraction.currentRecognizer = new Thread(mainRecognizer);
		AudioUserInteraction.threadRunning = true;
		AudioUserInteraction.currentRecognizer.start();
	}
	
	/**
	 * @param grammar Grammar to switch to
	 */
	public static void switchGrammar(Grammar grammar){
		AudioUserInteraction.currentRecognizer.interrupt();
		if(grammar == null || grammar.getName().equals(AudioUserInteraction.mainGrammar.getName())){
			AudioUserInteraction.currentRecognizer = new Thread(AudioUserInteraction.mainRecognizer);
		}else{
			AudioUserInteraction.currentRecognizer = new Thread(recognizerList.get(grammar.getName()));
		}
		AudioUserInteraction.threadRunning = true;
		AudioUserInteraction.currentRecognizer.start();
		
	}
	
	
	/**
	 * tell Amy to say something
	 * @param s String you want Amy to say
	 */
	public static void say(String s) {
		AudioUserInteraction.soundPlaying = true;
		AudioUserInteraction.output.setOutputString(s);
		AudioUserInteraction.tts = new Thread(AudioUserInteraction.output);
		AudioUserInteraction.tts.start();
		while(AudioUserInteraction.tts.isAlive()) {
			AudioUserInteraction.currentRecognizer.yield();
		}
		AudioUserInteraction.soundPlaying = false;
	}
	
	// -----------------------------------------------------------------------------------------------

	/**
	 * call this before calling start()
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechIO#setSpeechInputHandler(de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler)
	 */
	@Override
	public void setSpeechInputHandler(SpeechInputHandler handler) {
		// TODO Auto-generated method stub
		this.inputHandler = handler;
	}
	
	/**
	 * call this to initiate Data
	 * call before start()
	 * @param mainGrammar The Main Grammar Mary uses
	 * @param swtichableGrammars All possible Grammar that can be changed to
	 */
	public void setData(Grammar mainGrammar, ArrayList<Grammar> swtichableGrammars){
		AudioUserInteraction.mainGrammar = mainGrammar;
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
	 * starts the default AudioInputStream
	 */
	private void createNewAudioInputStream() {
		if(this.ais == null){
    		// TODO Auto-generated method stub
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
		
	// -----------------------------------------------------------------------------------------------
	// ===============================================================================================

}
