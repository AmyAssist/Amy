/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Rest Resource for music
 * 
 * @author Muhammed Kaya
 */
@Path("music")
public class MusicResource {
	
	@Reference
	private MusicEntity musicEntity = new MusicEntity();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public MusicEntity getMusic() {
		return musicEntity;
	}


}
