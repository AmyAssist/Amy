/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
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
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.MusicEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.PlaylistEntity;

/**
 * Rest Resource for music
 * 
 * @author Muhammed Kaya, Christian Bräuner
 */
@Path(MusicResource.PATH)
public class MusicResource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "music";

	@Reference
	private PlayerLogic logic;

	@Reference
	private StringGenerator stringGenerator;

	private MusicEntity musicEntity;

	/**
	 * needed for the first init. Use the clientID and the clientSecret form a spotify devloper account or load the
	 * properties file in apikeys
	 * 
	 * @param clientID
	 *            from spotify developer account
	 * @param clientSecret
	 *            from spotify developer account
	 * @return login link to a personal spotify account
	 */
	@POST
	@Path("init")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public URI firstTimeInit(@QueryParam("clientID") String clientID, @QueryParam("clientSecret") String clientSecret) {
		if (clientID != null && clientSecret != null) {
			URI uri = this.logic.firstTimeInit(clientID, clientSecret);
			if (uri != null) {
				return uri;
			}
			throw new WebApplicationException("Enter valid client information", Status.CONFLICT);
		}
		URI uri = this.logic.firstTimeInit();
		if (uri != null) {
			return uri;
		}
		throw new WebApplicationException("Check your property location", Status.CONFLICT);
	}

	/**
	 * create the refresh token in he authorization object with the authCode
	 * 
	 * @param authCode
	 *            Callback from the login link
	 * @return response that token is created
	 */
	@POST
	@Path("token/{authCode}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String inputAuthCode(@PathParam("authCode") String authCode) {
		this.logic.inputAuthCode(authCode);
		return "Token created.";
	}

	/**
	 * returns a list with all given devices
	 * 
	 * @return a list with all given devices
	 */
	@GET
	@Path("getDevices")
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceEntity[] getDevices() {
		List<DeviceEntity> deviceList = this.logic.getDevices();
		DeviceEntity[] devices = new DeviceEntity[deviceList.size()];
		for (int i = 0; i < deviceList.size(); i++) {
			devices[i] = deviceList.get(i);
		}
		return devices;
	}

	/**
	 * sets which device to use
	 * 
	 * @param deviceValue
	 *            the number or ID of the device to use
	 * @return the selected device if there is one and is available
	 */
	@POST
	@Path("setDevice/{deviceValue}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setDevice(@PathParam("deviceValue") String deviceValue) {
		try {
			int deviceNumber = Integer.parseInt(deviceValue);
			String result = this.logic.setDevice(deviceNumber);
			if (result.equals("No device found")) {
				throw new WebApplicationException("No device found", Status.NOT_FOUND);
			}
			return result;
		} catch (NumberFormatException e) {
			if (this.logic.setDevice(deviceValue)) {
				return "Device: '" + deviceValue + "' is selected now";
			}
			throw new WebApplicationException("Device: '" + deviceValue + "' is not available", Status.CONFLICT);
		}
	}

	/**
	 * returns the currently played music
	 * 
	 * @return the currently played music
	 */
	@GET
	@Path("currentSong")
	@Produces(MediaType.APPLICATION_JSON)
	public MusicEntity getCurrentSong() {
		Map<String, String> currentSong = this.logic.getCurrentSong();
		this.musicEntity = new MusicEntity();
		if (currentSong != null && currentSong.containsKey("title") && currentSong.containsKey("artist")) {
			this.musicEntity = new MusicEntity(currentSong.get("title"), currentSong.get("artist"));
			return this.musicEntity;
		}
		throw new WebApplicationException("No song is currently playing", Status.CONFLICT);
	}

	/**
	 * this method allows to search artist, track, playlist and album names
	 * 
	 * @param searchText
	 *            the text you want to search
	 * @param type
	 *            artist, track, playlist, album
	 * @param limit
	 *            how many results maximal searched for
	 * @return one output String with all results
	 */
	@POST
	@Path("search/{searchText}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, String>> search(@PathParam("searchText") String searchText,
			@QueryParam("type") @DefaultValue("track") String type, @QueryParam("limit") @DefaultValue("5") int limit) {
		List<Map<String, String>> actualSearchResult;
		switch (type) {
		case "artist":
			actualSearchResult = this.logic.search(searchText, "artist", limit);
			break;
		case "playlist":
			actualSearchResult = this.logic.search(searchText, "playlist", limit);
			break;
		case "album":
			actualSearchResult = this.logic.search(searchText, "album", limit);
			break;
		case "track":
		default:
			actualSearchResult = this.logic.search(searchText, "track", limit);
			break;
		}
		if (actualSearchResult != null) {
			return actualSearchResult;
		}
		throw new WebApplicationException("No results found", Status.NO_CONTENT);
	}

	/**
	 * plays the given music
	 * 
	 * @param songNumber
	 *            which song or playlist number should be played
	 * @param type
	 *            where to play: allowed parameters: user (playlist), featured (playlist), track
	 * @param limit
	 *            limit of returned tracks or playlists
	 * @param music
	 *            the music to be played
	 * @return the playing music if there is one
	 */
	@POST
	@Path("play")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String play(MusicEntity music, @QueryParam("songNumber") @DefaultValue("0") int songNumber,
			@QueryParam("type") @DefaultValue("track") String type, @QueryParam("limit") @DefaultValue("5") int limit) {
		switch (type) {
		case "user":
			getPlaylists("user", limit);
			return this.stringGenerator
					.generateSearchOutputString(this.logic.play(songNumber, SearchTypes.USER_PLAYLISTS));
		case "featured":
			getPlaylists("featured", limit);
			return this.stringGenerator
					.generateSearchOutputString(this.logic.play(songNumber, SearchTypes.FEATURED_PLAYLISTS));
		case "track":
		default:
			if (music != null) {
				this.logic.search(music.toString(), SpotifyConstants.TYPE_TRACK, limit);
				this.musicEntity = new MusicEntity(music.getTitle(), music.getArtist());
				return this.stringGenerator
						.generateSearchOutputString(this.logic.play(songNumber, SearchTypes.NORMAL_SEARCH));
			}
			throw new WebApplicationException("Found nothing to play.", Status.CONFLICT);
		}
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
	public String pause() {
		if (this.logic.pause()) {
			return "pause";
		}
		throw new WebApplicationException("Check player state", Status.CONFLICT);
	}

	/**
	 * skips the actual playback
	 * 
	 * @return HTTP Response with player status
	 */
	@POST
	@Path("skip")
	@Produces(MediaType.TEXT_PLAIN)
	public String skip() {
		if (this.logic.skip()) {
			return "skip";
		}
		throw new WebApplicationException("Check player state", Status.CONFLICT);
	}

	/**
	 * pauses the actual playback
	 * 
	 * @return HTTP Response with player status
	 */
	@POST
	@Path("back")
	@Produces(MediaType.TEXT_PLAIN)
	public String back() {
		if (this.logic.back()) {
			return "back";
		}
		throw new WebApplicationException("Check player state", Status.CONFLICT);
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
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setVolume(@PathParam("volumeValue") String volumeString) {
		try {
			int volume = Integer.parseInt(volumeString);
			if (volume < 0 || volume > 100) {
				throw new WebApplicationException("Incorrect volume value", Status.BAD_REQUEST);
			}
			this.logic.setVolume(volume);
			return String.valueOf(volume);
		} catch (NumberFormatException e) {
			if (volumeString != "mute" && volumeString != "max" && volumeString != "up" && volumeString != "down") {
				throw new WebApplicationException("Incorrect volume command", Status.BAD_REQUEST);
			}
			int volume = this.logic.setVolume(volumeString);
			if (volume != -1) {
				return String.valueOf(volume);
			}
			throw new WebApplicationException("Check player state", Status.CONFLICT);
		}
	}

	/**
	 * get user or featured playlists
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @param type
	 *            type of playlists: allowed parameters: user, featured
	 * @return user or featured playlists
	 */
	@POST
	@Path("playlists/{type}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public PlaylistEntity[] getPlaylists(@PathParam("type") String type, @QueryParam("limit") @DefaultValue("5") int limit) {
		List<PlaylistEntity> pl;
		PlaylistEntity[] playlists;
		switch (type) {
		case "user":
			pl = this.logic.getOwnPlaylists(limit);
			break;
		case "featured":
			pl = this.logic.getFeaturedPlaylists(limit);
			break;
		default:
			pl = this.logic.getFeaturedPlaylists(limit);
			break;
		}
		playlists = new PlaylistEntity[pl.size()];
		if (!pl.isEmpty()) {
			for (int i = 0; i < pl.size(); i++) {
				playlists[i] = new PlaylistEntity(pl.get(i).getName(), pl.get(i).getSongs(), pl.get(i).getUri(),
						pl.get(i).getImageUrl());
			}
			return playlists;
		}
		throw new WebApplicationException("No Playlist is available", Status.NOT_FOUND);
	}

}
