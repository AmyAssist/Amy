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

import static io.github.amyassist.amy.test.matcher.rest.ResponseMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.plugin.spotify.entities.*;
import io.github.amyassist.amy.plugin.spotify.logic.DeviceLogic;
import io.github.amyassist.amy.plugin.spotify.logic.PlayerLogic;
import io.github.amyassist.amy.plugin.spotify.logic.Search;
import io.github.amyassist.amy.plugin.spotify.logic.SearchTypes;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of music
 * 
 * @author Muhammed Kaya, Lars Buttgereit
 */
@ExtendWith(FrameworkExtension.class)
class MusicRestTest {

	@Reference
	private TestFramework testFramework;

	private PlayerLogic logic;
	private DeviceLogic deviceLogic;
	private List<PlaylistEntity> userPlaylists = new ArrayList<>();
	private List<PlaylistEntity> featuredPlaylists = new ArrayList<>();
	private List<TrackEntity> trackList = new ArrayList<>();
	private List<AlbumEntity> albumList = new ArrayList<>();
	private List<ArtistEntity> artistList = new ArrayList<>();
	private List<PlaylistEntity> playlistList = new ArrayList<>();
	private WebTarget target;
	private Search search;

	private static final String SONG_NAME1 = "Flames";
	private static final String SONG_NAME2 = "Say Something";
	private static final String ARTIST_NAME1 = "David Guetta";

	/**
	 * setUp
	 */
	@BeforeEach
	public void setUp() {
		this.target = this.testFramework.setRESTResource(MusicResource.class);
		this.deviceLogic = this.testFramework.mockService(DeviceLogic.class);
		this.logic = this.testFramework.mockService(PlayerLogic.class);
		this.search = this.testFramework.mockService(Search.class);
		createData();
	}

	/**
	 * creates data for testing
	 */
	private void createData() {
		TrackEntity track = new TrackEntity();
		track.setName(SONG_NAME1);
		this.trackList.add(track);
		AlbumEntity album = new AlbumEntity();
		album.setName(SONG_NAME1);
		this.albumList.add(album);
		ArtistEntity artist = new ArtistEntity();
		artist.setName(ARTIST_NAME1);
		this.artistList.add(artist);
		PlaylistEntity playlist = new PlaylistEntity();
		playlist.setName(SONG_NAME1);
		this.playlistList.add(playlist);
		PlaylistEntity userPlaylist = new PlaylistEntity();
		userPlaylist.setName(SONG_NAME2);
		this.userPlaylists.add(userPlaylist);
		PlaylistEntity featuredPlaylist = new PlaylistEntity();
		featuredPlaylist.setName(ARTIST_NAME1);
		this.featuredPlaylists.add(featuredPlaylist);
	}

	/**
	 * creates List with device examples for testing
	 * 
	 * @return an example device list
	 */
	private List<DeviceEntity> createDeviceList() {
		List<DeviceEntity> devicesList = new ArrayList<>();
		devicesList.add(new DeviceEntity("Smartphone", "Hello", "abc123"));
		devicesList.add(new DeviceEntity("Computer", "Goodbye", "123abc"));
		return devicesList;
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#firstTimeInit(String, String)}.
	 */
	@Test
	void testFirstTimeInit() {
		URI uri = URI.create("http://example.com");

		Mockito.when(this.logic.firstTimeInit("HansDieter", "horst123")).thenReturn(uri);
		try (Response response = this.target.path("init").queryParam("clientID", "HansDieter")
				.queryParam("clientSecret", "horst123").request().post(null)) {
			assertThat(response.readEntity(URI.class), is(uri));
			assertThat(response, status(200));
			Mockito.verify(this.logic).firstTimeInit("HansDieter", "horst123");
		}

		Mockito.when(this.logic.firstTimeInit("abc123", "123abc")).thenReturn(null);
		try (Response response = this.target.path("init").queryParam("clientID", "abc123")
				.queryParam("clientSecret", "123abc").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Enter valid client information"));
			assertThat(response, status(409));
			Mockito.verify(this.logic).firstTimeInit("abc123", "123abc");
		}

		Mockito.when(this.logic.firstTimeInit()).thenReturn(uri);
		try (Response response = this.target.path("init").queryParam("clientID", null).queryParam("clientSecret", null)
				.request().post(null)) {
			assertThat(response.readEntity(URI.class), is(uri));
			assertThat(response, status(200));
		}

		try (Response response = this.target.path("init").request().post(null)) {
			assertThat(response.readEntity(URI.class), is(uri));
			assertThat(response, status(200));
		}

		Mockito.when(this.logic.firstTimeInit()).thenReturn(null);
		try (Response response = this.target.path("init").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Check your property location"));
			assertThat(response, status(409));
			Mockito.verify(this.logic, Mockito.times(3)).firstTimeInit();
		}

	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#inputAuthCode(String)}.
	 */
	@Test
	void testInputAuthCode() {
		try (Response response = this.target.path("token/abc").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Token created."));
			assertThat(response, status(200));
			Mockito.verify(this.logic).inputAuthCode("abc");
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#getDevices()}.
	 */
	@Test
	void testGetDevices() {
		Mockito.when(this.deviceLogic.getDevices()).thenReturn(createDeviceList());
		try (Response response = this.target.path("getDevices").request().get()) {
			DeviceEntity[] actualDevices = response.readEntity(DeviceEntity[].class);
			assertThat(actualDevices[0].getType(), equalTo("Smartphone"));
			assertThat(actualDevices[0].getName(), equalTo("Hello"));
			assertThat(actualDevices[0].getID(), equalTo("abc123"));
			assertThat(actualDevices[1].getType(), equalTo("Computer"));
			assertThat(actualDevices[1].getName(), equalTo("Goodbye"));
			assertThat(actualDevices[1].getID(), equalTo("123abc"));
			assertThat(response, status(200));
		}

		List<DeviceEntity> emptyList = new ArrayList<>();
		Mockito.when(this.deviceLogic.getDevices()).thenReturn(emptyList);
		try (Response response = this.target.path("getDevices").request().get()) {
			assertThat(response.readEntity(String.class),
					equalTo("Currently there are no devices available or connected"));
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#setDevice(String)}.
	 */
	@Test
	void testSetDevice() {
		Mockito.when(this.deviceLogic.setDevice(0)).thenReturn("Hello");
		try (Response response = this.target.path("setDevice/0").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Hello"));
			assertThat(response, status(200));
			Mockito.verify(this.deviceLogic).setDevice(0);
		}

		Mockito.when(this.deviceLogic.setDevice(2)).thenReturn("No device found");
		try (Response response = this.target.path("setDevice/2").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("No device found"));
			assertThat(response, status(404));
			Mockito.verify(this.deviceLogic).setDevice(2);
		}

		Mockito.when(this.deviceLogic.setDevice("abc123")).thenReturn(true);
		try (Response response = this.target.path("setDevice/abc123").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Device: 'abc123' is selected now"));
			assertThat(response, status(200));
			Mockito.verify(this.deviceLogic).setDevice("abc123");
		}

		Mockito.when(this.deviceLogic.setDevice("xyz789")).thenReturn(false);
		try (Response response = this.target.path("setDevice/xyz789").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Device: 'xyz789' is not available"));
			assertThat(response, status(409));
			Mockito.verify(this.deviceLogic).setDevice("xyz789");
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#searchTracks(String, int)}.
	 */
	@Test
	void testSearchTrack() {
		Mockito.when(this.search.searchforTracks(SONG_NAME1, 1)).thenReturn(this.trackList);
		try (Response response = this.target.path("search/track/" + SONG_NAME1).queryParam("limit", 1).request()
				.get()) {
			TrackEntity[] actual = response.readEntity(TrackEntity[].class);
			assertThat(actual[0].getName(), equalTo(this.trackList.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.search).searchforTracks(SONG_NAME1, 1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#searchTracks(String, int)}.
	 */
	@Test
	void testSearchTrackEmpty() {
		Mockito.when(this.search.searchforTracks(SONG_NAME1, 1)).thenReturn(new ArrayList<>());
		try (Response response = this.target.path("search/track/" + SONG_NAME1).queryParam("limit", 1).request()
				.get()) {
			assertEquals(404, response.getStatus());
			Mockito.verify(this.search).searchforTracks(SONG_NAME1, 1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#searchAlbums(String, int)}.
	 */
	@Test
	void testSearchAlbum() {
		Mockito.when(this.search.searchforAlbums(SONG_NAME1, 1)).thenReturn(this.albumList);
		try (Response response = this.target.path("search/album/" + SONG_NAME1).queryParam("limit", 1).request()
				.get()) {
			AlbumEntity[] actual = response.readEntity(AlbumEntity[].class);
			assertThat(actual[0].getName(), equalTo(this.albumList.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.search).searchforAlbums(SONG_NAME1, 1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#searchAlbums(String, int)}.
	 */
	@Test
	void testSearchAlbumEmpty() {
		Mockito.when(this.search.searchforAlbums(SONG_NAME1, 1)).thenReturn(new ArrayList<>());
		try (Response response = this.target.path("search/album/" + SONG_NAME1).queryParam("limit", 1).request()
				.get()) {
			assertEquals(404, response.getStatus());
			Mockito.verify(this.search).searchforAlbums(SONG_NAME1, 1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#searchArtists(String, int)}.
	 */
	@Test
	void testSearchArtist() {
		Mockito.when(this.search.searchforArtists(ARTIST_NAME1, 1)).thenReturn(this.artistList);
		try (Response response = this.target.path("search/artist/" + ARTIST_NAME1).queryParam("limit", 1).request()
				.get()) {
			ArtistEntity[] actual = response.readEntity(ArtistEntity[].class);
			assertThat(actual[0].getName(), equalTo(this.artistList.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.search).searchforArtists(ARTIST_NAME1, 1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#searchArtists(String, int)}.
	 */
	@Test
	void testSearchArtistEmpty() {
		Mockito.when(this.search.searchforTracks(ARTIST_NAME1, 1)).thenReturn(new ArrayList<>());
		try (Response response = this.target.path("search/artist/" + ARTIST_NAME1).queryParam("limit", 1).request()
				.get()) {
			assertEquals(404, response.getStatus());
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#searchPlaylists(String, int)}.
	 */
	@Test
	void testSearchPlaylist() {
		Mockito.when(this.search.searchforPlaylists(SONG_NAME1, 1)).thenReturn(this.playlistList);
		try (Response response = this.target.path("search/playlist/" + SONG_NAME1).queryParam("limit", 1).request()
				.get()) {
			PlaylistEntity[] actual = response.readEntity(PlaylistEntity[].class);
			assertThat(actual[0].getName(), equalTo(this.playlistList.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.search).searchforPlaylists(SONG_NAME1, 1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#searchPlaylists(String, int)}.
	 */
	@Test
	void testSearchPlaylistEmpty() {
		Mockito.when(this.search.searchforPlaylists(SONG_NAME1, 1)).thenReturn(new ArrayList<>());
		try (Response response = this.target.path("search/playlist/" + SONG_NAME1).queryParam("limit", 1).request()
				.get()) {
			assertEquals(404, response.getStatus());
			Mockito.verify(this.search).searchforPlaylists(SONG_NAME1, 1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#playPlaylist(int, String)}.
	 */
	@Test
	void playPlaylistUser() {
		Mockito.when(this.logic.playPlaylist(0, SearchTypes.USER_PLAYLISTS)).thenReturn(this.userPlaylists.get(0));
		try (Response response = this.target.path("play/playlist").queryParam("type", "user").queryParam("index", 0)
				.request().post(null)) {
			PlaylistEntity actual = response.readEntity(PlaylistEntity.class);
			assertThat(actual.getName(), equalTo(this.userPlaylists.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).playPlaylist(0, SearchTypes.USER_PLAYLISTS);
		}

		Mockito.when(this.logic.playPlaylist(0, SearchTypes.USER_PLAYLISTS)).thenReturn(null);
		try (Response response = this.target.path("play/playlist").queryParam("type", "user").queryParam("index", 0)
				.request().post(null)) {
			assertEquals(404, response.getStatus());
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#playPlaylist(int, String)}.
	 */
	@Test
	void playPlaylistFeatured() {
		Mockito.when(this.logic.playPlaylist(0, SearchTypes.FEATURED_PLAYLISTS))
				.thenReturn(this.featuredPlaylists.get(0));
		try (Response response = this.target.path("play/playlist").queryParam("type", "featured").queryParam("index", 0)
				.request().post(null)) {
			PlaylistEntity actual = response.readEntity(PlaylistEntity.class);
			assertThat(actual.getName(), equalTo(this.featuredPlaylists.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).playPlaylist(0, SearchTypes.FEATURED_PLAYLISTS);
		}

		Mockito.when(this.logic.playPlaylist(0, SearchTypes.FEATURED_PLAYLISTS)).thenReturn(null);
		try (Response response = this.target.path("play/playlist").queryParam("type", "featured").queryParam("index", 0)
				.request().post(null)) {
			assertEquals(404, response.getStatus());
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#playPlaylist(int, String)}.
	 */
	@Test
	void playPlaylistSearch() {
		Mockito.when(this.logic.playPlaylist(0, SearchTypes.SEARCH_PLAYLISTS)).thenReturn(this.playlistList.get(0));
		try (Response response = this.target.path("play/playlist").queryParam("type", "search").queryParam("index", 0)
				.request().post(null)) {
			PlaylistEntity actual = response.readEntity(PlaylistEntity.class);
			assertThat(actual.getName(), equalTo(this.playlistList.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).playPlaylist(0, SearchTypes.SEARCH_PLAYLISTS);
		}

		when(this.logic.playPlaylist(0, SearchTypes.SEARCH_PLAYLISTS)).thenReturn(null);
		try (Response response = this.target.path("play/playlist").queryParam("type", "search").queryParam("index", 0)
				.request().post(null)) {
			assertEquals(404, response.getStatus());
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#playAlbum(int)}.
	 */
	@Test
	void playAlbum() {
		Mockito.when(this.logic.playAlbum(0)).thenReturn(this.albumList.get(0));
		try (Response response = this.target.path("play/album").queryParam("index", 0).request().post(null)) {
			AlbumEntity actual = response.readEntity(AlbumEntity.class);
			assertThat(actual.getName(), equalTo(this.albumList.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).playAlbum(0);
		}

		Mockito.when(this.logic.playAlbum(0)).thenReturn(null);
		try (Response response = this.target.path("play/album").queryParam("index", 0).request().post(null)) {
			assertEquals(404, response.getStatus());
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#playTrack(int)}.
	 */
	@Test
	void playTrack() {
		Mockito.when(this.logic.playTrack(0)).thenReturn(this.trackList.get(0));
		try (Response response = this.target.path("play/track").queryParam("index", 0).request().post(null)) {
			TrackEntity actual = response.readEntity(TrackEntity.class);
			assertThat(actual.getName(), equalTo(this.trackList.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).playTrack(0);
		}

		Mockito.when(this.logic.playTrack(0)).thenReturn(null);
		try (Response response = this.target.path("play/track").queryParam("index", 0).request().post(null)) {
			assertEquals(404, response.getStatus());
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#playArtist(int)}.
	 */
	@Test
	void playArtist() {
		Mockito.when(this.logic.playArtist(0)).thenReturn(this.artistList.get(0));
		try (Response response = this.target.path("play/artist").queryParam("index", 0).request().post(null)) {
			ArtistEntity actual = response.readEntity(ArtistEntity.class);
			assertThat(actual.getName(), equalTo(this.artistList.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).playArtist(0);
		}

		Mockito.when(this.logic.playArtist(0)).thenReturn(null);
		try (Response response = this.target.path("play/artist").queryParam("index", 0).request().post(null)) {
			assertEquals(404, response.getStatus());
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#resume()}.
	 */
	@Test
	void testResume() {
		Mockito.when(this.logic.resume()).thenReturn(true);
		try (Response response = this.target.path("resume").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("resume"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).resume();
		}

		Mockito.when(this.logic.resume()).thenReturn(false);
		try (Response response = this.target.path("resume").request().post(null)) {
			assertThat(response, status(409));
			assertThat(response.readEntity(String.class), equalTo("Check player state"));
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#pause()}.
	 */
	@Test
	void testPause() {
		Mockito.when(this.logic.pause()).thenReturn(true);
		try (Response response = this.target.path("pause").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("pause"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).pause();
		}

		Mockito.when(this.logic.pause()).thenReturn(false);
		try (Response response = this.target.path("pause").request().post(null)) {
			assertThat(response, status(409));
			assertThat(response.readEntity(String.class), equalTo("Check player state"));
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#skip()}.
	 */
	@Test
	void testSkip() {
		Mockito.when(this.logic.skip()).thenReturn(true);
		try (Response response = this.target.path("skip").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("skip"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).skip();
		}

		Mockito.when(this.logic.skip()).thenReturn(false);
		try (Response response = this.target.path("skip").request().post(null)) {
			assertThat(response, status(409));
			assertThat(response.readEntity(String.class), equalTo("Check player state"));
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#back()}.
	 */
	@Test
	void testBack() {
		Mockito.when(this.logic.back()).thenReturn(true);
		try (Response response = this.target.path("back").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("back"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).back();
		}

		Mockito.when(this.logic.back()).thenReturn(false);
		try (Response response = this.target.path("back").request().post(null)) {
			assertThat(response, status(409));
			assertThat(response.readEntity(String.class), equalTo("Check player state"));
		}
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.spotify.MusicResource#getCurrentSong()}.
	 */
	@Test
	void testGetCurrentSong() {
		Mockito.when(this.logic.getCurrentSong()).thenReturn(this.trackList.get(0));
		try (Response response = this.target.path("currentSong").request().get()) {
			TrackEntity actualMusic = response.readEntity(TrackEntity.class);
			assertThat(actualMusic.getName(), equalTo(this.trackList.get(0).getName()));
			assertThat(response, status(200));
		}

		Mockito.when(this.logic.getCurrentSong()).thenReturn(null);
		try (Response response = this.target.path("currentSong").request().get()) {
			assertThat(response.readEntity(String.class), equalTo("No song is currently playing"));
			assertEquals(404, response.getStatus());
			Mockito.verify(this.logic, Mockito.times(2)).getCurrentSong();
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#getPlaylists(String, int)}.
	 */
	@Test
	void testGetPlaylistUser() {
		when(this.search.searchOwnPlaylists(1)).thenReturn(this.userPlaylists);
		try (Response response = this.target.path("playlists/user").queryParam("limit", 1).request().get()) {
			PlaylistEntity[] actual = response.readEntity(PlaylistEntity[].class);
			assertThat(actual[0].getName(), equalTo(this.userPlaylists.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.search).searchOwnPlaylists(1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#getPlaylists(String, int)}.
	 */
	@Test
	void testGetPlaylistFeatured() {
		Mockito.when(this.search.searchFeaturedPlaylists(1)).thenReturn(this.featuredPlaylists);
		try (Response response = this.target.path("playlists/featured").queryParam("limit", 1).request().get()) {
			PlaylistEntity[] actual = response.readEntity(PlaylistEntity[].class);
			assertThat(actual[0].getName(), equalTo(this.featuredPlaylists.get(0).getName()));
			assertThat(response, status(200));
			Mockito.verify(this.search).searchFeaturedPlaylists(1);
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#getPlaylists(String, int)}.
	 */
	@Test
	void testGetPlaylistEmpty() {
		Mockito.when(this.search.searchFeaturedPlaylists(1)).thenReturn(new ArrayList<>());
		try (Response response = this.target.path("playlists/featured").queryParam("limit", 1).request().get()) {
			assertEquals(404, response.getStatus());
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#setVolume(java.lang.String)}.
	 */
	@Test
	void testSetVolume() {
		try (Response response = this.target.path("volume/0").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("0"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).setVolume(0);
		}

		try (Response response = this.target.path("volume/50").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("50"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).setVolume(50);
		}

		try (Response response = this.target.path("volume/100").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("100"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).setVolume(100);
		}

		try (Response response = this.target.path("volume/-1").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Incorrect volume value"));
			assertThat(response, status(400));
		}

		try (Response response = this.target.path("volume/101").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Incorrect volume value"));
			assertThat(response, status(400));
		}

		try (Response response = this.target.path("volume/xyz").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("Incorrect volume command"));
			assertThat(response, status(400));
		}

		Mockito.when(this.logic.setVolume("mute")).thenReturn(0);
		try (Response response = this.target.path("volume/mute").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("0"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).setVolume("mute");
		}
		Mockito.when(this.logic.setVolume("max")).thenReturn(100);
		try (Response response = this.target.path("volume/max").request().post(null)) {
			assertThat(response.readEntity(String.class), equalTo("100"));
			assertThat(response, status(200));
			Mockito.verify(this.logic).setVolume("max");
		}

		Mockito.when(this.logic.setVolume("up")).thenReturn(60);
		try (Response response = this.target.path("volume/up").request().post(null)) {
			assertThat(response, status(200));
			assertThat(response.readEntity(String.class), equalTo("60"));
			Mockito.verify(this.logic).setVolume("up");
		}

		Mockito.when(this.logic.setVolume("down")).thenReturn(40);
		try (Response response = this.target.path("volume/down").request().post(null)) {
			assertThat(response, status(200));
			assertThat(response.readEntity(String.class), equalTo("40"));
			Mockito.verify(this.logic).setVolume("down");
		}

		Mockito.when(this.logic.setVolume("mute")).thenReturn(-1);
		try (Response response = this.target.path("volume/mute").request().post(null)) {
			assertThat(response, status(409));
			assertThat(response.readEntity(String.class), equalTo("Check player state"));
		}
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.spotify.MusicResource#setDeviceName(String, String)}.
	 */
	@Test
	public void setDeviceName() {
		try (Response response = this.target.path("setDeviceName").request().post(null)) {
			Mockito.verify(this.deviceLogic).setNewDeviceName(any(), any());
		}
	}

}
