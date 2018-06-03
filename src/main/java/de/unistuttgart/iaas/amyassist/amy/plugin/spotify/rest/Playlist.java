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
 * Playlist entity for JSON
 * @author Christian Bräuner
 */
@XmlRootElement
public class Playlist {
	
	/**
	 * the name of the playlist
	 */
	public String name = "";
	
	/**
	 * the songs in the playlist
	 */
	public MusicEntity[] songs;

}
