/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.MusicEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Playlist;

/**
 * Rest Resource for music
 * 
 * @author Muhammed Kaya, Christian Bräuner
 */
@Path("music")
public class MusicResource {
	
	
	private MusicEntity musicEntity = new MusicEntity();
	private Playlist playlist;

	/**
	 * returns the currently played music
	 * 
	 * @return the currently played music
	 */
	@GET
	@Path("currentSong")
	@Produces(MediaType.APPLICATION_JSON)
	public MusicEntity getMusic() {
		return this.musicEntity;
	}
	
	/**
	 * plays the given music
	 * 
	 * @param music the music to be played
	 */
	@POST
	@Path("play")
	@Consumes(MediaType.APPLICATION_JSON)
	public void play(MusicEntity music) {
		this.musicEntity = music;
	}

	/**
	 * returns a playlist
	 * 
	 * @return the playlist
	 */
	@GET
	@Path("playlist")
	public Playlist getPlaylist() {
		return this.playlist;
	}

}
