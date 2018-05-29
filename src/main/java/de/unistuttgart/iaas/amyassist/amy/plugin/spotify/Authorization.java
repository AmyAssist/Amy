/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Scanner;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

/**
 * TODO: Description
 * 
 * @author Lars Buttgereit
 */
public class Authorization {

	private String clientID = null;
	private String clientSecret = null;
	private final URI redirectURI = SpotifyHttpManager.makeUri("http://localhost:8888");
	private String refreshToken = null;
	private SpotifyApi spotifyApi = null;
	private AuthorizationCodeCredentials authorizationCodeCredentials;
	private AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest;
	// Rules for the spotify user authentication e.g. access to the playcontrol
	private final String SPOTIFY_RULES = "user-modify-playback-state,user-read-playback-state";
	// help variable for export/import the different ids
	private HashMap<String, String> idExport = new HashMap<>();
	private boolean firstTime = true;

	/**
	 * only succsefull if clientID and ClientSecret was written to file first
	 * 
	 * @return true if succeded, else false
	 */
	public boolean init() {
		try {
			InputStream fis = null;
			File tmp = new File(System.getProperty("java.io.tmpdir"));
			File f = new File(tmp, "/spotifyAuth/auth.auth");

			fis = new FileInputStream(f);
			ObjectInputStream stream = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			HashMap<String, String> readObject = ((HashMap<String, String>) stream.readObject());
			this.idExport = readObject;
			//cheks if all necessary IDs in file
			if (this.idExport.get("clientID") != null && this.idExport.get("clientSecret") != null) {
				this.clientID = this.idExport.get("clientID");
				this.clientSecret = this.idExport.get("clientSecret");
				this.spotifyApi = new SpotifyApi.Builder().setClientId(this.clientID).setClientSecret(this.clientSecret)
						.setRedirectUri(this.redirectURI).build();

			} else {
				fis.close();
				return false;
			}
			if (this.idExport.get("refresh") != null) {
				this.refreshToken = this.idExport.get("refresh");
			}
			fis.close();
			return true;

		} catch (FileNotFoundException e) {
			System.err.println("Please set ClientID and ClientSecret");
			return false;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Authentifcation error. " + e);
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * create a authenitcation link for the User to authenticate his spotify account. The Link create a authenitcation code for the next step
	 * @return
	 */
	public URI authorizationCodeUri() {
		init();
		AuthorizationCodeUriRequest authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri().state("TEST")
				.scope(this.SPOTIFY_RULES).show_dialog(true).build();
		final URI uri = authorizationCodeUriRequest.execute();
		return uri;
	}
	
	/**
	 * create a persistent refresh token with the authentifaction Code
	 * @param authCode from authorizationCodeUri()
	 */
	public void createRefreshToken(String authCode) {
		AuthorizationCodeRequest authorizationCodeRequest = this.spotifyApi.authorizationCode(authCode).build();
		try {
			AuthorizationCodeCredentials authorizationCodeCredentials1 = authorizationCodeRequest.execute();
			this.refreshToken = authorizationCodeCredentials1.getRefreshToken();
			this.idExport.put("refresh", this.refreshToken);
			writeToFile();
		} catch (SpotifyWebApiException | IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * write alle Token and IDs to file in the temp folder of the system. Only for testing without persistent storage in Core
	 */
	private void writeToFile() {
		try {
			File tmp = new File(System.getProperty("java.io.tmpdir"));
			File f = new File(tmp, "/spotifyAuth/auth.auth");
			f.delete();
			f.getParentFile().mkdirs();
			FileOutputStream fos;
			fos = new FileOutputStream(f);
			ObjectOutputStream stream = new ObjectOutputStream(fos);
			stream.writeObject(this.idExport);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * in the first execution create this method a spotifyApi object and get these back, after the first execution the method checks the access token valid and refresh the access token if invalid 
	 *@return a spotifyAPI object for queries to the Spotify Web API
	 */
	public SpotifyApi getSpotifyApi() {
		if (this.firstTime) {
			try {
				if (this.clientID != null && this.clientSecret != null && this.refreshToken != null) {
					this.spotifyApi = new SpotifyApi.Builder().setClientId(this.clientID)
							.setClientSecret(this.clientSecret).setRefreshToken(this.refreshToken).build();
					this.authorizationCodeRefreshRequest = this.spotifyApi.authorizationCodeRefresh().build();
					this.authorizationCodeCredentials = this.authorizationCodeRefreshRequest.execute();
					this.spotifyApi.setAccessToken(this.authorizationCodeCredentials.getAccessToken());
				} else {
					return null;
				}
			} catch (SpotifyWebApiException | IOException e) {
				return null;
			}
			this.firstTime = false;
			return this.spotifyApi;
		} else if (this.authorizationCodeCredentials.getExpiresIn().intValue() > 120) {
			return this.spotifyApi;
		} else {
			try {

				this.authorizationCodeCredentials = this.authorizationCodeRefreshRequest.execute();
				this.spotifyApi.setAccessToken(this.authorizationCodeCredentials.getAccessToken());

			} catch (SpotifyWebApiException | IOException e) {
				System.out.println("Error: " + e.getMessage());
				return null;
			}
			return this.spotifyApi;
		}
	}

	/**
	 * sets the ClientID from the spotify Web devloper account
	 * 
	 * @param clientID
	 */
	public void setClientID(String clientID) {
		this.clientID = clientID;
		this.idExport.put("clientID", clientID);

		writeToFile();

	}

	/**
	 * sets the ClientSecret from the spotify Web devloper account
	 * 
	 * @param clientSecret
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		this.idExport.put("clientSecret", clientSecret);
		writeToFile();
	}

	// test without UI or speech
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Authorization auth = new Authorization();
		//insert here ClientID and ClientSecret for testing
		auth.setClientID("");
		auth.setClientSecret("");
		auth.init();
		System.out.println(auth.authorizationCodeUri());
		System.out.println("Please insert Auth Code");
		auth.createRefreshToken(sc.nextLine());
		System.out.println(auth.getSpotifyApi().getAccessToken());
		sc.close();

	}
}
