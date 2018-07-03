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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.Playlist;
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

	private SearchResult albums1;
	private SearchResult artists1;
	private SearchResult playlists1;
	private SearchResult tracks1;
	private SearchResult albums2;
	private SearchResult artists2;
	private SearchResult tracks2;
	private FeaturedPlaylists featuredPlaylists;
	private Paging<PlaylistSimplified> playlistsSpotifyFormat;
	private List<Playlist> playlistsOwnFormat;
	private Search search;

	private static final String ID1 = "abc123";
	private static final String ID2 = "123abc";
	private static final String PLAYLIST_NAME1 = "New Hits";
	private static final String PLAYLIST_NAME2 = "must popular hits";
	private static final String ARTIST_NAME1 = "David Guetta";
	private static final String ARTIST_NAME2 = "Justin Timberlake";

	@Mock
	private SpotifyAPICalls spotifyAPICalls;

	@BeforeEach
	public void init() {
		this.spotifyAPICalls = this.testFramework.mockService(SpotifyAPICalls.class);
		this.search = this.testFramework.setServiceUnderTest(Search.class);
		createSearchResults();

		createFeaturedPlaylist();
	}

	public void createFeaturedPlaylist() {
		PlaylistSimplified playlist1 = new PlaylistSimplified.Builder().setName("Flames").setUri("123")
				.setOwner(new User.Builder().setDisplayName("David Guetta").build()).build();
		PlaylistSimplified playlist2 = new PlaylistSimplified.Builder().setName("Say Something").setUri("abc")
				.setOwner(new User.Builder().setDisplayName("Justin Timberlake").build()).build();
		PlaylistSimplified[] playlistList = new PlaylistSimplified[2];
		playlistList[0] = playlist1;
		playlistList[1] = playlist2;
		featuredPlaylists = new FeaturedPlaylists.Builder()
				.setPlaylists(new Paging.Builder<PlaylistSimplified>().setItems(playlistList).build()).build();
	}

	public void initPlaylists() {
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
		this.playlistsOwnFormat.add(new Playlist(PLAYLIST_NAME1, null, ID1, null));
		this.playlistsOwnFormat.add(new Playlist(PLAYLIST_NAME2, null, ID2, ID1));
	}

	public void createSearchResults() {
		{
			Track track1 = new Track.Builder().setName("Flames").setUri("123")
					.setArtists(new ArtistSimplified.Builder().setName("David Guetta").build()).build();
			Track track2 = new Track.Builder().setName("Say Something").setUri("abc")
					.setArtists(new ArtistSimplified.Builder().setName("Justin Timberlake").build()).build();
			Track[] trackList = new Track[2];
			trackList[0] = track1;
			trackList[1] = track2;
			this.tracks1 = new SearchResult.Builder().setTracks(new Paging.Builder<Track>().setItems(trackList).build())
					.build();
			AlbumSimplified album1 = new AlbumSimplified.Builder().setName("Flames").setUri("123")
					.setArtists(new ArtistSimplified.Builder().setName("David Guetta").build()).build();
			AlbumSimplified album2 = new AlbumSimplified.Builder().setName("Say Something").setUri("abc")
					.setArtists(new ArtistSimplified.Builder().setName("Justin Timberlake").build()).build();
			AlbumSimplified[] albumList = new AlbumSimplified[2];
			albumList[0] = album1;
			albumList[1] = album2;
			this.albums1 = new SearchResult.Builder()
					.setAlbums(new Paging.Builder<AlbumSimplified>().setItems(albumList).build()).build();
			Artist artist1 = new Artist.Builder().setName("David Guetta").setUri("123").setGenres("Pop").build();
			Artist artist2 = new Artist.Builder().setName("Cro").setUri("abc").setGenres("Rock").build();
			Artist[] artistList = new Artist[2];
			artistList[0] = artist1;
			artistList[1] = artist2;
			this.artists1 = new SearchResult.Builder()
					.setArtists(new Paging.Builder<Artist>().setItems(artistList).build()).build();
			PlaylistSimplified playlist1 = new PlaylistSimplified.Builder().setName("Flames").setUri("123")
					.setOwner(new User.Builder().setDisplayName("David Guetta").build()).build();
			PlaylistSimplified playlist2 = new PlaylistSimplified.Builder().setName("Say Something").setUri("abc")
					.setOwner(new User.Builder().setDisplayName("Justin Timberlake").build()).build();
			PlaylistSimplified[] playlistList = new PlaylistSimplified[2];
			playlistList[0] = playlist1;
			playlistList[1] = playlist2;
			this.playlists1 = new SearchResult.Builder()
					.setPlaylists(new Paging.Builder<PlaylistSimplified>().setItems(playlistList).build()).build();
		}
		{
			Track track1 = new Track.Builder().setName("Flames").setUri("123")
					.setArtists(new ArtistSimplified.Builder().setName("David Guetta").build(),
							new ArtistSimplified.Builder().setName("Hans Dieter").build())
					.build();
			Track track2 = new Track.Builder().setName("Say Something").setUri("abc")
					.setArtists(new ArtistSimplified.Builder().setName("Justin Timberlake").build(),
							new ArtistSimplified.Builder().setName("Hans Dieter").build())
					.build();
			Track[] trackList = new Track[2];
			trackList[0] = track1;
			trackList[1] = track2;
			this.tracks2 = new SearchResult.Builder().setTracks(new Paging.Builder<Track>().setItems(trackList).build())
					.build();
			AlbumSimplified album1 = new AlbumSimplified.Builder().setName("Flames").setUri("123")
					.setArtists(new ArtistSimplified.Builder().setName("David Guetta").build(),
							new ArtistSimplified.Builder().setName("Hans Dieter").build())
					.build();
			AlbumSimplified album2 = new AlbumSimplified.Builder().setName("Say Something").setUri("abc")
					.setArtists(new ArtistSimplified.Builder().setName("Justin Timberlake").build(),
							new ArtistSimplified.Builder().setName("Hans Dieter").build())
					.build();
			AlbumSimplified[] albumList = new AlbumSimplified[2];
			albumList[0] = album1;
			albumList[1] = album2;
			this.albums2 = new SearchResult.Builder()
					.setAlbums(new Paging.Builder<AlbumSimplified>().setItems(albumList).build()).build();
			Artist artist1 = new Artist.Builder().setName("David Guetta").setUri("123").setGenres("Pop", "Rock")
					.build();
			Artist artist2 = new Artist.Builder().setName("Cro").setUri("abc").setGenres("Rock", "Electro").build();
			Artist[] artistList = new Artist[2];
			artistList[0] = artist1;
			artistList[1] = artist2;
			this.artists2 = new SearchResult.Builder()
					.setArtists(new Paging.Builder<Artist>().setItems(artistList).build()).build();
		}
	}

	@Test
	public void testSearchHashMapTrack() {
		List<Map<String, String>> result1 = this.search.createMap(tracks1, "track");
		List<Map<String, String>> result2 = this.search.createMap(tracks2, "track");
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_NAME), equalTo("Flames"));
		assertThat(result1.get(0).get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta"));
		assertThat(result1.get(1).get(SpotifyConstants.ITEM_NAME), equalTo("Say Something"));
		assertThat(result1.get(1).get(SpotifyConstants.ARTIST_NAME), equalTo("Justin Timberlake"));
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_URI), equalTo("123"));
		assertThat(result1.get(1).get(SpotifyConstants.ITEM_URI), equalTo("abc"));
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_TYPE), equalTo("track"));
		assertThat(result2.get(0).get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta, Hans Dieter"));
		assertThat(result2.get(1).get(SpotifyConstants.ARTIST_NAME), equalTo("Justin Timberlake, Hans Dieter"));
	}

	@Test
	public void testSearchHashMapPlaylist() {
		List<Map<String, String>> result1 = this.search.createMap(playlists1, "playlist");
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_NAME), equalTo("Flames"));
		assertThat(result1.get(0).get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta"));
		assertThat(result1.get(1).get(SpotifyConstants.ITEM_NAME), equalTo("Say Something"));
		assertThat(result1.get(1).get(SpotifyConstants.ARTIST_NAME), equalTo("Justin Timberlake"));
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_URI), equalTo("123"));
		assertThat(result1.get(1).get(SpotifyConstants.ITEM_URI), equalTo("abc"));
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_TYPE), equalTo("playlist"));
	}

	@Test
	public void testSearchHashMapAlbum() {
		List<Map<String, String>> result1 = this.search.createMap(albums1, "album");
		List<Map<String, String>> result2 = this.search.createMap(albums2, "album");
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_NAME), equalTo("Flames"));
		assertThat(result1.get(0).get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta"));
		assertThat(result1.get(1).get(SpotifyConstants.ITEM_NAME), equalTo("Say Something"));
		assertThat(result1.get(1).get(SpotifyConstants.ARTIST_NAME), equalTo("Justin Timberlake"));
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_URI), equalTo("123"));
		assertThat(result1.get(1).get(SpotifyConstants.ITEM_URI), equalTo("abc"));
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_TYPE), equalTo("album"));
		assertThat(result2.get(0).get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta, Hans Dieter"));
		assertThat(result2.get(1).get(SpotifyConstants.ARTIST_NAME), equalTo("Justin Timberlake, Hans Dieter"));
	}

	@Test
	public void testSearchHashMapArtist() {
		List<Map<String, String>> result1 = this.search.createMap(artists1, "artist");
		List<Map<String, String>> result2 = this.search.createMap(artists2, "artist");
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_NAME), equalTo("David Guetta"));
		assertThat(result1.get(1).get(SpotifyConstants.ITEM_NAME), equalTo("Cro"));
		assertThat(result1.get(0).get(SpotifyConstants.GENRE), equalTo("Pop"));
		assertThat(result1.get(1).get(SpotifyConstants.GENRE), equalTo("Rock"));
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_URI), equalTo("123"));
		assertThat(result1.get(1).get(SpotifyConstants.ITEM_URI), equalTo("abc"));
		assertThat(result1.get(0).get(SpotifyConstants.ITEM_TYPE), equalTo("artist"));
		assertThat(result2.get(0).get(SpotifyConstants.GENRE), equalTo("Pop, Rock"));
		assertThat(result2.get(1).get(SpotifyConstants.GENRE), equalTo("Rock, Electro"));
	}

	@Test
	public void testEmptyResult() {
		List<Map<String, String>> result = this.search.createMap(null, "artist");
		assertThat(result.isEmpty(), equalTo(true));
	}

	@Test
	public void testWrongType() {
		List<Map<String, String>> result = this.search.createMap(artists1, "artistsss");
		assertThat(result.isEmpty(), equalTo(true));
	}

	@Test
	public void testTypeCheck() {
		assertThat(search.typeCheck(SpotifyConstants.TYPE_ALBUM), equalTo(true));
		assertThat(search.typeCheck(SpotifyConstants.TYPE_ARTIST), equalTo(true));
		assertThat(search.typeCheck(SpotifyConstants.TYPE_TRACK), equalTo(true));
		assertThat(search.typeCheck(SpotifyConstants.TYPE_PLAYLIST), equalTo(true));
		assertThat(search.typeCheck(""), equalTo(false));
	}

	@Test
	public void testFeaturedPlaylists() {
		when(spotifyAPICalls.getFeaturedPlaylists(Search.SEARCH_LIMIT)).thenReturn(featuredPlaylists);
		List<Playlist> result = this.search.getFeaturedPlaylists(Search.SEARCH_LIMIT);
		assertThat(result.get(0).getName(), equalTo("Flames"));
		// assertThat(result.get(0).get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta"));
		assertThat(result.get(1).getName(), equalTo("Say Something"));
		// assertThat(result.get(1).get(SpotifyConstants.ARTIST_NAME), equalTo("Justin Timberlake"));
		assertThat(result.get(0).getUri(), equalTo("123"));
		assertThat(result.get(1).getUri(), equalTo("abc"));
	}

	@Test
	public void testEmptyFeaturedPlaylists() {
		when(spotifyAPICalls.getFeaturedPlaylists(Search.SEARCH_LIMIT)).thenReturn(null);
		assertThat(search.getFeaturedPlaylists(Search.SEARCH_LIMIT), equalTo(new ArrayList<>()));
	}

	@Test
	public void testSerachList() {
		when(spotifyAPICalls.searchInSpotify("Flames", "playlist", Search.SEARCH_LIMIT)).thenReturn(playlists1);
		List<Map<String, String>> result = this.search.searchList("Flames", "playlist", Search.SEARCH_LIMIT);
		assertThat(result.get(0).get(SpotifyConstants.ITEM_NAME), equalTo("Flames"));
		assertThat(result.get(0).get(SpotifyConstants.ARTIST_NAME), equalTo("David Guetta"));
		assertThat(result.get(1).get(SpotifyConstants.ITEM_NAME), equalTo("Say Something"));
		assertThat(result.get(1).get(SpotifyConstants.ARTIST_NAME), equalTo("Justin Timberlake"));
		assertThat(result.get(0).get(SpotifyConstants.ITEM_URI), equalTo("123"));
		assertThat(result.get(1).get(SpotifyConstants.ITEM_URI), equalTo("abc"));
		assertThat(result.get(0).get(SpotifyConstants.ITEM_TYPE), equalTo("playlist"));
		when(spotifyAPICalls.searchInSpotify("", "playlist", Search.SEARCH_LIMIT)).thenReturn(null);
		List<Map<String, String>> result2 = this.search.searchList("", "playlist", Search.SEARCH_LIMIT);
		assertThat(result2.isEmpty(), equalTo(true));
	}

	@Test
	public void testGetUsersPlaylists() {
		initPlaylists();
		List<Playlist> pl;
		when(this.spotifyAPICalls.getOwnPlaylists(2)).thenReturn(this.playlistsSpotifyFormat);
		pl = this.search.getOwnPlaylists(2);
		assertThat(pl.get(0).getUri(), equalTo(ID1));
		assertThat(pl.get(1).getUri(), equalTo(ID2));
		assertThat(pl.get(0).getName(), equalTo(PLAYLIST_NAME1));
		assertThat(pl.get(1).getName(), equalTo(PLAYLIST_NAME2));
		assertThat(pl.get(0).getImageUrl(), equalTo(null));
		assertThat(pl.get(1).getImageUrl(), equalTo(ID1));
	}

	@Test
	public void testGetFeturedPlaylists() {
		initPlaylists();
		FeaturedPlaylists featuredPls = new FeaturedPlaylists.Builder().setPlaylists(this.playlistsSpotifyFormat)
				.build();
		List<Playlist> pl;
		when(this.spotifyAPICalls.getFeaturedPlaylists(2)).thenReturn(featuredPls);
		pl = this.search.getFeaturedPlaylists(2);
		assertThat(pl.get(0).getUri(), equalTo(ID1));
		assertThat(pl.get(1).getUri(), equalTo(ID2));
		assertThat(pl.get(0).getName(), equalTo(PLAYLIST_NAME1));
		assertThat(pl.get(1).getName(), equalTo(PLAYLIST_NAME2));
		assertThat(pl.get(0).getImageUrl(), equalTo(null));
		assertThat(pl.get(1).getImageUrl(), equalTo(ID1));
	}
}
