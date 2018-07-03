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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;

import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.MusicEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Playlist;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of music
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
class MusicRestTest {

	@Reference
	private TestFramework testFramework;

	private StringGenerator stringGenerator;
	private PlayerLogic logic;
	private MusicEntity[] userMusics;
	private MusicEntity[] featuredMusics;
	private List<Playlist> userPlaylists;
	private List<Playlist> featuredPlaylists;

	private WebTarget target;

	/**
	 * 
	 */
	@BeforeEach
	public void setUp() {
		this.testFramework.setRESTResource(MusicResource.class);
		this.stringGenerator = this.testFramework.mockService(StringGenerator.class);
		this.logic = this.testFramework.mockService(PlayerLogic.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);

		createSongAndPlaylist();
	}

	/**
	 * creates song and playlist for testing
	 */
	private void createSongAndPlaylist() {
		HashMap<String, String> currentSong = new HashMap<>();
		currentSong.put("title", "Say Something");
		currentSong.put("artist", "Justin Timberlake");
		Mockito.when(this.logic.getCurrentSong()).thenReturn(currentSong);

		MusicEntity musicEntity1 = new MusicEntity(currentSong.get("title"), currentSong.get("artist"));
		MusicEntity musicEntity2 = new MusicEntity("Flames", "David Guetta");
		MusicEntity musicEntity3 = new MusicEntity("Holz", "Hans Dieter");

		this.userMusics = new MusicEntity[2];
		this.userMusics[0] = musicEntity1;
		this.userMusics[1] = musicEntity2;

		this.featuredMusics = new MusicEntity[2];
		this.featuredMusics[0] = musicEntity2;
		this.featuredMusics[1] = musicEntity3;

		Playlist myFirstPlaylist = new Playlist("myFirstPlaylist", this.userMusics, "test123", "image.com");
		Playlist mySecondPlaylist = new Playlist("mySecondPlaylist", this.featuredMusics, "test456", "picture.com");
		Playlist featuredPlaylist1 = new Playlist("featuredPlaylist", this.featuredMusics, "test789", "cover.com");

		this.userPlaylists = new ArrayList<>();
		this.userPlaylists.add(myFirstPlaylist);
		this.userPlaylists.add(mySecondPlaylist);

		this.featuredPlaylists = new ArrayList<>();
		this.featuredPlaylists.add(featuredPlaylist1);
	}

	/**
	 * creates List with device examples for testing
	 * 
	 * @return an example device list
	 */
	private List<de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Device> createDeviceList() {
		List<de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Device> devicesList = new ArrayList<>();
		devicesList.add(
				new de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Device("Smartphone", "Hello", "abc123"));
		devicesList.add(
				new de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Device("Computer", "Goodbye", "123abc"));
		return devicesList;
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#firstTimeInit(String, String)}.
	 */
	@Test
	void testFirstTimeInit() {
		URI uri = URI.create("http://example.com");

		Mockito.when(this.logic.firstTimeInit("HansDieter", "horst123")).thenReturn(uri);
		Response response = this.target.path("music").path("init").queryParam("clientID", "HansDieter")
				.queryParam("clientSecret", "horst123").request().post(null);
		URI actual = response.readEntity(URI.class);
		assertThat(actual, is(uri));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).firstTimeInit("HansDieter", "horst123");

		Mockito.when(this.logic.firstTimeInit("abc123", "123abc")).thenReturn(null);
		response = this.target.path("music").path("init").queryParam("clientID", "abc123")
				.queryParam("clientSecret", "123abc").request().post(null);
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Enter valid client information"));
		assertThat(response.getStatus(), is(409));
		Mockito.verify(this.logic).firstTimeInit("abc123", "123abc");

		Mockito.when(this.logic.firstTimeInit()).thenReturn(uri);
		response = this.target.path("music").path("init").queryParam("clientID", null).queryParam("clientSecret", null)
				.request().post(null);
		actual = response.readEntity(URI.class);
		assertThat(actual, is(uri));
		assertThat(response.getStatus(), is(200));

		response = this.target.path("music").path("init").request().post(null);
		actual = response.readEntity(URI.class);
		assertThat(actual, is(uri));
		assertThat(response.getStatus(), is(200));

		Mockito.when(this.logic.firstTimeInit()).thenReturn(null);
		response = this.target.path("music").path("init").request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Check your property location"));
		assertThat(response.getStatus(), is(409));
		verify(this.logic, Mockito.times(3)).firstTimeInit();

	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#inputAuthCode(String)}.
	 */
	@Test
	void testInputAuthCode() {
		Response response = this.target.path("music").path("token/abc").request().post(null);
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Token created."));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).inputAuthCode("abc");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#getDevices()}.
	 */
	@Test
	void testGetDevices() {
		Mockito.when(this.logic.getDevices()).thenReturn(createDeviceList());

		Response response = this.target.path("music").path("getDevices").request().get();

		assertThat(response.readEntity(String.class),
				is("[{\"type\":\"Smartphone\",\"name\":\"Hello\",\"id\":\"abc123\"},"
						+ "{\"type\":\"Computer\",\"name\":\"Goodbye\",\"id\":\"123abc\"}]"));
		assertThat(response.getStatus(), is(200));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#setDevice(int)}.
	 */
	@Test
	void testSetDevice() {
		Mockito.when(this.logic.setDevice(0)).thenReturn("Hello");
		Response response = this.target.path("music").path("setDevice/0").request().post(null);
		assertThat(response.readEntity(String.class), is("Hello"));
		assertThat(response.getStatus(), is(200));
		verify(this.logic).setDevice(0);

		Mockito.when(this.logic.setDevice(2)).thenReturn("No device found");
		response = this.target.path("music").path("setDevice/2").request().post(null);
		assertThat(response.readEntity(String.class), is("No device found"));
		assertThat(response.getStatus(), is(404));
		verify(this.logic).setDevice(2);

		Mockito.when(this.logic.setDevice("abc123")).thenReturn(true);
		response = this.target.path("music").path("setDevice/abc123").request().post(null);
		assertThat(response.readEntity(String.class), is("Device: 'abc123' is selected now"));
		assertThat(response.getStatus(), is(200));
		verify(this.logic).setDevice("abc123");

		Mockito.when(this.logic.setDevice("xyz789")).thenReturn(false);
		response = this.target.path("music").path("setDevice/xyz789").request().post(null);
		assertThat(response.readEntity(String.class), is("Device: 'xyz789' is not available"));
		assertThat(response.getStatus(), is(409));
		verify(this.logic).setDevice("xyz789");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#getCurrentSong()}.
	 */
	@Test
	void testGetCurrentSong() {
		Response response = this.target.path("music").path("currentSong").request().get();

		assertThat(response.readEntity(String.class),
				is("{\"artist\":\"Justin Timberlake\",\"title\":\"Say Something\"}"));
		assertThat(response.getStatus(), is(200));

		Mockito.when(this.logic.getCurrentSong()).thenReturn(null);
		response = this.target.path("music").path("currentSong").request().get();
		assertThat(response.readEntity(String.class), is("No song is currently playing"));
		assertThat(response.getStatus(), is(409));
		verify(this.logic, Mockito.times(2)).getCurrentSong();
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#search(String, String, int)}.
	 */
	@Test
	void testSearch() {
		List<Map<String, String>> searchList = new ArrayList<>();
		HashMap<String, String> entry = new HashMap<>();
		entry.put("artist", "David Guetta");
		entry.put("track", "Flames");
		entry.put("album", "albumName");
		entry.put("playlist", "playlistName");
		searchList.add(0, entry);

		Mockito.when(this.logic.search("Flames", "track", 1)).thenReturn(searchList);
		Response response = this.target.path("music").path("search/Flames").queryParam("type", "track")
				.queryParam("limit", 1).request().post(null);
		List<Map<String, String>> actual = response.readEntity(List.class);
		assertThat(actual, is(searchList));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).search("Flames", "track", 1);

		Mockito.when(this.logic.search("Flames", "track", 5)).thenReturn(searchList);
		response = this.target.path("music").path("search/Flames").request().post(null);
		actual = response.readEntity(List.class);
		assertThat(actual, is(searchList));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).search("Flames", "track", 5);

		Mockito.when(this.logic.search("abc", "artist", 1)).thenReturn(null);
		response = this.target.path("music").path("search/abc").queryParam("type", "artist").queryParam("limit", 1)
				.request().post(null);
		actual = response.readEntity(List.class);
		String nullString = null;
		assertThat(actual, is(nullString));
		assertThat(response.getStatus(), is(204));
		Mockito.verify(this.logic).search("abc", "artist", 1);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#play(MusicEntity, int, String, int)}.
	 */
	@Test
	void testPlay() {
		Entity<MusicEntity> entity = Entity.entity(this.userMusics[0], MediaType.APPLICATION_JSON);
		List<Map<String, String>> playName = new ArrayList<>();
		HashMap<String, String> actualMusic = new HashMap<>();
		actualMusic.put("title", "Say Something");
		actualMusic.put("artist", "Justin Timberlake");

		Mockito.when(this.logic.search(this.userMusics[0].toString(), SpotifyConstants.TYPE_TRACK, 5))
				.thenReturn(playName);
		Mockito.when(this.stringGenerator.generateSearchOutputString(this.logic.play(0, SearchTypes.NORMAL_SEARCH)))
				.thenReturn("songName");

		Response response = this.target.path("music").path("play").request().post(entity);
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("songName"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).search(this.userMusics[0].toString(), SpotifyConstants.TYPE_TRACK, 5);

		response = this.target.path("music").path("play").request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Found nothing to play."));
		assertThat(response.getStatus(), is(409));

		Mockito.when(this.stringGenerator.generateSearchOutputString(this.logic.play(1, SearchTypes.USER_PLAYLISTS)))
				.thenReturn("myPlaylist");
		Mockito.when(this.logic.getOwnPlaylists(5)).thenReturn(this.userPlaylists);
		response = this.target.path("music").path("play").queryParam("type", "user").queryParam("songNumber", "1")
				.request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("myPlaylist"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).getOwnPlaylists(5);

		Mockito.when(
				this.stringGenerator.generateSearchOutputString(this.logic.play(0, SearchTypes.FEATURED_PLAYLISTS)))
				.thenReturn("featuredPlaylist");
		Mockito.when(this.logic.getFeaturedPlaylists(4)).thenReturn(this.featuredPlaylists);
		response = this.target.path("music").path("play").queryParam("type", "featured").queryParam("limit", "4")
				.request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("featuredPlaylist"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).getFeaturedPlaylists(4);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#resume()}.
	 */
	@Test
	void testResume() {
		Mockito.when(this.logic.resume()).thenReturn(true);
		Response response = this.target.path("music").path("resume").request().post(null);
		assertThat(response.readEntity(String.class), is("resume"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).resume();

		Mockito.when(this.logic.resume()).thenReturn(false);
		response = this.target.path("music").path("resume").request().post(null);
		assertThat(response.getStatus(), is(409));
		assertThat(response.readEntity(String.class), is("Check player state"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#pause()}.
	 */
	@Test
	void testPause() {
		Mockito.when(this.logic.pause()).thenReturn(true);
		Response response = this.target.path("music").path("pause").request().post(null);
		assertThat(response.readEntity(String.class), is("pause"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).pause();

		Mockito.when(this.logic.pause()).thenReturn(false);
		response = this.target.path("music").path("pause").request().post(null);
		assertThat(response.getStatus(), is(409));
		assertThat(response.readEntity(String.class), is("Check player state"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#skip()}.
	 */
	@Test
	void testSkip() {
		Mockito.when(this.logic.skip()).thenReturn(true);
		Response response = this.target.path("music").path("skip").request().post(null);
		assertThat(response.readEntity(String.class), is("skip"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).skip();

		Mockito.when(this.logic.skip()).thenReturn(false);
		response = this.target.path("music").path("skip").request().post(null);
		assertThat(response.getStatus(), is(409));
		assertThat(response.readEntity(String.class), is("Check player state"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#back()}.
	 */
	@Test
	void testBack() {
		Mockito.when(this.logic.back()).thenReturn(true);
		Response response = this.target.path("music").path("back").request().post(null);
		assertThat(response.readEntity(String.class), is("back"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).back();

		Mockito.when(this.logic.back()).thenReturn(false);
		response = this.target.path("music").path("back").request().post(null);
		assertThat(response.getStatus(), is(409));
		assertThat(response.readEntity(String.class), is("Check player state"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#setVolume(java.lang.String)}.
	 */
	@Test
	void testSetVolume() {
		Response response = this.target.path("music").path("volume/0").request().post(null);
		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("0"));

		response = this.target.path("music").path("volume/50").request().post(null);
		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("50"));

		response = this.target.path("music").path("volume/100").request().post(null);
		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("100"));

		response = this.target.path("music").path("volume/-1").request().post(null);
		assertThat(response.getStatus(), is(400));
		assertThat(response.readEntity(String.class), is("Incorrect volume value"));

		response = this.target.path("music").path("volume/101").request().post(null);
		assertThat(response.getStatus(), is(400));
		assertThat(response.readEntity(String.class), is("Incorrect volume value"));

		response = this.target.path("music").path("volume/xyz").request().post(null);
		assertThat(response.getStatus(), is(400));
		assertThat(response.readEntity(String.class), is("Incorrect volume command"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#getPlaylists(String)}.
	 */
	@Test
	void testGetPlaylist() {
		Mockito.when(this.logic.getOwnPlaylists(5)).thenReturn(this.userPlaylists);
		Response response = this.target.path("music").path("playlists/user").request().post(null);
		Playlist[] actual = response.readEntity(Playlist[].class);
		assertThat(actual[0].getUri(), is(this.userPlaylists.get(0).getUri()));
		assertThat(actual[0].getName(), is(this.userPlaylists.get(0).getName()));
		assertThat(actual[0].getImageUrl(), is(this.userPlaylists.get(0).getImageUrl()));
		assertThat(response.getStatus(), is(200));
		verify(this.logic).getOwnPlaylists(5);

		Mockito.when(this.logic.getOwnPlaylists(2)).thenReturn(this.userPlaylists);
		response = this.target.path("music").path("playlists/user").queryParam("limit", 2).request().post(null);
		actual = response.readEntity(Playlist[].class);
		assertThat(actual[1].getUri(), is(this.userPlaylists.get(1).getUri()));
		assertThat(actual[1].getName(), is(this.userPlaylists.get(1).getName()));
		assertThat(actual[1].getImageUrl(), is(this.userPlaylists.get(1).getImageUrl()));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).getOwnPlaylists(2);

		Mockito.when(this.logic.getFeaturedPlaylists(1)).thenReturn(this.featuredPlaylists);
		response = this.target.path("music").path("playlists/featured").queryParam("limit", 1).request().post(null);
		actual = response.readEntity(Playlist[].class);
		assertThat(actual[0].getUri(), is(this.featuredPlaylists.get(0).getUri()));
		assertThat(actual[0].getName(), is(this.featuredPlaylists.get(0).getName()));
		assertThat(actual[0].getImageUrl(), is(this.featuredPlaylists.get(0).getImageUrl()));
		assertThat(response.getStatus(), is(200));

		Mockito.when(this.logic.getFeaturedPlaylists(1)).thenReturn(new ArrayList<Playlist>());
		response = this.target.path("music").path("playlists/featured").queryParam("limit", 1).request().post(null);
		assertThat(response.readEntity(String.class), is("No Playlist is available"));
		assertThat(response.getStatus(), is(404));
		Mockito.verify(this.logic, Mockito.times(2)).getFeaturedPlaylists(1);
	}

}
