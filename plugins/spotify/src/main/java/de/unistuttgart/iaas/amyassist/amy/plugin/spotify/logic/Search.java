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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wrapper.spotify.enums.ModelObjectType;
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
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.SearchTypes;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.SpotifyAPICalls;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.SpotifyConstants;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;

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

	
	private static final String SPOTIFY_URI_STORAGE = "spotify_uri_";
	/**
	 * search limit for a few search queries
	 */
	public static final int SEARCH_LIMIT = 10;

	/**
	 * create a search query and output a list with search results
	 * 
	 * @param searchItem
	 *            tesxt to search
	 * @param type
	 *            what is searched for (track, artist, album, playlist)
	 * @param limit
	 *            max length of the result list
	 * @return a list with search result entries. every entry has a Map with different attributes. e.g. artist, ...
	 *         every entry has a uri to the song an the type (track, artist, album, palylist)
	 */
	public List<Map<String, String>> searchList(String searchItem, String type, int limit) {
		List<Map<String, String>> result = new ArrayList<>();
		if (typeCheck(type)) {
			SearchResult searchResult = this.spotifyAPICalls.searchInSpotify(searchItem, type, limit);
			if (searchResult != null) {
				result = createMap(searchResult, type);
			}
		}
		writeUrisToStorageMap(result, SearchTypes.NORMAL_SEARCH);
		return result;
	}

	/**
	 * 
	 * @param searchResult
	 *            the search result from a query
	 * @param type
	 *            artist, track, album or palylist
	 * @return a list with search result entries. every entry has a Map with different attributes. e.g. artist, ...
	 *         every entry has a uri to the song an the type (track, artist, album, palylist)
	 */
	public List<Map<String, String>> createMap(SearchResult searchResult, String type) {
		List<Map<String, String>> resultList = new ArrayList<>();
		if (searchResult != null) {
			switch (type.toLowerCase()) {
			case SpotifyConstants.TYPE_TRACK:
				return createTrackOutput(searchResult.getTracks());
			case SpotifyConstants.TYPE_PLAYLIST:
				return createPlaylistOutput(searchResult.getPlaylists());
			case SpotifyConstants.TYPE_ARTIST:
				return createArtistOutput(searchResult.getArtists());
			case SpotifyConstants.TYPE_ALBUM:
				return createAlbumOutput(searchResult.getAlbums());
			default:
				return resultList;
			}
		}
		return resultList;
	}

	/**
	 * create a List with all items in a Paging Object when the type is a track
	 * 
	 * @param searchResult
	 *            the search result from a query
	 * @return a list with search result entries. every entry has a Map with different attributes. e.g. artist, ...
	 *         every entry has a uri to the song an the type track
	 */
	public List<Map<String, String>> createTrackOutput(Paging<Track> searchResult) {
		HashMap<String, String> entry;
		List<Map<String, String>> result = new ArrayList<>();
		if (searchResult != null) {
			for (Track track : searchResult.getItems()) {
				entry = new HashMap<>();
				if (track.getName() != null) {
					entry.put(SpotifyConstants.ITEM_NAME, track.getName());
				}
				String artistName = "";
				for (int j = 0; j < track.getArtists().length - 1; j++) {
					artistName = artistName.concat(track.getArtists()[j].getName()).concat(", ");
				}
				if (0 < track.getArtists().length) {
					artistName = artistName.concat(track.getArtists()[track.getArtists().length - 1].getName());
				}
				entry.put(SpotifyConstants.ARTIST_NAME, artistName);
				entry.put(SpotifyConstants.ITEM_URI, track.getUri());
				entry.put(SpotifyConstants.ITEM_TYPE, SpotifyConstants.TYPE_TRACK);
				result.add(entry);
			}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is a album
	 * 
	 * @param searchResult
	 *            the search result from a query
	 * @return a list with search result entries. every entry has a Map with different attributes. e.g. artist, ...
	 *         every entry has a uri to the song an the type album
	 */
	public List<Map<String, String>> createAlbumOutput(Paging<AlbumSimplified> searchResult) {
		HashMap<String, String> entry;
		List<Map<String, String>> result = new ArrayList<>();
		if (searchResult != null) {
			for (AlbumSimplified album : searchResult.getItems()) {
				entry = new HashMap<>();
				if (album.getName() != null) {
					entry.put(SpotifyConstants.ITEM_NAME, album.getName());
				}
				String artists = "";
				for (int j = 0; j < album.getArtists().length - 1; j++) {
					artists = artists.concat(album.getArtists()[j].getName()).concat(", ");
				}
				if (0 < album.getArtists().length) {
					artists = artists.concat(album.getArtists()[album.getArtists().length - 1].getName());
				}
				entry.put(SpotifyConstants.ARTIST_NAME, artists);
				entry.put(SpotifyConstants.ITEM_URI, album.getUri());
				entry.put(SpotifyConstants.ITEM_TYPE, SpotifyConstants.TYPE_ALBUM);
				result.add(entry);
			}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is a artist
	 * 
	 * @param searchResult
	 *            the search result from a query
	 * @return a list with search result entries. every entry has a Map with different attributes. e.g. artist, ...
	 *         every entry has a uri to the song an the type artist
	 */
	public List<Map<String, String>> createArtistOutput(Paging<Artist> searchResult) {
		HashMap<String, String> entry;
		List<Map<String, String>> result = new ArrayList<>();
		if (searchResult != null) {
			for (Artist artist : searchResult.getItems()) {
				entry = new HashMap<>();
				if (artist.getName() != null) {
					entry.put(SpotifyConstants.ITEM_NAME, artist.getName());
				}
				String genre = "";
				for (int j = 0; j < artist.getGenres().length - 1; j++) {
					genre = genre.concat(artist.getGenres()[j]).concat(", ");
				}
				if (0 < artist.getGenres().length) {
					genre = genre.concat(artist.getGenres()[artist.getGenres().length - 1]);
				}
				entry.put(SpotifyConstants.GENRE, genre);
				entry.put(SpotifyConstants.ITEM_URI, artist.getUri());
				entry.put(SpotifyConstants.ITEM_TYPE, SpotifyConstants.TYPE_ARTIST);
				result.add(entry);
			}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is a playlist
	 * 
	 * @param searchResult
	 *            the search result from a query
	 * @return a list with search result entries. every entry has a Map with different attributes. e.g. artist, ...
	 *         every entry has a uri to the song an the type playlist
	 */
	public List<Map<String, String>> createPlaylistOutput(Paging<PlaylistSimplified> searchResult) {
		HashMap<String, String> entry;
		List<Map<String, String>> result = new ArrayList<>();
		if (searchResult != null) {
			for (PlaylistSimplified playlist : searchResult.getItems()) {
				entry = new HashMap<>();
				if (playlist.getName() != null) {
					entry.put(SpotifyConstants.ITEM_NAME, playlist.getName());
				}

				if (playlist.getOwner().getDisplayName() != null) {
					entry.put(SpotifyConstants.ARTIST_NAME, playlist.getOwner().getDisplayName());
				}
				entry.put(SpotifyConstants.ITEM_URI, playlist.getUri());
				entry.put(SpotifyConstants.ITEM_TYPE, SpotifyConstants.TYPE_PLAYLIST);
				result.add(entry);
			}
		}
		return result;
	}

	/**
	 * get a list from user created or followed playlists
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @return a list from Playlists
	 */
	public List<PlaylistEntity> getOwnPlaylists(int limit) {
		Paging<PlaylistSimplified> playlists = this.spotifyAPICalls.getOwnPlaylists(limit);
		return generatePlaylistsOutput(playlists.getItems(), SearchTypes.USER_PLAYLISTS);
	}

	/**
	 * get a list from featured playlists
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @return a list from Playlists
	 */
	public List<PlaylistEntity> getFeaturedPlaylists(int limit) {
		FeaturedPlaylists playlists = this.spotifyAPICalls.getFeaturedPlaylists(limit);
		if (playlists != null) {
			return generatePlaylistsOutput(playlists.getPlaylists().getItems(), SearchTypes.FEATURED_PLAYLISTS);
		}
		return new ArrayList<>();
	}

	private List<PlaylistEntity> generatePlaylistsOutput(PlaylistSimplified[] playlists, SearchTypes type) {
		ArrayList<PlaylistEntity> result = new ArrayList<>();
		if (playlists != null) {
			for (PlaylistSimplified playlist : playlists) {
				if (playlist.getImages() != null && playlist.getImages().length > 0) {
					result.add(new PlaylistEntity(playlist.getName(), null, playlist.getUri(),
							playlist.getImages()[0].getUrl()));
				} else {
					result.add(new PlaylistEntity(playlist.getName(), null, playlist.getUri(), null));
				}

			}
		}
		writeUrisToStorage(result, type);
		return result;
	}

	/**
	 * check a string if is in the ModelObjectType true, else false
	 * 
	 * @param type
	 *            artist, album, track or playlist
	 * @return if the param type one of the for types then return true, else false
	 */
	public boolean typeCheck(String type) {
		for (int i = 0; i < ModelObjectType.values().length; i++) {
			if (ModelObjectType.values()[i].toString().equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * write all Uris from a list of Playlists to the storage. type is needed to store different search queries. For
	 * example getOwnPlaylists() or getFeaturedPlaylists()
	 * 
	 * @param playlists
	 *            playlists to write uris
	 * @param type
	 *            to write to right position
	 */
	private void writeUrisToStorage(List<PlaylistEntity> playlists, SearchTypes type) {
	/*	deleteUrisFromStroage(type);
		for (int i = 0; i < playlists.size(); i++) {
			this.storage.put(SPOTIFY_URI_STORAGE.concat(type.toString()).concat("_").concat(String.valueOf(i)),
					playlists.get(i).getUri());
		}*/
	}

	private void writeUrisToStorageMap(List<Map<String, String>> playlists, SearchTypes type) {
		deleteUrisFromStroage(type);
		for (int i = 0; i < playlists.size(); i++) {
			this.storage.put(SPOTIFY_URI_STORAGE.concat(type.toString()).concat("_").concat(String.valueOf(i)),
					playlists.get(i).get(SpotifyConstants.ITEM_URI));
		}
	}

	/**
	 * delete all saved uris from the storage of the given query
	 * 
	 * @param type
	 *            of the query items to delete
	 */
	private void deleteUrisFromStroage(SearchTypes type) {
	/*	int i = 0;
		while (this.storage
				.has(SPOTIFY_URI_STORAGE.concat(type.toString()).concat("_").concat(String.valueOf(i)))) {
			this.storage.delete(SPOTIFY_URI_STORAGE.concat(type.toString()).concat("_").concat(String.valueOf(i)));
			i++;
		}*/
	}

	/**
	 * restore all uris from a search
	 * 
	 * @param type
	 *            to restore
	 * @return a list of uris
	 */
	public List<String> restoreUris(SearchTypes type) {
		List<String> result = new ArrayList<>();
		/*int i = 0;
		while (this.storage
				.has(SPOTIFY_URI_STORAGE.concat(type.toString()).concat("_").concat(String.valueOf(i)))) {
			result.add(this.storage
					.get(SPOTIFY_URI_STORAGE.concat(type.toString()).concat("_").concat(String.valueOf(i))));
			i++;
		}*/
		return result;
	}
}
