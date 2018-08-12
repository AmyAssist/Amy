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

import java.util.ArrayList;
import java.util.List;

import com.wrapper.spotify.model_objects.special.FeaturedPlaylists;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.SpotifyAPICalls;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.AlbumEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.ArtistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.TrackEntity;

/**
 * This class create search query to the spotify web api and parse the results in a String or in a Hashmap with
 * different attributes
 * 
 * @author Lars Buttgereit
 */
@Service
public class Search {
	@Reference
	private SpotifyAPICalls spotifyAPICalls;
	@Reference
	private IStorage storage;

	private List<TrackEntity> trackSearchResults = new ArrayList<>();
	private List<AlbumEntity> albumSearchResults = new ArrayList<>();
	private List<ArtistEntity> artistSearchResults = new ArrayList<>();
	private List<PlaylistEntity> playlistSearchResults = new ArrayList<>();

	private List<PlaylistEntity> featuredPlaylists = new ArrayList<>();
	private List<PlaylistEntity> ownPlaylists = new ArrayList<>();

	/**
	 * search limit for a few search queries
	 */
	public static final int SEARCH_LIMIT = 10;

	/**
	 * create a search query and output a list with search results from type track
	 * 
	 * @param searchItem
	 *            text to search
	 * @param limit
	 *            max length of the result list
	 * @return a list with tracks
	 */
	public List<TrackEntity> searchforTracks(String searchItem, int limit) {
		SearchResult searchResult = this.spotifyAPICalls.searchInSpotify(searchItem, "track", limit);
		if (searchResult != null) {
			this.trackSearchResults = createTrackData(searchResult.getTracks());
		} else {
			this.trackSearchResults = new ArrayList<>();
		}
		return this.trackSearchResults;
	}

	/**
	 * create a search query and output a list with search results from type track
	 * 
	 * @param searchItem
	 *            text to search
	 * @param limit
	 *            max length of the result list
	 * @return a list with tracks
	 */
	public List<PlaylistEntity> searchforPlaylists(String searchItem, int limit) {
		SearchResult searchResult = this.spotifyAPICalls.searchInSpotify(searchItem, "playlist", limit);
		if (searchResult != null) {
			this.playlistSearchResults = createPlaylistData(searchResult.getPlaylists().getItems());
		} else {
			this.playlistSearchResults = new ArrayList<>();
		}
		return this.playlistSearchResults;
	}

	/**
	 * create a search query and output a list with search results from type track
	 * 
	 * @param searchItem
	 *            text to search
	 * @param limit
	 *            max length of the result list
	 * @return a list with tracks
	 */
	public List<AlbumEntity> searchforAlbums(String searchItem, int limit) {
		SearchResult searchResult = this.spotifyAPICalls.searchInSpotify(searchItem, "album", limit);
		if (searchResult != null) {
			this.albumSearchResults = createAlbumData(searchResult.getAlbums());
		} else {
			this.albumSearchResults = new ArrayList<>();
		}
		return this.albumSearchResults;
	}

	/**
	 * create a search query and output a list with search results from type track
	 * 
	 * @param searchItem
	 *            text to search
	 * @param limit
	 *            max length of the result list
	 * @return a list with tracks
	 */
	public List<ArtistEntity> searchforArtists(String searchItem, int limit) {
		SearchResult searchResult = this.spotifyAPICalls.searchInSpotify(searchItem, "artist", limit);
		if (searchResult != null) {
			this.artistSearchResults = createArtistData(searchResult.getArtists());
		} else {
			this.artistSearchResults = new ArrayList<>();
		}
		return this.artistSearchResults;
	}

	/**
	 * create a List with tracks out of the search results from spotify
	 * 
	 * @param searchResult
	 *            the search result from a query
	 * @return a list with track entities
	 */
	public List<TrackEntity> createTrackData(Paging<Track> searchResult) {
		TrackEntity trackEntity;
		List<TrackEntity> result = new ArrayList<>();
		if (searchResult != null) {
			for (Track track : searchResult.getItems()) {
				trackEntity = new TrackEntity();
				trackEntity.setName(track.getName());

				String[] artistNames = new String[track.getArtists().length];
				for (int i = 0; i < track.getArtists().length; i++) {
					artistNames[i] = track.getArtists()[i].getName();
				}
				trackEntity.setArtists(artistNames);
				trackEntity.setUri(track.getUri());
				trackEntity.setDurationInMs(track.getDurationMs());
				result.add(trackEntity);
			}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is album
	 * 
	 * @param searchResult
	 *            the search result from a query
	 * @return a list with album entities
	 */
	private List<AlbumEntity> createAlbumData(Paging<AlbumSimplified> searchResult) {
		AlbumEntity albumEntity;
		List<AlbumEntity> result = new ArrayList<>();
		if (searchResult != null) {
			for (AlbumSimplified album : searchResult.getItems()) {
				albumEntity = new AlbumEntity();
				albumEntity.setName(album.getName());
				String[] artistNames = new String[album.getArtists().length];
				for (int i = 0; i < album.getArtists().length; i++) {
					artistNames[i] = album.getArtists()[i].getName();
				}
				if (album.getImages() != null && album.getImages().length > 0) {
					albumEntity.setImageUrl(album.getImages()[0].getUrl());
				}
				albumEntity.setArtists(artistNames);
				albumEntity.setUri(album.getUri());
				result.add(albumEntity);
			}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is artist
	 * 
	 * @param searchResult
	 *            the search result from a query
	 * @return a list with artist entities
	 */
	private List<ArtistEntity> createArtistData(Paging<Artist> searchResult) {
		List<ArtistEntity> result = new ArrayList<>();
		ArtistEntity artistEntity;
		if (searchResult != null) {
			for (Artist artist : searchResult.getItems()) {
				artistEntity = new ArtistEntity();
				if (artist.getImages() != null && artist.getImages().length > 0) {
					artistEntity.setImageUrl(artist.getImages()[0].getUrl());
				}
				artistEntity.setName(artist.getName());
				artistEntity.setGenre(artist.getGenres());
				artistEntity.setUri(artist.getUri());
				result.add(artistEntity);
			}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is playlist
	 * 
	 * @param playlists
	 *            searchResult the search result from a query
	 * @return a list with playlist entities
	 */
	private List<PlaylistEntity> createPlaylistData(PlaylistSimplified[] playlists) {
		ArrayList<PlaylistEntity> result = new ArrayList<>();
		if (playlists != null) {
			for (PlaylistSimplified playlist : playlists) {
				PlaylistEntity playlistEntity = new PlaylistEntity();
				if (playlist.getImages() != null && playlist.getImages().length > 0) {
					playlistEntity.setImageUrl(playlist.getImages()[0].getUrl());
				}
				if (playlist.getOwner() != null && playlist.getOwner().getDisplayName() != null) {
					playlistEntity.setPlaylistCreator(playlist.getOwner().getDisplayName());
				}
				playlistEntity.setUri(playlist.getUri());
				playlistEntity.setName(playlist.getName());
				result.add(playlistEntity);
			}
		}
		return result;
	}

	/**
	 * search the playlists that from the user created or followed
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @return a list from Playlists
	 */
	public List<PlaylistEntity> searchOwnPlaylists(int limit) {
		Paging<PlaylistSimplified> playlists = this.spotifyAPICalls.getOwnPlaylists(limit);
		if (playlists != null) {
			this.ownPlaylists = createPlaylistData(playlists.getItems());
		} else {
			this.ownPlaylists = new ArrayList<>();
		}
		return this.ownPlaylists;
	}

	/**
	 * search for featured playlists
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @return a list from Playlists
	 */
	public List<PlaylistEntity> searchFeaturedPlaylists(int limit) {
		FeaturedPlaylists playlists = this.spotifyAPICalls.getFeaturedPlaylists(limit);
		if (playlists != null) {
			this.featuredPlaylists = createPlaylistData(playlists.getPlaylists().getItems());
		} else {
			this.featuredPlaylists = new ArrayList<>();
		}
		return this.featuredPlaylists;
	}

	/**
	 * Get's {@link #trackSearchResults trackSearchResults}
	 * 
	 * @return trackSearchResults
	 */
	public List<TrackEntity> getTrackSearchResults() {
		return this.trackSearchResults;
	}

	/**
	 * Get's {@link #albumSearchResults albumSearchResults}
	 * 
	 * @return albumSearchResults
	 */
	public List<AlbumEntity> getAlbumSearchResults() {
		return this.albumSearchResults;
	}

	/**
	 * Get's {@link #artistSearchResults artistSearchResults}
	 * 
	 * @return artistSearchResults
	 */
	public List<ArtistEntity> getArtistSearchResults() {
		return this.artistSearchResults;
	}

	/**
	 * Get's {@link #playlistSearchResults playlistSearchResults}
	 * 
	 * @return playlistSearchResults
	 */
	public List<PlaylistEntity> getPlaylistSearchResults() {
		return this.playlistSearchResults;
	}

	/**
	 * Get's {@link #featuredPlaylists featuredPlaylists}
	 * 
	 * @return featuredPlaylists
	 */
	public List<PlaylistEntity> getFeaturedPlaylists() {
		return this.featuredPlaylists;
	}

	/**
	 * Get's {@link #ownPlaylists ownPlaylists}
	 * 
	 * @return ownPlaylists
	 */
	public List<PlaylistEntity> getOwnPlaylists() {
		return this.ownPlaylists;
	}
}
