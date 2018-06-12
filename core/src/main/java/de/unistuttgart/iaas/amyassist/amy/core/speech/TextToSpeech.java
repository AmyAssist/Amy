package de.unistuttgart.iaas.amyassist.amy.core.speech;


import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

/**
 * Class Based on MaryTTS: https://github.com/marytts/marytts
 * Class that gives out a String input as Speech
 * First setup() the TTS to make the output later Faster
 * Before running as thread set the String-To-Say with setOutputString
 * @author Tim Neumann, Kai Menzel
 */
public class TextToSpeech {
	
	private static TextToSpeech tts;
	
	private TextToSpeech() {
		try {
			this.mary = new LocalMaryInterface();
			this.mary.setVoice("dfki-poppy-hsmm");
		} catch (MaryConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static TextToSpeech getTTS() {
		if(tts == null) {
			tts = new TextToSpeech();
		}
		return tts;
	}
	
	// -----------------------------------------------------------------------------------------------
	
	private LocalMaryInterface mary = null;
	
	/**
	 * 
	 */
	private AudioInputStream audio;
	
	private Clip outputClip;
	
	// -----------------------------------------------------------------------------------------------

	
	/**
	 * outputs Speech translated from given String
	 * @param s String that shall be said
	 */
	private void speak(String s){
		try {
			this.audio = this.mary.generateAudio(s);
			AudioFormat format = this.audio.getFormat();
		    DataLine.Info info = new DataLine.Info(Clip.class, format);
		    this.outputClip = (Clip) AudioSystem.getLine(info);
		    this.outputClip.open(this.audio);
		    this.outputClip.start();
		} catch (SynthesisException | LineUnavailableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	public void say(String s) {
		speak(preProcessing(s));
	}
	
	
	// -----------------------------------------------------------------------------------------------
	
	/**
	 * cleans String of SubString Mary can't pronounce
	 * @param s String Mary shall say
	 * @return cleaned String Mary shall say
	 */
	private String preProcessing(String s) {
		String text = s;
		text = text.replace("°C", " degree Celsius");
		text = text.replace("°F", " degree Fahrenheit");
		return text;
	}
	
	/**
	 * Method to close the outputClip
	 */
	public void stopOutput() {
		this.outputClip.close();
	}
	
	// -----------------------------------------------------------------------------------------------

	/**
	 * Get's {@link #outputClip outputClip}
	 * @return  outputClip
	 */
	public Clip getOutputClip() {
		return this.outputClip;
	}
	
}
