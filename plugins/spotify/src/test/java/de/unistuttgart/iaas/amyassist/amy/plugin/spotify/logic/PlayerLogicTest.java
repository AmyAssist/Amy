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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.SpotifyAPICalls;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.AlbumEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.ArtistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.TrackEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.PlayerLogic;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.Search;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.SearchTypes;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

@ExtendWith(FrameworkExtension.class)
class PlayerLogicTest {

	private static final String ID1 = "abc123";
	private static final String ID2 = "123abc";
	private static final String DEVICE_NAME1 = "Hello";
	private static final String DEVICE_NAME2 = "Godbye";
	private static final String PLAYLIST_NAME1 = "New Hits";
	private static final String PLAYLIST_NAME2 = "must popular hits";
	private static final String ARTIST_NAME1 = "David Guetta";
	private static final String ARTIST_NAME2 = "Justin Timberlake";

	private PlayerLogic playerLogic;

	@Reference
	private TestFramework testFramework;

	private List<PlaylistEntity> playlistList;
	private Device[] devices;
	private CurrentlyPlayingContext currentlyPlayingContext;
	private Paging<PlaylistSimplified> playlistsSpotifyFormat;
	private List<PlaylistEntity> playlistsOwnFormat;

	private SpotifyAPICalls spotifyAPICalls;

	private Search search;

	private MessageHub messageHub;

	@BeforeEach
	void init() {
		this.spotifyAPICalls = this.testFramework.mockService(SpotifyAPICalls.class);
		this.search = this.testFramework.mockService(Search.class);
		this.messageHub = this.testFramework.mockService(MessageHub.class);
		this.playerLogic = this.testFramework.setServiceUnderTest(PlayerLogic.class);
		initDevices();
		initCurrentTrack();

		this.playlistList = new ArrayList<>();
		this.playlistList.add(new PlaylistEntity());

	}

	private void initPlaylists() {
		PlaylistSimplified playlist1 = new PlaylistSimplified.Builder().setName(PLAYLIST_NAME1).setUri(ID1)
				.setOwner(new User.Builder().setDisplayName(ARTIST_NAME1).build()).build();
		PlaylistSimplified playlist2 = new PlaylistSimplified.Builder().setName(PLAYLIST_NAME2).setUri(ID2)
				.setImages(new Image.Builder().setUrl(ID1).build())
				.setOwner(new User.Builder().setDisplayName(ARTIST_NAME2).build()).build();
		PlaylistSimplified[] playlistList = new PlaylistSimplified[2];
		playlistList[0] = playlist1;
		playlistList[1] = playlist2;

		PlaylistEntity playlistEntity1 = new PlaylistEntity();
		playlistEntity1.setName(PLAYLIST_NAME1);
		playlistEntity1.setUri(ID1);
		PlaylistEntity playlistEntity2 = new PlaylistEntity();
		playlistEntity2.setName(PLAYLIST_NAME2);
		playlistEntity2.setUri(ID2);
		playlistEntity2.setImageUrl(ID1);
		this.playlistsSpotifyFormat = new Paging.Builder<PlaylistSimplified>().setItems(playlistList).build();
		this.playlistsOwnFormat = new ArrayList<>();

		this.playlistsOwnFormat.add(playlistEntity1);
		this.playlistsOwnFormat.add(playlistEntity2);

	}

	private void initDevices() {
		this.devices = new Device[2];
		this.devices[0] = new Device.Builder().setId(ID1).setIs_active(true).setName(DEVICE_NAME1).setVolume_percent(50)
				.setType("Smartphone").build();
		this.devices[1] = new Device.Builder().setId(ID2).setIs_active(false).setName(DEVICE_NAME2).setType("Computer")
				.build();
	}

	private void initCurrentTrack() {

		Track track1 = new Track.Builder().setName("Flames").setUri(ID1).setDurationMs(10)
				.setArtists(new ArtistSimplified.Builder().setName(ARTIST_NAME1).build(),
						new ArtistSimplified.Builder().setName(ARTIST_NAME2).build())
				.build();
		this.currentlyPlayingContext = new CurrentlyPlayingContext.Builder().setItem(track1).build();
	}

	@Test
	void testFirstTimeInitWihtClientIdAndSecret() {
		this.playerLogic.firstTimeInit(ID2, ID2);
		verify(this.spotifyAPICalls).setClientID(ID2);
		verify(this.spotifyAPICalls).setClientSecret(ID2);
		verify(this.spotifyAPICalls).authorizationCodeUri();
	}

	@Test
	void testFirstTimeInit() {
		this.playerLogic.firstTimeInit();
		verify(this.spotifyAPICalls).authorizationCodeUri();
	}

	@Test
	void testInputAuthCode() {
		this.playerLogic.inputAuthCode(ID2);
		verify(this.spotifyAPICalls).createRefreshToken(ID2);
	}

	@Test
	void testPlayEmptyList() {
		when(this.search.searchFeaturedPlaylists(5)).thenReturn(new ArrayList<>());
		assertThat(this.playerLogic.play(), equalTo(null));

	}

	@Test
	void testPlayNotEmptyList() {
		initPlaylists();
		when(this.search.searchFeaturedPlaylists(5)).thenReturn(this.playlistsOwnFormat);
		when(this.spotifyAPICalls.playListFromUri(any())).thenReturn(true);
		assertThat(this.playerLogic.play().getUri(), equalTo(ID1));
		verify(this.spotifyAPICalls).playListFromUri(ID1);
	}

	@Test
	void testPlayNotEmptyListSupressed() {
		initPlaylists();
		when(this.search.searchFeaturedPlaylists(5)).thenReturn(this.playlistsOwnFormat);
		when(this.spotifyAPICalls.playListFromUri(any())).thenReturn(true);
		this.playerLogic.setSuppressed(true);
		assertThat(this.playerLogic.play().getUri(), equalTo(ID1));
		verify(this.spotifyAPICalls, Mockito.never()).playListFromUri(any());
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).playListFromUri(any());
	}

	@Test
	void playPlaylist() {
		when(this.search.getFeaturedPlaylists()).thenReturn(this.playlistList);
		assertThat(this.playerLogic.playPlaylist(0, SearchTypes.FEATURED_PLAYLISTS), equalTo(this.playlistList.get(0)));
		assertThat(this.playerLogic.playPlaylist(1, SearchTypes.FEATURED_PLAYLISTS), equalTo(null));
	}

	@Test
	void playPlaylistSupressed() {
		when(this.search.getFeaturedPlaylists()).thenReturn(this.playlistList);
		this.playerLogic.setSuppressed(true);
		assertThat(this.playerLogic.playPlaylist(0, SearchTypes.FEATURED_PLAYLISTS), equalTo(this.playlistList.get(0)));
		assertThat(this.playerLogic.playPlaylist(1, SearchTypes.FEATURED_PLAYLISTS), equalTo(null));
		verify(this.spotifyAPICalls, Mockito.never()).playListFromUri(any());
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).playListFromUri(any());
	}

	@Test
	void playPlaylistUser() {
		when(this.search.getOwnPlaylists()).thenReturn(this.playlistList);
		assertThat(this.playerLogic.playPlaylist(0, SearchTypes.USER_PLAYLISTS), equalTo(this.playlistList.get(0)));
		assertThat(this.playerLogic.playPlaylist(1, SearchTypes.USER_PLAYLISTS), equalTo(null));
	}

	@Test
	void playPlaylistSearch() {
		when(this.search.getPlaylistSearchResults()).thenReturn(this.playlistList);
		assertThat(this.playerLogic.playPlaylist(0, SearchTypes.SEARCH_PLAYLISTS), equalTo(this.playlistList.get(0)));
		assertThat(this.playerLogic.playPlaylist(1, SearchTypes.SEARCH_PLAYLISTS), equalTo(null));
	}

	@Test
	void playTrackSearch() {
		List<TrackEntity> trackList = new ArrayList<>();
		trackList.add(new TrackEntity());
		when(this.search.getTrackSearchResults()).thenReturn(trackList);
		assertThat(this.playerLogic.playTrack(0), equalTo(trackList.get(0)));
		assertThat(this.playerLogic.playTrack(1), equalTo(null));
	}

	@Test
	void playTrackSearchSupressed() {
		List<TrackEntity> trackList = new ArrayList<>();
		trackList.add(new TrackEntity());
		when(this.search.getTrackSearchResults()).thenReturn(trackList);
		this.playerLogic.setSuppressed(true);
		assertThat(this.playerLogic.playTrack(0), equalTo(trackList.get(0)));
		assertThat(this.playerLogic.playTrack(1), equalTo(null));
		verify(this.spotifyAPICalls, Mockito.never()).playListFromUri(any());
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).playSongFromUri(any());
	}

	@Test
	void playAlbumSearch() {
		List<AlbumEntity> albumList = new ArrayList<>();
		albumList.add(new AlbumEntity());
		when(this.search.getAlbumSearchResults()).thenReturn(albumList);
		assertThat(this.playerLogic.playAlbum(0), equalTo(albumList.get(0)));
		assertThat(this.playerLogic.playAlbum(1), equalTo(null));
	}

	@Test
	void playAlbumSearchSupressed() {
		List<AlbumEntity> albumList = new ArrayList<>();
		albumList.add(new AlbumEntity());
		when(this.search.getAlbumSearchResults()).thenReturn(albumList);
		this.playerLogic.setSuppressed(true);
		assertThat(this.playerLogic.playAlbum(0), equalTo(albumList.get(0)));
		assertThat(this.playerLogic.playAlbum(1), equalTo(null));
		verify(this.spotifyAPICalls, Mockito.never()).playListFromUri(any());
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).playListFromUri(any());
	}

	@Test
	void playArtistSearch() {
		List<ArtistEntity> artistList = new ArrayList<>();
		artistList.add(new ArtistEntity());
		when(this.search.getArtistSearchResults()).thenReturn(artistList);
		assertThat(this.playerLogic.playArtist(0), equalTo(artistList.get(0)));
		assertThat(this.playerLogic.playArtist(1), equalTo(null));
	}

	@Test
	void playArtistSearchSupressed() {
		List<ArtistEntity> artistList = new ArrayList<>();
		artistList.add(new ArtistEntity());
		when(this.search.getArtistSearchResults()).thenReturn(artistList);
		this.playerLogic.setSuppressed(true);
		assertThat(this.playerLogic.playArtist(0), equalTo(artistList.get(0)));
		assertThat(this.playerLogic.playArtist(1), equalTo(null));
		verify(this.spotifyAPICalls, Mockito.never()).playListFromUri(any());
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).playListFromUri(any());
	}

	@Test
	void testResume() {
		this.playerLogic.resume();
		verify(this.spotifyAPICalls).resume();
	}

	@Test
	void testSupressedResumep() {
		this.playerLogic.setSuppressed(true);
		this.playerLogic.resume();
		verify(this.spotifyAPICalls, Mockito.never()).resume();
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).resume();
	}

	@Test
	void testPause() {
		this.playerLogic.pause();
		verify(this.spotifyAPICalls).pause();
	}

	@Test
	void testSupressedPause() {
		when(this.spotifyAPICalls.getIsPlaying()).thenReturn(true);
		this.playerLogic.setSuppressed(true);
		verify(this.spotifyAPICalls).pause();
		this.playerLogic.pause();
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls, Mockito.atMost(1)).pause();

	}

	@Test
	void testSkip() {
		this.playerLogic.skip();
		verify(this.spotifyAPICalls).skip();
	}

	@Test
	void testSupressedSkip() {
		this.playerLogic.setSuppressed(true);
		this.playerLogic.skip();
		verify(this.spotifyAPICalls, Mockito.never()).skip();
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).skip();
	}

	@Test
	void testBack() {
		this.playerLogic.back();
		verify(this.spotifyAPICalls).back();
	}

	@Test
	void testSupressedBack() {
		this.playerLogic.setSuppressed(true);
		this.playerLogic.back();
		verify(this.spotifyAPICalls, Mockito.never()).back();
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).back();
	}

	@Test
	void testSetVolumeStringMute() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(this.playerLogic.setVolume("mute"), equalTo(0));
		verify(this.spotifyAPICalls).setVolume(0);
		verify(this.spotifyAPICalls).getVolume();
	}

	@Test
	void testSetVolumeStringMax() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(this.playerLogic.setVolume("max"), equalTo(100));
		verify(this.spotifyAPICalls).setVolume(100);
		verify(this.spotifyAPICalls).getVolume();
	}

	@Test
	void testSetVolumeStringUpInRange() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(90);
		assertThat(this.playerLogic.setVolume("up"), equalTo(100));
		verify(this.spotifyAPICalls).setVolume(100);
		verify(this.spotifyAPICalls).getVolume();
	}

	@Test
	void testSetVolumeStringDownInRange() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(10);
		assertThat(this.playerLogic.setVolume("down"), equalTo(0));
		verify(this.spotifyAPICalls).setVolume(0);
		verify(this.spotifyAPICalls).getVolume();
	}

	@Test
	void testSetVolumeStringUpOurOfRange() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(91);
		assertThat(this.playerLogic.setVolume("up"), equalTo(100));
		verify(this.spotifyAPICalls).setVolume(100);
		verify(this.spotifyAPICalls).getVolume();
	}

	@Test
	void testSetVolumeStringDownOutOfRange() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(9);
		assertThat(this.playerLogic.setVolume("down"), equalTo(0));
		verify(this.spotifyAPICalls).setVolume(0);
		verify(this.spotifyAPICalls).getVolume();
	}

	@Test
	void testSetVolumeStringWrongString() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(50);
		assertThat(this.playerLogic.setVolume("downM"), equalTo(-1));
	}

	@Test
	void testSetVolumeStringUnknownVolume() {
		when(this.spotifyAPICalls.getVolume()).thenReturn(-1);
		assertThat(this.playerLogic.setVolume("up"), equalTo(-1));
		verify(this.spotifyAPICalls).getVolume();
	}

	@Test
	void testVolumeIntOutOfRangeNegative() {
		assertThat(this.playerLogic.setVolume(-2), equalTo(-1));
	}

	@Test
	void testVolumeIntOutOfRangePositve() {
		assertThat(this.playerLogic.setVolume(101), equalTo(-1));
	}

	@Test
	void testVolumeInt() {
		assertThat(this.playerLogic.setVolume(1), equalTo(1));
		verify(this.spotifyAPICalls).setVolume(1);
	}

	@Test
	void testCurrentSong() {
		when(this.spotifyAPICalls.getCurrentPlayingContext()).thenReturn(this.currentlyPlayingContext);
		when(this.search.createTrackData(any())).thenCallRealMethod();
		TrackEntity track = this.playerLogic.getCurrentSong();
		assertThat(track.getUri(), equalTo(ID1));
	}

	@Test
	void testNoCurrentSong() {
		when(this.spotifyAPICalls.getCurrentPlayingContext()).thenReturn(null);
		assertThat(this.playerLogic.getCurrentSong(), equalTo(null));
	}
	
	@Test
	void testSuppresedPlayingNothingChanged() {
		when(this.spotifyAPICalls.getIsPlaying()).thenReturn(true);
		this.playerLogic.setSuppressed(true);
		verify(this.spotifyAPICalls).pause();
		when(this.spotifyAPICalls.getIsPlaying()).thenReturn(false);
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls).resume();
	}
	
	@Test
	void testSuppresedNotPlayingNothingChanged() {
		when(this.spotifyAPICalls.getIsPlaying()).thenReturn(false);
		this.playerLogic.setSuppressed(true);
		verify(this.spotifyAPICalls, Mockito.never()).pause();
		this.playerLogic.setSuppressed(false);
		verify(this.spotifyAPICalls, Mockito.never()).resume();
	}

}
