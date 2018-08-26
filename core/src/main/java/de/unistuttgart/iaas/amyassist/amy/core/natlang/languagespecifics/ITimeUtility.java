package de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics;

import java.time.LocalTime;

/**
 * 
 * this interface provide methods for time processing 
 * @author Lars Buttgereit
 */
public interface ITimeUtility {
	/**
	 * parse a string to a localTime
	 * @param toParse String to parse 
	 * @return a LocalTime object
	 */
	LocalTime parseTime(String toParse);
	
	/**
	 * format the time from google speech to a more parser friendly format. for example english time: 1:00 p.m. to 1 x 00 pm
	 * @param input the string to replace the time
	 * @return the input string with replaced time
	 */
	String formatTime(String input);
}
