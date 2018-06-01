/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Music Entity for JSON
 * 
 * @author Muhammed Kaya
 */
@XmlRootElement
public class MusicEntity {

	/**
	 * the artist of the music
	 */
	public String artist = "";
	
	/**
	 * the title of the music
	 */
	public String title = "";
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return this.title + " " + this.artist;
	}
}
