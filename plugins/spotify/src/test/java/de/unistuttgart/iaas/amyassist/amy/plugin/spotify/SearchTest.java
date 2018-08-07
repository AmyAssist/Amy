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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wrapper.spotify.model_objects.special.FeaturedPlaylists;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.AlbumEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.ArtistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.TrackEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.Search;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * 
 * 
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class SearchTest {
	@Reference
	private TestFramework testFramework;

	private SearchResult albums;
	private SearchResult artists;
	private SearchResult playlists;
	private SearchResult tracks;
	private FeaturedPlaylists featuredPlaylists;
	private Paging<PlaylistSimplified> playlistsSpotifyFormat;
	private List<PlaylistEntity> playlistsOwnFormat;
	private Search search;

	private static final String SONG_NAME1 = "Flames";
	private static final String SONG_NAME2 = "Say Something";
	private static final String ID1 = "abc123";
	private static final String ID2 = "123abc";
	private static final String PLAYLIST_NAME1 = "New Hits";
	private static final String PLAYLIST_NAME2 = "must popular hits";
	private static final String ARTIST_NAME1 = "David Guetta";
	private static final String ARTIST_NAME2 = "Justin Timberlake";
	private static final String GENRE1 = "Pop";
	private static final String GENRE2 = "Rock";
	private static final int DURATION1 = 10;
	private static final int DURATION2 = 30;
	private static final String IMAGE_URL1 = "abc123";
	private static final String IMAGE_URL2 = "123abc";

	private SpotifyAPICalls spotifyAPICalls;

	@BeforeEach
	void init() {
		this.spotifyAPICalls = this.testFramework.mockService(SpotifyAPICalls.class);
		this.search = this.testFramework.setServiceUnderTest(Search.class);
		spotifyAlbum();
		spotifyArtist();
		spotifyPlaylist();
		spotifyTrack();

		createFeaturedPlaylist();
	}

	private void createFeaturedPlaylist() {
		PlaylistSimplified playlist1 = new PlaylistSimplified.Builder().setName(SONG_NAME1).setUri(ID1)
				.setOwner(new User.Builder().setDisplayName(ARTIST_NAME1).build()).build();
		PlaylistSimplified playlist2 = new PlaylistSimplified.Builder().setName(SONG_NAME2).setUri(ID2)
				.setOwner(new User.Builder().setDisplayName(ARTIST_NAME2).build()).build();
		PlaylistSimplified[] playlistList = new PlaylistSimplified[2];
		playlistList[0] = playlist1;
		playlistList[1] = playlist2;
		this.featuredPlaylists = new FeaturedPlaylists.Builder()
				.setPlaylists(new Paging.Builder<PlaylistSimplified>().setItems(playlistList).build()).build();
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
		this.playlistsSpotifyFormat = new Paging.Builder<PlaylistSimplified>().setItems(playlistList).build();
		this.playlistsOwnFormat = new ArrayList<>();
		this.playlistsOwnFormat.add(new PlaylistEntity(PLAYLIST_NAME1, null, ID1, null));
		this.playlistsOwnFormat.add(new PlaylistEntity(PLAYLIST_NAME2, null, ID2, ID1));
	}

	private void spotifyTrack() {
		Track track1 = new Track.Builder().setName(SONG_NAME1).setUri(ID1).setDurationMs(DURATION1)
				.setArtists(new ArtistSimplified.Builder().setName(ARTIST_NAME1).build()).build();
		Track track2 = new Track.Builder().setName(SONG_NAME2).setUri(ID2).setDurationMs(DURATION2)
				.setArtists(new ArtistSimplified.Builder().setName(ARTIST_NAME2).build()).build();
		Track[] trackList = new Track[2];
		trackList[0] = track1;
		trackList[1] = track2;
		this.tracks = new SearchResult.Builder().setTracks(new Paging.Builder<Track>().setItems(trackList).build())
				.build();
	}

	private void spotifyAlbum() {
		AlbumSimplified album1 = new AlbumSimplified.Builder().setName(SONG_NAME1).setUri(ID1)
				.setImages(new Image.Builder().setUrl(IMAGE_URL1).build())
				.setArtists(new ArtistSimplified.Builder().setName(ARTIST_NAME1).build()).build();
		AlbumSimplified album2 = new AlbumSimplified.Builder().setName(SONG_NAME2).setUri(ID2)
				.setImages(new Image.Builder().setUrl(IMAGE_URL2).build())
				.setArtists(new ArtistSimplified.Builder().setName(ARTIST_NAME2).build()).build();
		AlbumSimplified[] albumList = new AlbumSimplified[2];
		albumList[0] = album1;
		albumList[1] = album2;
		this.albums = new SearchResult.Builder()
				.setAlbums(new Paging.Builder<AlbumSimplified>().setItems(albumList).build()).build();
	}

	private void spotifyArtist() {
		Artist artist1 = new Artist.Builder().setName(ARTIST_NAME1).setUri(ID1).setGenres(GENRE1)
				.setImages(new Image.Builder().setUrl(IMAGE_URL1).build()).build();
		Artist artist2 = new Artist.Builder().setName(ARTIST_NAME2).setUri(ID2).setGenres(GENRE2)
				.setImages(new Image.Builder().setUrl(IMAGE_URL2).build()).build();
		Artist[] artistList = new Artist[2];
		artistList[0] = artist1;
		artistList[1] = artist2;
		this.artists = new SearchResult.Builder().setArtists(new Paging.Builder<Artist>().setItems(artistList).build())
				.build();
	}

	private void spotifyPlaylist() {
		PlaylistSimplified playlist1 = new PlaylistSimplified.Builder().setName(SONG_NAME1).setUri(ID1)
				.setImages(new Image.Builder().setUrl(IMAGE_URL1).build())
				.setOwner(new User.Builder().setDisplayName(ARTIST_NAME1).build()).build();
		PlaylistSimplified playlist2 = new PlaylistSimplified.Builder().setName(SONG_NAME2).setUri(ID2)
				.setImages(new Image.Builder().setUrl(IMAGE_URL2).build())
				.setOwner(new User.Builder().setDisplayName(ARTIST_NAME2).build()).build();
		PlaylistSimplified[] playlistList = new PlaylistSimplified[2];
		playlistList[0] = playlist1;
		playlistList[1] = playlist2;
		this.playlists = new SearchResult.Builder()
				.setPlaylists(new Paging.Builder<PlaylistSimplified>().setItems(playlistList).build()).build();
	}

	@Test
	void testSearchTrack() {
		when(this.spotifyAPICalls.searchInSpotify(SONG_NAME1, "track", 2)).thenReturn(this.tracks);
		List<TrackEntity> trackEntities = this.search.searchforTracks(SONG_NAME1, 2);
		TrackEntity trackEntity1 = trackEntities.get(0);
		TrackEntity TrackEntity2 = trackEntities.get(1);
		assertThat(trackEntity1.getName(), equalTo(SONG_NAME1));
		assertThat(TrackEntity2.getName(), equalTo(SONG_NAME2));
		assertThat(trackEntity1.getArtist()[0], equalTo(ARTIST_NAME1));
		assertThat(TrackEntity2.getArtist()[0], equalTo(ARTIST_NAME2));
		assertThat(trackEntity1.getUri(), equalTo(ID1));
		assertThat(TrackEntity2.getUri(), equalTo(ID2));
		assertThat(trackEntity1.getDurationInMs(), equalTo(DURATION1));
		assertThat(TrackEntity2.getDurationInMs(), equalTo(DURATION2));
	}

	@Test
	void testSearchPlaylist() {
		when(this.spotifyAPICalls.searchInSpotify(SONG_NAME1, "playlist", 2)).thenReturn(this.playlists);
		List<PlaylistEntity> playlistEntities = this.search.searchforPlaylists(SONG_NAME1, 2);
		PlaylistEntity playlistEntity1 = playlistEntities.get(0);
		PlaylistEntity playlistEntity2 = playlistEntities.get(1);
		assertThat(playlistEntity1.getName(), equalTo(SONG_NAME1));
		assertThat(playlistEntity2.getName(), equalTo(SONG_NAME2));
		assertThat(playlistEntity1.getPlaylistCreator(), equalTo(ARTIST_NAME1));
		assertThat(playlistEntity2.getPlaylistCreator(), equalTo(ARTIST_NAME2));
		assertThat(playlistEntity1.getUri(), equalTo(ID1));
		assertThat(playlistEntity2.getUri(), equalTo(ID2));
		assertThat(playlistEntity1.getImageUrl(), equalTo(IMAGE_URL1));
		assertThat(playlistEntity2.getImageUrl(), equalTo(IMAGE_URL2));
	}

	@Test
	void testSearchAlbum() {
		when(this.spotifyAPICalls.searchInSpotify(SONG_NAME1, "album", 2)).thenReturn(this.albums);
		List<AlbumEntity> albumEntities = this.search.searchforAlbum(SONG_NAME1, 2);
		AlbumEntity albumEntity1 = albumEntities.get(0);
		AlbumEntity albumEntity2 = albumEntities.get(1);
		assertThat(albumEntity1.getName(), equalTo(SONG_NAME1));
		assertThat(albumEntity2.getName(), equalTo(SONG_NAME2));
		assertThat(albumEntity1.getArtists()[0], equalTo(ARTIST_NAME1));
		assertThat(albumEntity2.getArtists()[0], equalTo(ARTIST_NAME2));
		assertThat(albumEntity1.getUri(), equalTo(ID1));
		assertThat(albumEntity2.getUri(), equalTo(ID2));
		assertThat(albumEntity1.getImageUrl(), equalTo(IMAGE_URL1));
		assertThat(albumEntity2.getImageUrl(), equalTo(IMAGE_URL2));
	}

	@Test
	void testSearchArtist() {
		when(this.spotifyAPICalls.searchInSpotify(ARTIST_NAME1, "artist", 2)).thenReturn(this.artists);
		List<ArtistEntity> artistEntities = this.search.searchforArtists(ARTIST_NAME1, 2);
		ArtistEntity artistEntity1 = artistEntities.get(0);
		ArtistEntity artistEntity2 = artistEntities.get(1);
		assertThat(artistEntity1.getName(), equalTo(ARTIST_NAME1));
		assertThat(artistEntity2.getName(), equalTo(ARTIST_NAME2));
		assertThat(artistEntity1.getGenre()[0], equalTo(GENRE1));
		assertThat(artistEntity2.getGenre()[0], equalTo(GENRE2));
		assertThat(artistEntity1.getUri(), equalTo(ID1));
		assertThat(artistEntity2.getUri(), equalTo(ID2));
		assertThat(artistEntity1.getImageUrl(), equalTo(IMAGE_URL1));
		assertThat(artistEntity2.getImageUrl(), equalTo(IMAGE_URL2));
	}

	@Test
	void testEmptySearchResults() {
		when(this.spotifyAPICalls.searchInSpotify(any(), any(), anyInt())).thenReturn(null);
		assertThat(this.search.searchforTracks("", 0).isEmpty(), equalTo(true));
		assertThat(this.search.searchforAlbum("", 0).isEmpty(), equalTo(true));
		assertThat(this.search.searchforPlaylists("", 0).isEmpty(), equalTo(true));
		assertThat(this.search.searchforArtists("", 0).isEmpty(), equalTo(true));
	}

	@Test
	void testFeaturedPlaylists() {
		when(spotifyAPICalls.getFeaturedPlaylists(Search.SEARCH_LIMIT)).thenReturn(featuredPlaylists);
		List<PlaylistEntity> result = this.search.searchFeaturedPlaylists(Search.SEARCH_LIMIT);
		assertThat(result.get(0).getName(), equalTo(SONG_NAME1));
		assertThat(result.get(1).getName(), equalTo(SONG_NAME2));
		assertThat(result.get(0).getUri(), equalTo(ID1));
		assertThat(result.get(1).getUri(), equalTo(ID2));
	}

	@Test
	void testEmptyFeaturedPlaylists() {
		when(spotifyAPICalls.getFeaturedPlaylists(Search.SEARCH_LIMIT)).thenReturn(null);
		assertThat(search.searchFeaturedPlaylists(Search.SEARCH_LIMIT), equalTo(new ArrayList<>()));
	}

	@Test
	void testGetUsersPlaylists() {
		initPlaylists();
		List<PlaylistEntity> pl;
		when(this.spotifyAPICalls.getOwnPlaylists(2)).thenReturn(this.playlistsSpotifyFormat);
		pl = this.search.searchOwnPlaylists(2);
		assertThat(pl.get(0).getUri(), equalTo(ID1));
		assertThat(pl.get(1).getUri(), equalTo(ID2));
		assertThat(pl.get(0).getName(), equalTo(PLAYLIST_NAME1));
		assertThat(pl.get(1).getName(), equalTo(PLAYLIST_NAME2));
		assertThat(pl.get(0).getImageUrl(), equalTo(null));
		assertThat(pl.get(1).getImageUrl(), equalTo(ID1));
	}

	@Test
	void testGetFeturedPlaylists() {
		initPlaylists();
		FeaturedPlaylists featuredPls = new FeaturedPlaylists.Builder().setPlaylists(this.playlistsSpotifyFormat)
				.build();
		List<PlaylistEntity> pl;
		when(this.spotifyAPICalls.getFeaturedPlaylists(2)).thenReturn(featuredPls);
		pl = this.search.searchFeaturedPlaylists(2);
		assertThat(pl.get(0).getUri(), equalTo(ID1));
		assertThat(pl.get(1).getUri(), equalTo(ID2));
		assertThat(pl.get(0).getName(), equalTo(PLAYLIST_NAME1));
		assertThat(pl.get(1).getName(), equalTo(PLAYLIST_NAME2));
		assertThat(pl.get(0).getImageUrl(), equalTo(null));
		assertThat(pl.get(1).getImageUrl(), equalTo(ID1));
	}
}
