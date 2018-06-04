/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A timestamp
 * 
 * @author Christian Bräuner
 */
@XmlRootElement
public class Timestamp {
	
	/**
	 * the hour of the timestamp
	 */
	public int hour;
	
	/**
	 * the minute of the timestamp
	 */
	public int minute;
	
	
	/**
	 * constructor for a timestamp without set values
	 */
	public Timestamp() {
		// needed for JSON
	}
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String sh = String.valueOf(this.hour);
		String sm = String.valueOf(this.minute);
		if(this.hour < 10) {
			sh = "0" + sh;
		}
		if(this.minute < 10) {
			sm = "0" + sm;
		}
		return sh + ":" + sm;
	}
	
	public boolean isValid() {
		return ((this.hour>=0 && this.hour < 24) && (this.minute >= 0 && this.minute < 60));
	}
}
