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
	
	
	private MusicEntity musicEntity = new MusicEntity();
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
	public MusicEntity getMusic() {
		this.musicEntity = new MusicEntity(this.logic.getCurrentSong().get("name"), this.logic.getCurrentSong().get("artist"));
		return this.musicEntity;
	}
	
	/**
	 * plays the given music
	 * 
	 * @param music the music to be played
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
