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
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.PlayerLogic;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;

/**
 * Rest Resource for music
 * 
 * @author Muhammed Kaya, Christian Bräuner, Lars Buttgereit
 */
@Path(MusicResource.PATH)
public class MusicResource implements Resource{

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "music";

	@Reference
	private PlayerLogic logic;

	@Reference
	private StringGenerator stringGenerator;
	
	@Reference
	private DeviceLogic deviceLogic;

	private MusicEntity musicEntity;

	/**
	 * needed for the first init. Use the clientID and the clientSecret as query parameter form a spotify devloper
	 * account or load the properties file in apikeys if there is one in the property location
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
		if (clientID != null && clientSecret != null && !clientID.equals("") && !clientSecret.equals("")) {
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
	 * create the refresh token in the authorization object with the authCode
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
	 * get all devices that logged in at the moment
	 * 
	 * @return empty json object if there are no devices available else a json object with all given devices
	 */
	@GET
	@Path("getDevices")
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceEntity[] getDevices() {
		List<DeviceEntity> deviceList = this.deviceLogic.getDevices();
		if (deviceList.isEmpty()) {
			throw new WebApplicationException("Currently there are no devices available or connected", Status.CONFLICT);
		}
		DeviceEntity[] devices = new DeviceEntity[deviceList.size()];
		for (int i = 0; i < deviceList.size(); i++) {
			devices[i] = deviceList.get(i);
		}
		return devices;

	}

	/**
	 * sets which of the connected devices to use
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
			String result = this.deviceLogic.setDevice(deviceNumber);
			if (result.equals("No device found")) {
				throw new WebApplicationException("No device found", Status.NOT_FOUND);
			}
			return result;
		} catch (NumberFormatException e) {
			if (this.deviceLogic.setDevice(deviceValue)) {
				return "Device: '" + deviceValue + "' is selected now";
			}
			throw new WebApplicationException("Device: '" + deviceValue + "' is not available", Status.CONFLICT);
		}
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
		List<Map<String, String>> actualSearchResult = null;
		switch (type.toLowerCase()) {
		case SpotifyConstants.TYPE_ARTIST:
			actualSearchResult = this.logic.search(searchText, SpotifyConstants.TYPE_ARTIST, limit);
			break;
		case SpotifyConstants.TYPE_PLAYLIST:
			actualSearchResult = this.logic.search(searchText, SpotifyConstants.TYPE_PLAYLIST, limit);
			break;
		case SpotifyConstants.TYPE_ALBUM:
			actualSearchResult = this.logic.search(searchText, SpotifyConstants.TYPE_ALBUM, limit);
			break;
		case SpotifyConstants.TYPE_TRACK:
			actualSearchResult = this.logic.search(searchText, SpotifyConstants.TYPE_TRACK, limit);
			break;
		default:
			
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
			@QueryParam("type") @DefaultValue("") String type, @QueryParam("limit") @DefaultValue("5") int limit) {
		switch (type.toLowerCase()) {
		case "user":
			Map<String, String> userPlaylist = this.logic.play(songNumber, SearchTypes.USER_PLAYLISTS);
			if (userPlaylist.isEmpty()) {
				throw new WebApplicationException("There is no user playlist available.", Status.CONFLICT);
			}
			return this.stringGenerator.generateSearchOutputString(userPlaylist);
		case "featured":
			Map<String, String> featuredPlaylist = this.logic.play(songNumber, SearchTypes.FEATURED_PLAYLISTS);
			if (featuredPlaylist.isEmpty()) {
				throw new WebApplicationException("There is no featured playlist available.", Status.CONFLICT);
			}
			return this.stringGenerator.generateSearchOutputString(featuredPlaylist);
		case "search":
			Map<String, String> searchResult = this.logic.play(songNumber, SearchTypes.NORMAL_SEARCH);
			if (searchResult.isEmpty()) {
				throw new WebApplicationException("There is no featured playlist available.", Status.CONFLICT);
			}
			return this.stringGenerator.generateSearchOutputString(searchResult);
		case SpotifyConstants.TYPE_TRACK:
			if (music != null) {
				List<Map<String, String>> searchList = this.logic.search(music.toString(), SpotifyConstants.TYPE_TRACK,
						limit);
				if (searchList.isEmpty()) {
					throw new WebApplicationException("No matching results found.", Status.CONFLICT);
				}
				this.musicEntity = new MusicEntity(music.getTitle(), music.getArtist());
				return this.stringGenerator
						.generateSearchOutputString(this.logic.play(songNumber, SearchTypes.NORMAL_SEARCH));
			}
			throw new WebApplicationException("Enter valid music information.", Status.CONFLICT);
		default:
			PlaylistEntity playlist = this.logic.play();
			if (playlist != null) {
				return playlist.toString();
			}
		}
		throw new WebApplicationException("Found nothing to play.", Status.CONFLICT);
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
		if (currentSong != null && currentSong.containsKey(SpotifyConstants.ITEM_NAME)
				&& currentSong.containsKey(SpotifyConstants.ARTIST_NAME)) {
			this.musicEntity = new MusicEntity(currentSong.get(SpotifyConstants.ITEM_NAME),
					currentSong.get(SpotifyConstants.ARTIST_NAME));
			return this.musicEntity;
		}
		throw new WebApplicationException("No song is currently playing", Status.CONFLICT);
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
	public PlaylistEntity[] getPlaylists(@PathParam("type") String type,
			@QueryParam("limit") @DefaultValue("5") int limit) {
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
		if (!pl.isEmpty()) {
			playlists = new PlaylistEntity[pl.size()];
			for (int i = 0; i < pl.size(); i++) {
				playlists[i] = new PlaylistEntity(pl.get(i).getName(), pl.get(i).getSongs(), pl.get(i).getUri(),
						pl.get(i).getImageUrl());
			}
			return playlists;
		}
		throw new WebApplicationException("No Playlists are available", Status.NOT_FOUND);
	}

	/**
	 * controls the volume of the player
	 * 
	 * @param volumeValue
	 *            allowed strings: mute, max, up, down, or a volume value between 0 and 100
	 * @return a int from 0-100. This represent the Volume in percent.
	 */
	@POST
	@Path("volume/{volumeValue}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setVolume(@PathParam("volumeValue") String volumeValue) {
		try {
			int volume = Integer.parseInt(volumeValue);
			if (volume < 0 || volume > 100) {
				throw new WebApplicationException("Incorrect volume value", Status.BAD_REQUEST);
			}
			this.logic.setVolume(volume);
			return String.valueOf(volume);
		} catch (NumberFormatException e) {
			if (!volumeValue.equals("mute") && !volumeValue.equals("max") && !volumeValue.equals("up")
					&& !volumeValue.equals("down")) {
				throw new WebApplicationException("Incorrect volume command", Status.BAD_REQUEST);
			}
			int volume = this.logic.setVolume(volumeValue);
			if (volume != -1) {
				return String.valueOf(volume);
			}
		}
		throw new WebApplicationException("Check player state", Status.CONFLICT);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}

	/**
	 * set the new name of the given device
	 * 
	 * @param deviceUri
	 *            the Uri from the device to change
	 * @param newName
	 * @return the deviceEntity with the new name or null if the Uri is not found in the registry
	 */
	@POST
	@Path("setDeviceName")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceEntity setDeviceName(
			@QueryParam("uri") @DefaultValue("") String uri, @QueryParam("newName") @DefaultValue("" )String newName) {
		DeviceEntity device = this.deviceLogic.setNewDeviceName(uri, newName);
		if(device != null) {
			return device;
		}
		throw new  WebApplicationException("No device with this uri", Status.NOT_FOUND);
	}
}
