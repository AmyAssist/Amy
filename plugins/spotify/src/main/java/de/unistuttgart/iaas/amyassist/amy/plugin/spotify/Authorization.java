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
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.IOException;
import java.net.URI;
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
	
	private AuthorizationCodeCredentials authorizationCodeCredentials;
	private AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest;
	// Rules for the spotify user authentication e.g. access to the playcontrol
	private static final String SPOTIFY_RULES = "user-modify-playback-state,user-read-playback-state";
	
	private static final String SPOTIFY_CLIENTSECRET = "spotify_clientSecret";
	private static final String SPOTIFY_CLIENTID = "spotify_clientId";
	private static final String SPOTIFY_REFRSHTOKEN = "spotify_refreshToken";

	private ConfigLoader configLoader = new ConfigLoader();

	private boolean firstTime = true;
	
	private SpotifyApi spotifyApi = null;

	/**
	 * only succsefull if clientID and ClientSecret was written to file first
	 * 
	 * @return true if succeded, else false
	 */
	public boolean init() {
		if (configLoader.get(SPOTIFY_CLIENTID) != null && configLoader.get(SPOTIFY_CLIENTSECRET) != null) {
			this.clientID = configLoader.get(SPOTIFY_CLIENTID);
			this.clientSecret = configLoader.get(SPOTIFY_CLIENTSECRET);
			this.spotifyApi = new SpotifyApi.Builder().setClientId(this.clientID).setClientSecret(this.clientSecret)
					.setRedirectUri(this.redirectURI).build();
		} else {
			System.err.println("Client Secret and ID missing. Please insert the config file");
			return false;
		}

		if (configLoader.get(SPOTIFY_REFRSHTOKEN) != null) {
			this.refreshToken = configLoader.get(SPOTIFY_REFRSHTOKEN);
		} else {
			System.err.println("Please exec the Authorization first");
			return false;
		}
		return true;
	}

	/**
	 * create a authenitcation link for the User to authenticate his spotify
	 * account. The Link create a authenitcation code for the next step
	 * 
	 * @return
	 */
	public URI authorizationCodeUri() {
		init();
		AuthorizationCodeUriRequest authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri().state("TEST")
				.scope(SPOTIFY_RULES).show_dialog(true).build();
		final URI uri = authorizationCodeUriRequest.execute();
		return uri;
	}

	/**
	 * create a persistent refresh token with the authentifaction Code
	 * 
	 * @param authCode
	 *            from authorizationCodeUri()
	 */
	public void createRefreshToken(String authCode) {
		AuthorizationCodeRequest authorizationCodeRequest = this.spotifyApi.authorizationCode(authCode).build();
		try {
			AuthorizationCodeCredentials authorizationCodeCredentials1 = authorizationCodeRequest.execute();
			this.refreshToken = authorizationCodeCredentials1.getRefreshToken();
			this.configLoader.set(SPOTIFY_REFRSHTOKEN, refreshToken);
			System.out.println(refreshToken);
		} catch (SpotifyWebApiException | IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * in the first execution create this method a spotifyApi object and get these
	 * back, after the first execution the method checks the access token valid and
	 * refresh the access token if invalid
	 * 
	 * @return a spotifyAPI object for queries to the Spotify Web API
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
				System.err.println(e.getCause().getMessage());
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
				System.err.println(e.getCause().getMessage());
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
		this.configLoader.set(SPOTIFY_CLIENTID, clientID);
		this.clientID = clientID;
	}

	/**
	 * sets the ClientSecret from the spotify Web devloper account
	 * 
	 * @param clientSecret
	 */
	public void setClientSecret(String clientSecret) {
		this.configLoader.set(SPOTIFY_CLIENTSECRET, clientSecret);
		this.clientSecret = clientSecret;
	}

	/**
	 * needed for first init. It takes until the UI can control the authorization
	 * process. Probably after Sprint 2. Please follow the instruction the
	 * instruction in the console
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Authorization auth = new Authorization();
		auth.init();
		System.out.println("copy this link in your browser an follow the login process");
		System.out.println(auth.authorizationCodeUri());
		System.out.println(
				"Please insert Auth Code form the result link. Copy all between 'code=' and '&'. For example for this result: http://localhost:8888/?code=AQComIDO...qHvVw&state=TEST) you cope only this: 'AQComIDO...qHvVw'");
		auth.createRefreshToken(sc.nextLine());
		System.out.println("complete");
		sc.close();
	}

}
