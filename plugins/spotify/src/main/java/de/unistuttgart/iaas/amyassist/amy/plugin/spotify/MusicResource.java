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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
	public Response getMusic() {
		this.musicEntity = new MusicEntity(this.logic.getCurrentSong().get("name"), this.logic.getCurrentSong().get("artist"));
		return Response.status(Status.OK).entity(this.musicEntity).build();
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
	public Response play(MusicEntity music) {
		this.musicEntity = music;
		this.logic.search(this.musicEntity.toString(), Search.TYPE_TRACK, 5);
		return Response.status(Status.OK).entity(this.logic.play(0)).build();
	}
	
	/**
	 * resumes the actual playback
	 * 
	 * @return HTTP Response with player status
	 */
	@POST
	@Path("resume")
	@Produces(MediaType.TEXT_PLAIN)
	public Response resume() {
		if (this.logic.resume()) {
			return Response.status(Status.OK).entity("resume").build();
		} else {
			return Response.status(Status.CONFLICT)
					.entity("Check player state").build();
		}
	}

	/**
	 * pauses the actual playback
	 * 
	 * @return HTTP Response with player status
	 */
	@POST
	@Path("pause")
	@Produces(MediaType.TEXT_PLAIN)
	public Response pausePlayback() {
		if(this.logic.pausePlayback()) {;
			return Response.status(Status.OK).entity("pause").build();
		} else {
			return Response.status(Status.CONFLICT)
					.entity("Check player state").build();
		}
	}

	/**
	 * returns a playlist
	 * 
	 * @return the playlist
	 */
	@GET
	@Path("playlist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPlaylist() {
		return Response.status(Status.OK).entity(this.playlist).build();
	}
	
	/**
	 * controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down
	 * @return a int from 0-100. This represent the Volume in percent. if the Playerstate incorrect return -1
	 */
	@POST
	@Path("volume")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response setVolume(String volumeString) {
		if (volumeString != "mute" && volumeString != "max" 
				&& volumeString != "up" && volumeString != "down") {
			return Response.status(Status.BAD_REQUEST)
					.entity("Incorrect volume command").build();
		} else {
			int volume = this.logic.setVolume(volumeString);
			if (volume != -1) {
				return Response.status(Status.OK).entity(volume).build();
			} else {
				return Response.status(Status.CONFLICT)
						.entity("Check player state").build();
			}
		}
	}

}
