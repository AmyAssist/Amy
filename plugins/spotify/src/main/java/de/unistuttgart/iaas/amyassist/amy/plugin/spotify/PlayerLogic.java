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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonParser;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.GetUsersAvailableDevicesRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SetVolumeForUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToNextTrackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToPreviousTrackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * TODO: This class have methods to control a spotify client from a user. For
 * examlpe play, pause playback or search for music tracks etc.
 * 
 * @author Lars Buttgereit
 */
@Service(PlayerLogic.class)
public class PlayerLogic {
	private Authorization auth;
	private String deviceID = null;
	private Search search;
	private int volume = 50;
	// private String deviceName = null;
	private ArrayList<String[]> actualSearchResult = null;

	public PlayerLogic() {
		init();
	}

	public void init() {
		this.auth = new Authorization();
		this.auth.init();
		setDevice(0);
		this.search = new Search(this.auth);
	}

	/**
	 * needed for the first init. need the clientID and the clientSecret form a
	 * spotify devloper account
	 * 
	 * @param clientID
	 * @param clientSecret
	 * @return login link to a personal spotify account
	 */
	public URI firstTimeInit(String clientID, String clientSecret) {
		this.auth.setClientID(clientID);
		this.auth.setClientSecret(clientSecret);
		return this.auth.authorizationCodeUri();
	}

	/**
	 * create the refresh token in he authorization object with the authCode
	 * 
	 * @param authCode
	 *            Callback from the login link
	 */
	public void inputAuthCode(String authCode) {
		this.auth.createRefreshToken(authCode);
		this.auth.init();
	}

	/**
	 * get all devices that logged in at the moment
	 * 
	 * @return empty ArrayList if no device available else the name of the
	 *         devices
	 */
	public ArrayList<String> getDevices() {

		ArrayList<String> deviceNames = new ArrayList<>();
		if (this.auth.getSpotifyApi() != null) {
			GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = this.auth.getSpotifyApi()
					.getUsersAvailableDevices().build();

			try {
				Device[] devices = getUsersAvailableDevicesRequest.execute();
				for (Device device : devices) {
					deviceNames.add(device.getName());
				}
			} catch (SpotifyWebApiException | IOException e) {
				System.err.println(e.getMessage());
			}

			return deviceNames;
		}
		System.err.println("please init the authorization object");
		return deviceNames;
	}

	/**
	 * set the given device as acutal active device for playing music
	 * 
	 * @param deviceNumber
	 *            index of the device array. Order is the same as in the output
	 *            in getDevices
	 * @return selected device
	 */
	public String setDevice(int deviceNumber) {
		if (this.auth.getSpotifyApi() != null) {
			GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = this.auth.getSpotifyApi()
					.getUsersAvailableDevices().build();
			try {
				Device[] devices = getUsersAvailableDevicesRequest.execute();
				if (deviceNumber < devices.length) {
					this.deviceID = devices[deviceNumber].getId();
					return devices[deviceNumber].getName();
				}
				return "This device was not found";
			} catch (SpotifyWebApiException | IOException e) {
				System.err.println(e.getMessage());
				return "A problem has occurred";
			}

		}
		return "Please init the spotify API";
	}

	/**
	 * this call the searchAnaything method in the Search class
	 * 
	 * @param searchText
	 * @param type
	 *            artist, track, playlist, album
	 * @param limit
	 *            how many results maximal searched for
	 * @return one output String with all results
	 */
	public String search(String searchText, String type, int limit) {
		if (checkPlayerState() == null) {
			this.actualSearchResult = this.search.SearchAnything(searchText, type, limit);
			String resultString = "";
			for (int i = 0; i < this.actualSearchResult.size(); i++) {
				resultString = resultString + "\n" + this.actualSearchResult.get(i)[1];
			}
			for (String[] result : this.actualSearchResult) {
				resultString = resultString + "\n" + result[1];
			}
			return resultString;
		} else {
			return checkPlayerState();
		}
	}

	/**
	 * this play method play a featured playlist from spotify
	 * 
	 * @return a string with the playlist name
	 */
	public String play() {
		if (checkPlayerState() == null) {
			ArrayList<String[]> playLists = this.search.getFeaturedPlaylists();
			if (playLists != null) {
				if (1 < this.search.getFeaturedPlaylists().size()) {
					playListFromUri(playLists.get(1)[0]);
					return playLists.get(1)[1];
				}
			}
			return "no featured playlist found";
		}
		System.err.println(checkPlayerState());
		return checkPlayerState();
	}

	/**
	 * this method play the item that searched before. Use only after a search
	 * 
	 * @param songNumber
	 *            number of the item form the search before
	 * @return
	 */
	public String play(int songNumber) {
		if (this.actualSearchResult != null && songNumber < this.actualSearchResult.size()) {
			if (this.actualSearchResult.get(songNumber)[2].equals("track")) {
				playSongFromUri(this.actualSearchResult.get(songNumber)[0]);
				return this.actualSearchResult.get(songNumber)[1];
			} else {
				playListFromUri(this.actualSearchResult.get(songNumber)[0]);
				return this.actualSearchResult.get(songNumber)[1];
			}
		} else {
			return "Please search before you call this or choose a smaller number";
		}
	}

	/**
	 * resume the actual playback
	 * 
	 * @return  a boolean. true if the command was executed, else if the command failed
	 */
	public boolean resume() {
		if (checkPlayerState() == null) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.auth.getSpotifyApi()
					.startResumeUsersPlayback().device_id(this.deviceID).build();
			try {
				startResumeUsersPlaybackRequest.execute();
				return true;
			} catch (SpotifyWebApiException | IOException e) {
				System.err.println("Error: " + e.getMessage());
				return false;
			}
		}
		System.err.println(checkPlayerState());
		return false;
		
	}

	/**
	 * pause the actual playback
	 * 
	 * @return  a boolean. true if the command was executed, else if the command failed
	 */
	public boolean pausePlayback() {
		if (checkPlayerState() == null) {
			PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.auth.getSpotifyApi().pauseUsersPlayback()
					.device_id(this.deviceID).build();
			try {
				pauseUsersPlaybackRequest.execute();
				return true;
			} catch (SpotifyWebApiException | IOException e) {
				System.out.println("Error: " + e.getMessage());
				return false;
			}
		}
		System.err.println(checkPlayerState());
		return false;
	}

	/**
	 * goes one song forward in the playlist or album
	 * 
	 * @return  a boolean. true if the command was executed, else if the command failed
	 */
	public boolean skip() {
		if (checkPlayerState() == null) {
			SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = this.auth.getSpotifyApi()
					.skipUsersPlaybackToNextTrack().device_id(this.deviceID).build();
			try {
				skipUsersPlaybackToNextTrackRequest.execute();
				return true;
			} catch (IOException | SpotifyWebApiException e) {
				System.err.println("Error: " + e.getMessage());
				return false;
			}
		}
		System.err.println(checkPlayerState());
		return false;
	}

	/**
	 * goes one song back in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean back() {
		if (checkPlayerState() == null) {

			SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = this.auth.getSpotifyApi()
					.skipUsersPlaybackToPreviousTrack().device_id(this.deviceID).build();
			try {
				skipUsersPlaybackToPreviousTrackRequest.execute();
				return true;
			} catch (IOException | SpotifyWebApiException e) {
				System.err.println("Error: " + e.getMessage());
				return false;
			}
		}
		System.err.println(checkPlayerState());
		return false;
	}

	/**
	 * gives the actual played song in the spotify client back
	 * 
	 * @return a hashMap with the keys name and artist
	 */
	public HashMap<String, String> getCurrentSong() {
		HashMap<String, String> result = new HashMap<>();
		if (checkPlayerState() == null) {
			GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest = this.auth
					.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build();
			try {
				CurrentlyPlayingContext currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest
						.execute();
				result.put("name", currentlyPlayingContext.getItem().getName());
				String artists = "";
				for (ArtistSimplified artist : currentlyPlayingContext.getItem().getArtists()) {
					artists = artists + artist.getName();

				}
				result.put("artist", artists);
				return result;
			} catch (SpotifyWebApiException | IOException e) {
				System.err.println("Error: " + e.getMessage());
				return null;
			}
		}
		System.err.println(checkPlayerState());
		return null;
	}

	/**
	 * this method controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down
	 * @return a int from 0-100. This represent the Volume in percent. if the Playerstate incorrect return -1
	 */
	public int setVolume(String volumeString) {
		if (checkPlayerState() == null) {
			switch (volumeString) {
			case "mute":
				setVolume(0);
				this.volume = 0;
				break;
			case "max":
				setVolume(100);
				break;
			case "up":
				if (this.volume + 10 <= 100) {
					this.volume += 10;
					setVolume(this.volume);
					
				}
				break;
			case "down":
				if (this.volume + 10 >= 0) {
					this.volume += 10;
					setVolume(this.volume);
					
				}
				break;
			default:
				System.err.println( "Incorrect volume command" );
				break;
			}
			return this.volume;
		}

		System.err.println(checkPlayerState());
		return -1;
	}

	/**
	 * set the volume from the remote spotify player
	 * 
	 * @param volume
	 */
	private void setVolume(int volume) {
		SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = this.auth.getSpotifyApi()
				.setVolumeForUsersPlayback(volume).device_id(this.deviceID).build();

		try {
			setVolumeForUsersPlaybackRequest.execute();
		} catch (SpotifyWebApiException | IOException e) {
			System.err.println("Error: " + e.getMessage());
		}

	}

	/**
	 * this method play the Song from the uri on spotify
	 * 
	 * @param uri
	 * @return true if no problem occur else false
	 */
	private boolean playSongFromUri(String uri) {
		StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.auth.getSpotifyApi()
				.startResumeUsersPlayback().device_id(this.deviceID)
				.uris(new JsonParser().parse("[\"" + uri + "\"]").getAsJsonArray()).build();
		try {
			startResumeUsersPlaybackRequest.execute();
			return true;
		} catch (SpotifyWebApiException | IOException e) {
			System.err.println("Error: " + e.getCause().getMessage());
			return false;
		}
	}

	/**
	 * play a list of tracks for example a playlists and albums
	 * 
	 * @param uri
	 * @return true if no problem occur else false
	 */
	private boolean playListFromUri(String uri) {
		StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.auth.getSpotifyApi()
				.startResumeUsersPlayback().context_uri(uri).device_id(this.deviceID).build();
		try {
			startResumeUsersPlaybackRequest.execute();
			return true;
		} catch (SpotifyWebApiException | IOException e) {
			System.err.println("Error: " + e.getCause().getMessage());
			return false;
		}
	}

	/**
	 * check all possible problems from the player
	 * 
	 * @return null if all good, else a String with a description of the problem
	 */
	private String checkPlayerState() {
		if (this.auth == null) {
			return "Please initialize the Authorization.";
		} else if (this.auth.getSpotifyApi() == null) {
			return "Please initialize the spotify api with the client id and client secret";
			// } else if (!getDevices().contains(this.deviceID)) {
			// return "the current device has been disconnected. Please select a
			// new
			// device.";
		}
		return null;
	}

}
