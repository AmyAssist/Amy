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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
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
	private PlayerLogic playerLogic;

	private SearchResult albums1;
	private SearchResult artists1;
	private SearchResult playlists1;
	private SearchResult tracks1;
	private SearchResult albums2;
	private SearchResult artists2;
	private SearchResult playlists2;
	private SearchResult tracks2;
	private Search search;

	@Mock
	Authorization auth;

	@BeforeEach
	public void init() {
		createSearchResults();
		search = new Search(auth);
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
		List<Map<String, String>> erg1 = this.search.createMap(tracks1, "track");
		List<Map<String, String>> erg2 = this.search.createMap(tracks2, "track");
		assertEquals("Flames", erg1.get(0).get(SpotifyConstants.ITEM_NAME));
		assertEquals("David Guetta", erg1.get(0).get(SpotifyConstants.ARTIST_NAME));
		assertEquals("Say Something",  erg1.get(1).get(SpotifyConstants.ITEM_NAME));
		assertEquals("Justin Timberlake",  erg1.get(1).get(SpotifyConstants.ARTIST_NAME));
		assertEquals("123",  erg1.get(0).get(SpotifyConstants.ITEM_URI));
		assertEquals("abc",  erg1.get(1).get(SpotifyConstants.ITEM_URI));
		assertEquals("track",  erg1.get(0).get(SpotifyConstants.ITEM_TYPE));
		assertEquals("David Guetta, Hans Dieter",  erg2.get(0).get(SpotifyConstants.ARTIST_NAME));
		assertEquals("Justin Timberlake, Hans Dieter",  erg2.get(1).get(SpotifyConstants.ARTIST_NAME));	
	}
	
	@Test
	public void testSearchHashMapPlaylist() {
		List<Map<String, String>> erg1 = this.search.createMap(playlists1, "playlist");
		assertEquals("Flames", erg1.get(0).get(SpotifyConstants.ITEM_NAME));
		assertEquals("David Guetta", erg1.get(0).get(SpotifyConstants.ARTIST_NAME));
		assertEquals("Say Something",  erg1.get(1).get(SpotifyConstants.ITEM_NAME));
		assertEquals("Justin Timberlake",  erg1.get(1).get(SpotifyConstants.ARTIST_NAME));
		assertEquals("123",  erg1.get(0).get(SpotifyConstants.ITEM_URI));
		assertEquals("abc",  erg1.get(1).get(SpotifyConstants.ITEM_URI));
		assertEquals("playlist",  erg1.get(0).get(SpotifyConstants.ITEM_TYPE));
	}
	
	@Test
	public void testSearchHashMapAlbum() {
		List<Map<String, String>> erg1 = this.search.createMap(albums1, "album");
		List<Map<String, String>> erg2 = this.search.createMap(albums2, "album");
		assertEquals("Flames", erg1.get(0).get(SpotifyConstants.ITEM_NAME));
		assertEquals("David Guetta", erg1.get(0).get(SpotifyConstants.ARTIST_NAME));
		assertEquals("Say Something",  erg1.get(1).get(SpotifyConstants.ITEM_NAME));
		assertEquals("Justin Timberlake",  erg1.get(1).get(SpotifyConstants.ARTIST_NAME));
		assertEquals("123",  erg1.get(0).get(SpotifyConstants.ITEM_URI));
		assertEquals("abc",  erg1.get(1).get(SpotifyConstants.ITEM_URI));
		assertEquals("album",  erg1.get(0).get(SpotifyConstants.ITEM_TYPE));
		assertEquals("David Guetta, Hans Dieter",  erg2.get(0).get(SpotifyConstants.ARTIST_NAME));
		assertEquals("Justin Timberlake, Hans Dieter",  erg2.get(1).get(SpotifyConstants.ARTIST_NAME));	
	}
	
	@Test
	public void testSearchHashMapArtist() {
		List<Map<String, String>> erg1 = this.search.createMap(artists1, "artist");
		List<Map<String, String>> erg2 = this.search.createMap(artists2, "artist");
		assertEquals("David Guetta", erg1.get(0).get(SpotifyConstants.ITEM_NAME));
		assertEquals("Cro",  erg1.get(1).get(SpotifyConstants.ITEM_NAME));
		assertEquals("Pop", erg1.get(0).get(SpotifyConstants.GENRE));
		assertEquals("Rock", erg1.get(1).get(SpotifyConstants.GENRE));
		assertEquals("123",  erg1.get(0).get(SpotifyConstants.ITEM_URI));
		assertEquals("abc",  erg1.get(1).get(SpotifyConstants.ITEM_URI));
		assertEquals("artist",  erg1.get(0).get(SpotifyConstants.ITEM_TYPE));
		assertEquals("Pop, Rock", erg2.get(0).get(SpotifyConstants.GENRE));
		assertEquals("Rock, Electro", erg2.get(1).get(SpotifyConstants.GENRE));

	}
}
