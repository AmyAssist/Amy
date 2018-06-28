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

	private PlayerLogic logic;
	private MusicEntity musicEntity;
	private Playlist playlist;
	private Device[] devices;

	private WebTarget target;

	/**
	 * 
	 */
	@BeforeEach
	public void setUp() {
		this.testFramework.setRESTResource(MusicResource.class);
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
		currentSong.put("name", "MusicName");
		currentSong.put("artist", "ArtistName");
		Mockito.when(this.logic.getCurrentSong()).thenReturn(currentSong);

		this.musicEntity = new MusicEntity(currentSong.get("name"), currentSong.get("artist"));

		MusicEntity[] musics = new MusicEntity[1];
		musics[0] = this.musicEntity;
		this.playlist = new Playlist("myPlaylist", musics, "test123", "image.com");
	}

	/**
	 * creates List with device examples for testing
	 * 
	 * @return an example device list
	 */
	private List<de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Device> createDeviceList() {
		List<de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Device> devicesList = new ArrayList<>();
		devicesList.add(new de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Device("Smartphone", "Hello", "abc123"));
		devicesList.add(new de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Device("Computer", "Goodbye", "123abc"));
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

		Mockito.when(this.logic.setDevice(2)).thenReturn("No device found");

		response = this.target.path("music").path("setDevice/2").request().post(null);

		assertThat(response.readEntity(String.class), is("No device found"));
		assertThat(response.getStatus(), is(409));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#getCurrentSong()}.
	 */
	@Test
	void testGetCurrentSong() {
		Response response = this.target.path("music").path("currentSong").request().get();

		assertThat(response.readEntity(String.class), is("{\"artist\":\"ArtistName\",\"title\":\"MusicName\"}"));
		assertThat(response.getStatus(), is(200));
		verify(this.logic).getCurrentSong();
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#play()}.
	 */
	@Test
	void testPlay() {
		Entity<MusicEntity> entity = Entity.entity(this.musicEntity, MediaType.APPLICATION_JSON);
		List<Map<String, String>> playName = new ArrayList<>();

		Mockito.when(this.logic.search(this.musicEntity.toString(), SpotifyConstants.TYPE_TRACK, 5))
				.thenReturn(playName);
		Mockito.when(this.logic.convertSearchOutputToSingleString(this.logic.play(0, SearchTypes.NORMAL_SEARCH))).thenReturn("playName");
		Response response = this.target.path("music").path("play").request().post(entity);

		assertThat(response.readEntity(String.class), is("playName"));
		assertThat(response.getStatus(), is(200));

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
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.MusicResource#getPlaylist()}.
	 */
	@Test
	void testGetPlaylist() {
		Response response = this.target.path("music").path("playlist").request().get();
		// Not implemented yet
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

}
