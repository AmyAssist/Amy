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
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.PlayerLogic;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Parameter;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

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

	@Reference
	private PlayerLogic logic;

	@Context
	private UriInfo info;

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
		List<Map<String, String>> actualSearchResult;
		switch (type) {
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
		default:
			actualSearchResult = this.logic.search(searchText, SpotifyConstants.TYPE_TRACK, limit);
			break;
		}
		if (actualSearchResult != null) {
			return actualSearchResult;
		}
		throw new WebApplicationException("No results found", Status.NO_CONTENT);
	}

	/**
	 * This method plays the item that searched before. Use only after a search/refresh
	 * 
	 * @param music
	 *            the music to be played
	 * @param songNumber
	 *            which song or playlist number should be played
	 * @param type
	 *            where to play: allowed parameters: user (playlist), featured (playlist), track
	 * @param limit
	 *            limit of returned tracks or playlists
	 * @return the playing music if there is one
	 */
	@POST
	@Path("play")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String play(MusicEntity music, @QueryParam("songNumber") @DefaultValue("0") int songNumber,
			@QueryParam("type") @DefaultValue("") String type, @QueryParam("limit") @DefaultValue("5") int limit) {
		switch (type) {
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
	 * goes one song forward in the playlist or album
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
	 * goes one song back in the playlist or album
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
	 * returns the currently playing music
	 * 
	 * @return the currently playing music
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
	 * set the new name of the given device
	 * 
	 * @param uri
	 *            the uri from the device to change
	 * @param newName
	 *            the new name of the device
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
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceEntity getPluginDescripion() {
		ResourceEntity resource = new ResourceEntity();
		resource.setName("Spotify Plugin");
		resource.setDescription(
				"A Plugin to control a spotify client from a user. Provides player and search functions for music and"
						+ " playlists and to show and set devices to use");
		resource.setMethods(this.getPluginMethods());
		resource.setLink(this.info.getBaseUriBuilder().path(MusicResource.class).build());
		return resource;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] getPluginMethods() {
		Method[] methods = new Method[14];
		methods[0] = createFirstTimeInitMethod();
		methods[1] = createInputAuthCodeMethod();
		methods[2] = createGetDevicesMethod();
		methods[3] = createSetDeviceMethod();
		methods[4] = createSearchMethod();
		methods[5] = createPlayMethod();
		methods[6] = createResumeMethod();
		methods[7] = createPauseMethod();
		methods[8] = createSkipMethod();
		methods[9] = createBackMethod();
		methods[10] = createGetCurrentSongMethod();
		methods[11] = createGetPlaylistsMethod();
		methods[12] = createSetVolumeMethod();
		methods[13] = createSetDeviceNameMethod();
		return methods;
	}

	/**
	 * returns the method describing the init method
	 * 
	 * @return the describing method object
	 */
	@Path("init")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createFirstTimeInitMethod() {
		Method init = new Method();
		init.setName("Initialize Account");
		init.setDescription("Needed for the first init to access spotify functions");
		init.setLink(this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "firstTimeInit")
				.build());
		init.setType(Types.POST);
		init.setParameters(getFirstTimeInitParameters());
		return init;
	}

	/**
	 * returns the method describing the inputAuthCode method
	 * 
	 * @return the describing method object
	 */
	@Path("token/{authCode}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createInputAuthCodeMethod() {
		Method auth = new Method();
		auth.setName("Refresh Token");
		auth.setDescription("Needed to create the refresh token in the authorization object");
		auth.setLink(this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "inputAuthCode")
				.build());
		auth.setType(Types.POST);
		auth.setParameters(getInputAuthCodeParameters());
		return auth;
	}

	/**
	 * returns the method describing the getDevices method
	 * 
	 * @return the describing method object
	 */
	@Path("getDevices")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetDevicesMethod() {
		Method getDev = new Method();
		getDev.setName("Get Devices");
		getDev.setDescription("Returns all devices that logged in at the moment");
		getDev.setLink(this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "getDevices")
				.build());
		getDev.setType(Types.GET);
		return getDev;
	}

	/**
	 * returns the method describing the setDevice method
	 * 
	 * @return the describing method object
	 */
	@Path("setDevice/{deviceValue}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createSetDeviceMethod() {
		Method setDev = new Method();
		setDev.setName("Set Device");
		setDev.setDescription("Sets which of the connected devices to use");
		setDev.setLink(
				this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "setDevice").build());
		setDev.setType(Types.POST);
		setDev.setParameters(getSetDeviceParameters());
		return setDev;
	}

	/**
	 * returns the method describing the search method
	 * 
	 * @return the describing method object
	 */
	@Path("search/{searchText}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createSearchMethod() {
		Method search = new Method();
		search.setName("Search");
		search.setDescription("Used to search artist, track, playlist and album names");
		search.setLink(
				this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "search").build());
		search.setType(Types.POST);
		search.setParameters(getSearchParameters());
		return search;
	}

	/**
	 * returns the method describing the play method
	 * 
	 * @return the describing method object
	 */
	@Path("play")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createPlayMethod() {
		Method play = new Method();
		play.setName("Play");
		play.setDescription("Used to play the item that searched before");
		play.setLink(this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "play").build());
		play.setType(Types.POST);
		play.setParameters(getPlayParameters());
		return play;
	}

	/**
	 * returns the method describing the resume method
	 * 
	 * @return the describing method object
	 */
	@Path("resume")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createResumeMethod() {
		Method resume = new Method();
		resume.setName("Resume");
		resume.setDescription("Used to resume the actual playback");
		resume.setLink(
				this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "resume").build());
		resume.setType(Types.POST);
		return resume;
	}

	/**
	 * returns the method describing the pause method
	 * 
	 * @return the describing method object
	 */
	@Path("pause")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createPauseMethod() {
		Method pause = new Method();
		pause.setName("Pause");
		pause.setDescription("Used to pause the actual playback");
		pause.setLink(
				this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "pause").build());
		pause.setType(Types.POST);
		return pause;
	}

	/**
	 * returns the method describing the skip method
	 * 
	 * @return the describing method object
	 */
	@Path("skip")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createSkipMethod() {
		Method skip = new Method();
		skip.setName("Skip");
		skip.setDescription("Used to go one song forward in the playlist or album");
		skip.setLink(this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "skip").build());
		skip.setType(Types.POST);
		return skip;
	}

	/**
	 * returns the method describing the back method
	 * 
	 * @return the describing method object
	 */
	@Path("back")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createBackMethod() {
		Method back = new Method();
		back.setName("Back");
		back.setDescription("Used to go one song back in the playlist or album");
		back.setLink(this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "back").build());
		back.setType(Types.POST);
		return back;
	}

	/**
	 * returns the method describing the currentSong method
	 * 
	 * @return the describing method object
	 */
	@Path("currentSong")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetCurrentSongMethod() {
		Method song = new Method();
		song.setName("Currently playing Song");
		song.setDescription("Used to give the actual playing song in the spotify client back");
		song.setLink(this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "getCurrentSong")
				.build());
		song.setType(Types.GET);
		return song;
	}

	/**
	 * returns the method describing the getPlaylists method
	 * 
	 * @return the describing method object
	 */
	@Path("playlists/{type}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetPlaylistsMethod() {
		Method getList = new Method();
		getList.setName("Get Playlists");
		getList.setDescription("Used to get user or featured playlists");
		getList.setLink(this.info.getBaseUriBuilder().path(MusicResource.class)
				.path(MusicResource.class, "getPlaylists").build());
		getList.setType(Types.POST);
		getList.setParameters(getGetPlaylistsParameters());
		return getList;
	}

	/**
	 * returns the method describing the setVolume method
	 * 
	 * @return the describing method object
	 */
	@Path("volume/{volumeValue}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createSetVolumeMethod() {
		Method volume = new Method();
		volume.setName("Set Volume");
		volume.setDescription("Used to control the volume of the player");
		volume.setLink(
				this.info.getBaseUriBuilder().path(MusicResource.class).path(MusicResource.class, "setVolume").build());
		volume.setType(Types.POST);
		volume.setParameters(getSetVolumeParameters());
		return volume;
	}

	/**
	 * returns the method describing the setDeviceName method
	 * 
	 * @return the describing method object
	 */
	@Path("setDeviceName")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createSetDeviceNameMethod() {
		Method devName = new Method();
		devName.setName("Set Device Name");
		devName.setDescription("Used to set the a new name for a given device");
		devName.setLink(this.info.getBaseUriBuilder().path(MusicResource.class)
				.path(MusicResource.class, "setDeviceName").build());
		devName.setType(Types.POST);
		devName.setParameters(getSetDeviceNameParameters());
		return devName;
	}

	private Parameter[] getFirstTimeInitParameters() {
		Parameter[] params = new Parameter[2];
		// clientID
		params[0] = new Parameter();
		params[0].setName("Client ID");
		params[0].setRequired(true);
		params[0].setParamType(Types.QUERY);
		params[0].setValueType(Types.STRING);
		// clientSecret
		params[1] = new Parameter();
		params[1].setName("Client Secret");
		params[1].setRequired(true);
		params[1].setParamType(Types.QUERY);
		params[1].setValueType(Types.STRING);
		return params;
	}

	private Parameter[] getInputAuthCodeParameters() {
		Parameter[] params = new Parameter[1];
		// authCode
		params[0] = new Parameter();
		params[0].setName("Authorization Code");
		params[0].setRequired(true);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.STRING);
		return params;
	}

	private Parameter[] getSetDeviceParameters() {
		Parameter[] params = new Parameter[1];
		// deviceValue
		params[0] = new Parameter();
		params[0].setName("Number or ID of Device");
		params[0].setRequired(true);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.STRING);
		return params;
	}

	private Parameter[] getSearchParameters() {
		Parameter[] params = new Parameter[3];
		// searchText
		params[0] = new Parameter();
		params[0].setName("SearchText");
		params[0].setRequired(true);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.STRING);
		// type
		params[1] = new Parameter();
		params[1].setName("Type");
		params[1].setRequired(true);
		params[1].setParamType(Types.QUERY);
		params[1].setValueType(Types.STRING);
		// limit
		params[2] = new Parameter();
		params[2].setName("Limit");
		params[2].setRequired(true);
		params[2].setParamType(Types.QUERY);
		params[2].setValueType(Types.INTEGER);
		return params;
	}

	private Parameter[] getPlayParameters() {
		Parameter[] params = new Parameter[3];
		// music
		params[0] = new Parameter();
		params[0].setName("Music");
		params[0].setRequired(false);
		params[0].setParamType(Types.BODY);
		params[0].setValueType(Types.BODY); // TODO music type
		// songNumber
		params[1] = new Parameter();
		params[1].setName("Song Number");
		params[1].setRequired(true);
		params[1].setParamType(Types.QUERY);
		params[1].setValueType(Types.INTEGER);
		// type
		params[2] = new Parameter();
		params[2].setName("Type");
		params[2].setRequired(true);
		params[2].setParamType(Types.QUERY);
		params[2].setValueType(Types.STRING);
		// limit
		params[3] = new Parameter();
		params[3].setName("Limit");
		params[3].setRequired(true);
		params[3].setParamType(Types.QUERY);
		params[3].setValueType(Types.INTEGER);
		return params;
	}

	private Parameter[] getGetPlaylistsParameters() {
		Parameter[] params = new Parameter[2];
		// type
		params[0] = new Parameter();
		params[0].setName("Type");
		params[0].setRequired(true);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.STRING);
		// limit
		params[1] = new Parameter();
		params[1].setName("Limit");
		params[1].setRequired(true);
		params[1].setParamType(Types.QUERY);
		params[1].setValueType(Types.INTEGER);
		return params;
	}

	private Parameter[] getSetVolumeParameters() {
		Parameter[] params = new Parameter[1];
		// volumeValue
		params[0] = new Parameter();
		params[0].setName("Volume");
		params[0].setRequired(true);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.STRING);
		return params;
	}

	private Parameter[] getSetDeviceNameParameters() {
		Parameter[] params = new Parameter[2];
		// uri
		params[0] = new Parameter();
		params[0].setName("URI");
		params[0].setRequired(true);
		params[0].setParamType(Types.QUERY);
		params[0].setValueType(Types.STRING);
		// newName
		params[1] = new Parameter();
		params[1].setName("New Name");
		params[1].setRequired(true);
		params[1].setParamType(Types.QUERY);
		params[1].setValueType(Types.STRING);
		return params;
	}

}
