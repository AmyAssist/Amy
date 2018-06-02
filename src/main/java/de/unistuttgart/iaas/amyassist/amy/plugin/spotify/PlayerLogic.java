/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.JsonParser;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.requests.data.player.GetUsersAvailableDevicesRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SetVolumeForUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToNextTrackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToPreviousTrackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Init;

/**
 * TODO: This class have methods to control a spotify client from a user. For examlpe play, pause playback or search for music tracks etc.
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

	@Init
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
		this.auth = new Authorization();
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
	 * @return empty ArrayList if no device available else the name of the devices
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
				System.err.println(e);
				e.printStackTrace();
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
	 *            index of the device array. Order is the same as in the output in
	 *            getDevices
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
				e.printStackTrace();
				System.err.println(e);
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
	 * @return
	 */
	public String play() {
		ArrayList<String[]> playLists = this.search.getFeaturedPlaylists();
		if (playLists != null) {
			if (1 < this.search.getFeaturedPlaylists().size()) {
				playListFromUri(playLists.get(1)[0]);
				return playLists.get(1)[1];
			}
		}
		return "no featured playlist found";
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
	 * @return short String about the execute action
	 */
	public String resume() {
		if (checkPlayerState() == null) {
			StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.auth.getSpotifyApi()
					.startResumeUsersPlayback().device_id(this.deviceID).build();
			try {
				startResumeUsersPlaybackRequest.execute();
				return "Playback resume";
			} catch (SpotifyWebApiException | IOException e) {
				System.out.println("Error: " + e.getMessage());
				return "A problem occur";
			}
		}
		return checkPlayerState();
	}

	/**
	 * pause the actual playback
	 * 
	 * @return short String about the execute action
	 */
	public String pausePlayback() {
		if (checkPlayerState() == null) {
			PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.auth.getSpotifyApi().pauseUsersPlayback()
					.device_id(this.deviceID).build();
			try {
				pauseUsersPlaybackRequest.execute();
				return "Playback pause";
			} catch (SpotifyWebApiException | IOException e) {
				System.out.println("Error: " + e.getMessage());
				return "A problem occur";
			}
		}
		return checkPlayerState();
	}

	/**
	 * goes one song forward in the playlist or album
	 * 
	 * @return short String about the execute action
	 */
	public String skip() {
		if (checkPlayerState() == null) {
			SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = this.auth.getSpotifyApi()
					.skipUsersPlaybackToNextTrack().device_id(this.deviceID).build();
			try {
				skipUsersPlaybackToNextTrackRequest.execute();
				return "Playback skip";
			} catch (IOException | SpotifyWebApiException e) {
				System.out.println("Error: " + e.getMessage());
				return "A problem occur";
			}
		}
		return checkPlayerState();
	}

	/**
	 * goes one song back in the playlist or album
	 * 
	 * @return short String about the execute action
	 */
	public String back() {
		if (checkPlayerState() == null) {

			SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = this.auth.getSpotifyApi()
					.skipUsersPlaybackToPreviousTrack().device_id(this.deviceID).build();
			try {
				skipUsersPlaybackToPreviousTrackRequest.execute();
				return "Playback back";
			} catch (IOException | SpotifyWebApiException e) {
				System.out.println("Error: " + e.getMessage());
				return "A problem occur";
			}
		}
		return checkPlayerState();
	}

	/**
	 * this method controls the volume of the player
	 * @param volumeString allowed strings: mute, max, up, down
	 * @return
	 */
	public String setVolume(String volumeString) {
		if (checkPlayerState() == null) {
			switch (volumeString) {
			case "mute":
				setVolume(0);
				return "Volume mute";
			case "max":
				setVolume(100);
				return "Volume max";
			case "up":
				if(this.volume + 10 <= 100) {
					this.volume += 10;
					setVolume(this.volume);
					return "Volume on " + this.volume + " percent";
				}
				return "Volume on 100 percent";
			case "down":
				if(this.volume + 10 >= 0) {
					this.volume += 10;
					setVolume(this.volume);
					return "Volume on " + this.volume + " percent";
				}
				return "Volume on 0 percent";
				
			default:
				return "Incorrect volume command";
			}
		}

		return checkPlayerState();
	}

	/**
	 * set the volume from the remote spotify player
	 * @param volume
	 */
	private void setVolume(int volume) {
		SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = this.auth.getSpotifyApi()
				.setVolumeForUsersPlayback(volume).device_id(this.deviceID).build();

		try {
			setVolumeForUsersPlaybackRequest.execute();
		} catch (SpotifyWebApiException | IOException e) {
			System.out.println("Error: " + e.getMessage());
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
			System.out.println("Error: " + e.getCause().getMessage());
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
			System.out.println("Error: " + e.getCause().getMessage());
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
			// return "the current device has been disconnected. Please select a new
			// device.";
		}
		return null;
	}

	// testing
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String clientId;
		String clientSecret;
		PlayerLogic pc = new PlayerLogic();
		System.out.println("first? (y/n)");
		if (sc.nextLine().equals("y")) {
			System.out.println("Input ClientId:");
			clientId = sc.nextLine();
			System.out.println("Input ClientSecret");
			clientSecret = sc.nextLine();
			System.out.println("Copy this to your browser:");
			System.out.println(pc.firstTimeInit(clientId, clientSecret));
			System.out.println("copy result code from browser in the console");
			pc.inputAuthCode(sc.nextLine());
		} else {
			pc.init();
		}
		pc.getDevices();
		for (int i = 0; i < pc.getDevices().size(); i++) {
			System.out.println(pc.getDevices().get(i));
		}
		// System.out.println(pc.search("game of thrones", "playlist", 5));
		// System.out.println(pc.play(2));
		System.out.println(pc.play());
		pc.pausePlayback();
		pc.resume();
		pc.skip();
		pc.back();
		pc.setVolume("max");
		pc.setVolume("mute");
		
		sc.close();
	}
}
