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
public class TextToSpeech implements Runnable{
	
	private String outputString;
	
	private LocalMaryInterface mary = null;
	
	// -----------------------------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public TextToSpeech() {
		// TODO Auto-generated constructor stub
		try {
			this.mary = new LocalMaryInterface();
			this.mary.setVoice("dfki-poppy-hsmm");
		} catch (MaryConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// -----------------------------------------------------------------------------------------------
		
	/**
	 * first set the string to say
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		speak(this.outputString);
	}	
	
	/**
	 * outputs Speech translated from given String
	 * @param s
	 */
	private void speak(String s){
		AudioInputStream audio;
		try {
			audio = this.mary.generateAudio(s);
			AudioFormat format = audio.getFormat();
		    DataLine.Info info = new DataLine.Info(Clip.class, format);
		    Clip clip = (Clip) AudioSystem.getLine(info);
		    clip.open(audio);
		    clip.start();
		} catch (SynthesisException | LineUnavailableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	// -----------------------------------------------------------------------------------------------
	
	/**
	 * set String to say, before you start the Thread
	 * @param s String Mary shall say
	 */
	public void setOutputString(String s) {
		this.outputString = preProcessing(s);
	}
	
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
}
