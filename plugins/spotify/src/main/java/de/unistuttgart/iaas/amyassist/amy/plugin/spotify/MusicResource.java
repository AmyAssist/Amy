/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.MusicEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Playlist;

/**
 * Rest Resource for music
 * TODO extend functionality
 * 
 * @author Muhammed Kaya, Christian Bräuner
 */
@Path("music")
public class MusicResource {
	
	private MusicEntity musicEntity;
	private Playlist playlist;
	
	@Reference
	private PlayerLogic logic;

	/**
	 * returns the currently played music
	 * 
	 * @return the currently played music
	 */
	@GET
	@Path("currentSong")
	@Produces(MediaType.APPLICATION_JSON)
	public MusicEntity getCurrentSong() {
		HashMap<String, String> currentSong = this.logic.getCurrentSong();
		this.musicEntity = new MusicEntity();
		if(currentSong != null && currentSong.containsKey("name") && currentSong.containsKey("artist") ) {
			this.musicEntity = new MusicEntity(currentSong.get("name"), currentSong.get("artist"));			
		}
		return this.musicEntity;
	}
	
	/**
	 * plays the given music
	 * 
	 * @param music
	 *            the music to be played
	 * @return the answer from the player
	 */
	@POST
	@Path("play")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String play(MusicEntity music) {
		this.musicEntity = music;
		this.logic.search(this.musicEntity.toString(), Search.TYPE_TRACK, 5);
		return this.logic.play(0);
	}
	
	/**
	 * resumes the actual playback
	 * 
	 * @return HTTP Response with player status
	 */
	@POST
	@Path("resume")
	@Produces(MediaType.TEXT_PLAIN)
	public String resume() {
		if (this.logic.resume()) {
			return "resume";
		} 
		throw new WebApplicationException("Check player state", Status.CONFLICT);
					
	}

	/**
	 * pauses the actual playback
	 * 
	 * @return HTTP Response with player status
	 */
	@POST
	@Path("pause")
	@Produces(MediaType.TEXT_PLAIN)
	public String pausePlayback() {
		if(this.logic.pausePlayback()) {
			return "pause";
		}		
		throw new WebApplicationException("Check player state", Status.CONFLICT);		
	}

	/**
	 * returns a playlist
	 * 
	 * @return the playlist
	 */
	@GET
	@Path("playlist")
	@Produces(MediaType.APPLICATION_JSON)
	public Playlist getPlaylist() {
		return this.playlist;
	}
	
	/**
	 * controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down, or a volume value between 0 and 100
	 * @return a int from 0-100. This represent the Volume in percent.
	 */
	@POST
	@Path("volume/{volumeValue}")
	@Produces(MediaType.TEXT_PLAIN)
	public String setVolume(@PathParam("volumeValue") String volumeString) {
		try {
			int volume = Integer.parseInt(volumeString);
			if(volume < 0 || volume > 100) {
				throw new WebApplicationException("Incorrect volume value", Status.BAD_REQUEST);
			}
			this.logic.setVolume(volume);
			return String.valueOf(volume);
		} catch (NumberFormatException e) {
		if (volumeString != "mute" && volumeString != "max" 
				&& volumeString != "up" && volumeString != "down") {
			throw new WebApplicationException("Incorrect volume command", Status.BAD_REQUEST);
		} else {
			int volume = this.logic.setVolume(volumeString);
			if (volume != -1) {
				return String.valueOf(volume);
			}
			throw new WebApplicationException("Check player state", Status.CONFLICT);		
			
		}
			
		}
		
	}

}
