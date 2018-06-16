/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.FeaturedPlaylists;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.browse.GetListOfFeaturedPlaylistsRequest;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;

/**
 * This class create search query to the spotify web api and parse the results
 * in a String or in a Hashmap with different attributes
 * 
 * @author Lars Buttgereit
 */
public class Search {
	private Authorization auth;

	public Search(Authorization auth) {
		this.auth = auth;
	}

	/**
	 * create a search query and output a list with search results
	 * 
	 * @param searchItem
	 * @param type
	 *            what is searched for (track, artist, album, playlist)
	 * @param limit
	 *            max length of the result list
	 * @return a list with search result entries. every entry has a Map with
	 *         different attributes. e.g. artist, ... every entry has a uri to the
	 *         song an the type (track, artist, album, palylist)
	 * @throws IOException
	 * @throws SpotifyWebApiException
	 */
	public List<Map<String, String>> searchList(String searchItem, String type, int limit)
			throws SpotifyWebApiException, IOException {
		SearchResult searchResult = searchInSpotify(searchItem, type, limit);
		return createMap(searchResult, type);
	}

	public List<Map<String, String>> createMap(SearchResult searchResult, String type) {
		List<Map<String, String>> resultList = new ArrayList<>();
		if (searchResult != null) {
			switch (type.toLowerCase()) {
			case SpotifyConstants.TYPE_TRACK:
				return createTrackSearchResult(searchResult, type);
			case SpotifyConstants.TYPE_PLAYLIST:
				return createPlaylistSearchResult(searchResult, type);
			case SpotifyConstants.TYPE_ARTIST:
				return createArtistSearchResult(searchResult, type);
			case SpotifyConstants.TYPE_ALBUM:
				return createAlbumSearchResult(searchResult, type);
			default:
				break;

			}
		}
		return resultList;
	}

	/**
	 * create a List with all SearchResults when the type is a track
	 * 
	 * @param searchResult
	 * @return a list with search result entries. every entry has a Map with
	 *         different attributes. e.g. artist, ... every entry has a uri to the
	 *         song an the type track
	 */
	private List<Map<String, String>> createTrackSearchResult(SearchResult searchResult, String type) {
		HashMap<String, String> entry;
		List<Map<String, String>> result = new ArrayList<>();
		if (type.equals(SpotifyConstants.TYPE_TRACK)) {
			for (Track track : searchResult.getTracks().getItems()) {
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
	 * @return a list with search result entries. every entry has a Map with
	 *         different attributes. e.g. artist, ... every entry has a uri to the
	 *         song an the type album
	 */
	private List<Map<String, String>> createAlbumSearchResult(SearchResult searchResult, String type) {
		HashMap<String, String> entry;
		List<Map<String, String>> result = new ArrayList<>();
		if (type.equals(SpotifyConstants.TYPE_ALBUM)) {
			for (AlbumSimplified album : searchResult.getAlbums().getItems()) {
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
	 * @return a list with search result entries. every entry has a Map with
	 *         different attributes. e.g. artist, ... every entry has a uri to the
	 *         song an the type artist
	 */
	private List<Map<String, String>> createArtistSearchResult(SearchResult searchResult, String type) {
		HashMap<String, String> entry;
		List<Map<String, String>> result = new ArrayList<>();
		if (type.equals(SpotifyConstants.TYPE_ARTIST)) {
			for (Artist artist : searchResult.getArtists().getItems()) {
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
	 * @return a list with search result entries. every entry has a Map with
	 *         different attributes. e.g. artist, ... every entry has a uri to the
	 *         song an the type playlist
	 */
	private List<Map<String, String>> createPlaylistSearchResult(SearchResult searchResult, String type) {
		HashMap<String, String> entry;
		List<Map<String, String>> result = new ArrayList<>();
		if (type.equals(SpotifyConstants.TYPE_PLAYLIST)) {
			for (PlaylistSimplified playlist : searchResult.getPlaylists().getItems()) {
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
	 * create a search query for spotify.
	 * 
	 * @param searchItem
	 * @param type
	 *            type of the search (artis, track, album, playlist)
	 * @param limit
	 *            how many entry the result maximal have
	 * @return a object of the type SearchResult from the spoitfy library
	 */
	private SearchResult searchInSpotify(String searchItem, String type, int limit)
			throws SpotifyWebApiException, IOException {
		SearchResult searchResult;
		if (typeCheck(type)) {
			SearchItemRequest searchItemRequest = this.auth.getSpotifyApi().searchItem(searchItem, type.toLowerCase())
					.limit(Integer.valueOf(limit)).offset(Integer.valueOf(0)).build();
			searchResult = searchItemRequest.execute();
			return searchResult;

		}
		return null;
	}

	/**
	 * this method get a list from 10 featured playlist back
	 * 
	 * @return a list with search result entries. every entry has a Map with
	 *         different attributes. e.g. artist, ... every entry has a uri to the
	 *         song an the type playlist
	 * @throws IOException
	 * @throws SpotifyWebApiException
	 * 
	 */
	public List<Map<String, String>> getFeaturedPlaylists() throws SpotifyWebApiException, IOException {
		List<Map<String, String>> result = new ArrayList<>();
		HashMap<String, String> entry;
		GetListOfFeaturedPlaylistsRequest getListOfFeaturedPlaylistsRequest = this.auth.getSpotifyApi()
				.getListOfFeaturedPlaylists().country(CountryCode.DE).limit(Integer.valueOf(10))
				.offset(Integer.valueOf(0)).build();
		FeaturedPlaylists featuredPlaylists = getListOfFeaturedPlaylistsRequest.execute();
		for (PlaylistSimplified playlist : featuredPlaylists.getPlaylists().getItems()) {
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

		return result;
	}

	public static boolean typeCheck(String type) {
		for (int i = 0; i < ModelObjectType.values().length; i++) {
			if (ModelObjectType.values()[i].toString().equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;

	}
}
