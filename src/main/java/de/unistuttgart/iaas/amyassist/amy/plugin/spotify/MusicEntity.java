/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Music Entity for JSON File
 * 
 * @author Muhammed Kaya
 */
@XmlRootElement
public class MusicEntity {

	public String artist = "";
	public String title = "";
	
}
