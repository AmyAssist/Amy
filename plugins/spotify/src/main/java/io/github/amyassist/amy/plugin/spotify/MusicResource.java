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

package io.github.amyassist.amy.plugin.spotify;

import java.net.URI;
import java.util.List;

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

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.plugin.spotify.entities.*;
import io.github.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import io.github.amyassist.amy.plugin.spotify.logic.PlayerLogic;
import io.github.amyassist.amy.plugin.spotify.logic.Search;
import io.github.amyassist.amy.plugin.spotify.logic.SearchTypes;
import io.github.amyassist.amy.utility.rest.Resource;
import io.github.amyassist.amy.utility.rest.ResourceEntity;

/**
 * Rest Resource for music
 * 
 * @author Muhammed Kaya, Christian Bräuner, Lars Buttgereit
 */
@Path(MusicResource.PATH)
public class MusicResource implements Resource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "music";

	private static final String CHECK_PLAYER_STATE = "Check player state";
	private static final String NO_RESULTS_FOUND = "No results found";

	@Reference
	private PlayerLogic logic;

	@Reference
	private DeviceLogic deviceLogic;

	@Reference
	private Search search;

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
	 * this method allows to search for tracks
	 * 
	 * @param searchText
	 *            the text you want to search
	 * @param limit
	 *            how many results maximal searched for
	 * @return a array with TrackEntities
	 */
	@GET
	@Path("search/track/{searchText}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public TrackEntity[] searchTracks(@PathParam("searchText") String searchText,
			@QueryParam("limit") @DefaultValue("5") int limit) {
		List<TrackEntity> trackList = this.search.searchforTracks(searchText, limit);
		if (!trackList.isEmpty()) {
			return trackList.toArray(new TrackEntity[trackList.size()]);
		}
		throw new WebApplicationException(NO_RESULTS_FOUND, Status.NOT_FOUND);
	}

	/**
	 * this method allows to search for playlists
	 * 
	 * @param searchText
	 *            the text you want to search
	 * @param limit
	 *            how many results maximal searched for
	 * @return a array with PlaylistEntities
	 */
	@GET
	@Path("search/playlist/{searchText}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public PlaylistEntity[] searchPlaylists(@PathParam("searchText") String searchText,
			@QueryParam("limit") @DefaultValue("5") int limit) {
		List<PlaylistEntity> playlistList = this.search.searchforPlaylists(searchText, limit);
		if (!playlistList.isEmpty()) {
			return playlistList.toArray(new PlaylistEntity[playlistList.size()]);
		}
		throw new WebApplicationException(NO_RESULTS_FOUND, Status.NOT_FOUND);
	}

	/**
	 * this method allows to search for albums
	 * 
	 * @param searchText
	 *            the text you want to search
	 * @param limit
	 *            how many results maximal searched for
	 * @return a array with AlbumEntities
	 */
	@GET
	@Path("search/album/{searchText}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public AlbumEntity[] searchAlbums(@PathParam("searchText") String searchText,
			@QueryParam("limit") @DefaultValue("5") int limit) {
		List<AlbumEntity> albumList = this.search.searchforAlbums(searchText, limit);
		if (!albumList.isEmpty()) {
			return albumList.toArray(new AlbumEntity[albumList.size()]);
		}
		throw new WebApplicationException(NO_RESULTS_FOUND, Status.NOT_FOUND);
	}

	/**
	 * this method allows to search for artists
	 * 
	 * @param searchText
	 *            the text you want to search
	 * @param limit
	 *            how many results maximal searched for
	 * @return a array with ArtistEntities
	 */
	@GET
	@Path("search/artist/{searchText}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public ArtistEntity[] searchArtists(@PathParam("searchText") String searchText,
			@QueryParam("limit") @DefaultValue("5") int limit) {
		List<ArtistEntity> albumList = this.search.searchforArtists(searchText, limit);
		if (!albumList.isEmpty()) {
			return albumList.toArray(new ArtistEntity[albumList.size()]);
		}
		throw new WebApplicationException(NO_RESULTS_FOUND, Status.NOT_FOUND);
	}

	/**
	 * plays the playlist with the index from the search
	 * 
	 * @param playlistNumber
	 *            playlist number should be played
	 * @param type
	 *            where to play: allowed parameters: user (playlist), featured (playlist), search
	 * @return a PlaylistEntity
	 */
	@POST
	@Path("play/playlist")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public PlaylistEntity playPlaylist(@QueryParam("index") @DefaultValue("0") int playlistNumber,
			@QueryParam("type") @DefaultValue("") String type) {
		PlaylistEntity playlist;
		switch (type.toLowerCase()) {
		case "user":
			playlist = this.logic.playPlaylist(playlistNumber, SearchTypes.USER_PLAYLISTS);
			break;
		case "featured":
			playlist = this.logic.playPlaylist(playlistNumber, SearchTypes.FEATURED_PLAYLISTS);
			break;
		case "search":
			playlist = this.logic.playPlaylist(playlistNumber, SearchTypes.SEARCH_PLAYLISTS);
			break;
		default:
			throw new WebApplicationException("Found nothing to play.", Status.CONFLICT);
		}
		if (playlist != null) {
			return playlist;
		}
		throw new WebApplicationException("There is no playlist available with this number.", Status.NOT_FOUND);
	}

	/**
	 * plays the track with the index from the search
	 * 
	 * @param trackNumber
	 *            which track number should be played
	 * @return a TrackEntity
	 */
	@POST
	@Path("play/track")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public TrackEntity playTrack(@QueryParam("index") @DefaultValue("0") int trackNumber) {
		TrackEntity searchTrack = this.logic.playTrack(trackNumber);
		if (searchTrack != null) {
			return searchTrack;
		}
		throw new WebApplicationException("There is no track available with this number.", Status.NOT_FOUND);
	}

	/**
	 * plays the album with the index from the search
	 * 
	 * @param albumNumber
	 *            which album number should be played
	 * @return a AlbumEntity
	 */
	@POST
	@Path("play/album")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public AlbumEntity playAlbum(@QueryParam("index") @DefaultValue("0") int albumNumber) {
		AlbumEntity searchAlbum = this.logic.playAlbum(albumNumber);
		if (searchAlbum != null) {
			return searchAlbum;
		}
		throw new WebApplicationException("There is no album available with this number.", Status.NOT_FOUND);
	}

	/**
	 * plays the artist with the index from the search
	 * 
	 * @param artistNumber
	 *            which album number should be played
	 * @return a ArtistEntity
	 */
	@POST
	@Path("play/artist")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public ArtistEntity playArtist(@QueryParam("index") @DefaultValue("0") int artistNumber) {
		ArtistEntity searchArtist = this.logic.playArtist(artistNumber);
		if (searchArtist != null) {
			return searchArtist;
		}
		throw new WebApplicationException("There is no artist available with this number.", Status.NOT_FOUND);
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
		throw new WebApplicationException(CHECK_PLAYER_STATE, Status.CONFLICT);
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
		throw new WebApplicationException(CHECK_PLAYER_STATE, Status.CONFLICT);
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
		throw new WebApplicationException(CHECK_PLAYER_STATE, Status.CONFLICT);
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
		throw new WebApplicationException(CHECK_PLAYER_STATE, Status.CONFLICT);
	}

	/**
	 * returns the currently played music
	 * 
	 * @return the currently played music
	 */
	@GET
	@Path("currentSong")
	@Produces(MediaType.APPLICATION_JSON)
	public TrackEntity getCurrentSong() {
		TrackEntity currentSong = this.logic.getCurrentSong();
		if (currentSong != null) {
			return currentSong;
		}
		throw new WebApplicationException("No song is currently playing", Status.NOT_FOUND);
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
	@GET
	@Path("playlists/{type}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public PlaylistEntity[] getPlaylists(@PathParam("type") String type,
			@QueryParam("limit") @DefaultValue("5") int limit) {
		List<PlaylistEntity> pl;
		switch (type) {
		case "user":
			pl = this.search.searchOwnPlaylists(limit);
			break;
		case "featured":
			pl = this.search.searchFeaturedPlaylists(limit);
			break;
		default:
			pl = this.search.searchFeaturedPlaylists(limit);
			break;
		}
		if (!pl.isEmpty()) {
			return pl.toArray(new PlaylistEntity[pl.size()]);
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
		throw new WebApplicationException(CHECK_PLAYER_STATE, Status.CONFLICT);
	}

	/**
	 * @see io.github.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	public ResourceEntity getPluginDescripion() {
		return null;
	}

	/**
	 * set the new name of the given device
	 * 
	 * @param uri
	 *            the Uri from the device to change
	 * @param newName
	 *            new device name
	 * @return the deviceEntity with the new name or null if the Uri is not found in the registry
	 */
	@POST
	@Path("setDeviceName")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceEntity setDeviceName(@QueryParam("uri") @DefaultValue("") String uri,
			@QueryParam("newName") @DefaultValue("") String newName) {
		DeviceEntity device = this.deviceLogic.setNewDeviceName(uri, newName);
		if (device != null) {
			return device;
		}
		throw new WebApplicationException("No device with this uri", Status.NOT_FOUND);
	}

	/**
	 * get actual volume from 0-100, -1 if no volume available
	 * 
	 * @return the volume
	 */
	@GET
	@Path("getVolume")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVolume() {
		return String.valueOf(this.logic.getVolume());
	}

}
