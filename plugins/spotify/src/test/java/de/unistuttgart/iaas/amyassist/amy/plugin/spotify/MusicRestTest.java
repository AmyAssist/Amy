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

import com.wrapper.spotify.model_objects.miscellaneous.Device;

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
		currentSong.put("name", "Say Something");
		currentSong.put("artist", "Justin Timberlake");
		Mockito.when(this.logic.getCurrentSong()).thenReturn(currentSong);

		MusicEntity musicEntity1 = new MusicEntity(currentSong.get("name"), currentSong.get("artist"));
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
		verify(this.logic).getCurrentSong();
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#play()}.
	 */
	@Test
	void testPlay() {
		Entity<MusicEntity> entity = Entity.entity(this.userMusics[0], MediaType.APPLICATION_JSON);
		List<Map<String, String>> playName = new ArrayList<>();

		Mockito.when(this.logic.search(this.userMusics[0].toString(), SpotifyConstants.TYPE_TRACK, 5))
				.thenReturn(playName);
		Mockito.when(this.stringGenerator.generateSearchOutputString(this.logic.play(0, SearchTypes.NORMAL_SEARCH)))
				.thenReturn("playName");

		Response response = this.target.path("music").path("play").request().post(entity);
		assertThat(response.readEntity(String.class), is("playName"));
		assertThat(response.getStatus(), is(200));
		verify(this.logic).search(this.userMusics[0].toString(), SpotifyConstants.TYPE_TRACK, 5);

		response = this.target.path("music").path("play").request().post(null);
		assertThat(response.getStatus(), is(500));
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
		Mockito.when(this.logic.getOwnPlaylists(2)).thenReturn(this.userPlaylists);
		Response response = this.target.path("music").path("playlists/2").request().post(null);
		Playlist[] actual = response.readEntity(Playlist[].class);
		assertThat(actual[0].getUri(), is(this.userPlaylists.get(0).getUri()));
		assertThat(actual[0].getName(), is(this.userPlaylists.get(0).getName()));
		assertThat(actual[0].getImageUrl(), is(this.userPlaylists.get(0).getImageUrl()));
		assertThat(response.getStatus(), is(200));
		verify(this.logic).getOwnPlaylists(2);

		Mockito.when(this.logic.getOwnPlaylists(2)).thenReturn(this.userPlaylists);
		response = this.target.path("music").path("playlists/2").queryParam("type", "user").request().post(null);
		actual = response.readEntity(Playlist[].class);
		assertThat(actual[1].getUri(), is(this.userPlaylists.get(1).getUri()));
		assertThat(actual[1].getName(), is(this.userPlaylists.get(1).getName()));
		assertThat(actual[1].getImageUrl(), is(this.userPlaylists.get(1).getImageUrl()));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic, Mockito.times(2)).getOwnPlaylists(2);

		Mockito.when(this.logic.getFeaturedPlaylists(1)).thenReturn(this.featuredPlaylists);
		response = this.target.path("music").path("playlists/1").queryParam("type", "featured").request().post(null);
		actual = response.readEntity(Playlist[].class);
		assertThat(actual[0].getUri(), is(this.featuredPlaylists.get(0).getUri()));
		assertThat(actual[0].getName(), is(this.featuredPlaylists.get(0).getName()));
		assertThat(actual[0].getImageUrl(), is(this.featuredPlaylists.get(0).getImageUrl()));
		assertThat(response.getStatus(), is(200));

		Mockito.when(this.logic.getFeaturedPlaylists(1)).thenReturn(new ArrayList<Playlist>());
		response = this.target.path("music").path("playlists/1").queryParam("type", "featured").request().post(null);
		assertThat(response.readEntity(String.class), is("No Playlist is available"));
		assertThat(response.getStatus(), is(404));
		Mockito.verify(this.logic, Mockito.times(2)).getFeaturedPlaylists(1);
	}

}
