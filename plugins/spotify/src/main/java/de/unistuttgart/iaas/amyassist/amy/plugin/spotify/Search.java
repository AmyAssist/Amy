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
	public static final String TYPE_ARTIST = "artist";
	public static final String TYPE_TRACK = "track";
	public static final String TYPE_PLAYLIST = "playlist";
	public static final String TYPE_ALBUM = "album";
	public static final String ITEM_NAME = "name";
	public static final String GENRE = "genre";
	public static final String ARTIST_NAME = "artisName";
	public static final String ITEM_URI = "uri";
	public static final String ITEM_TYPE = "type";

	public Search(Authorization auth) {
		this.auth = auth;
	}

	/**
	 * Start a Search in the Spotify library.
	 * 
	 * @param searchItem
	 * @param type
	 *            what is searched for (track, artist, album, playlist)
	 * @param limit
	 *            max length of the result list
	 * @param spotifyApi
	 * @return a array list of the result entrys. every entry has at 0 the uri of
	 *         the item and at 1 a output String with information about the item and
	 *         at 2 the type of the item. if no element found a empty list is
	 *         returned
	 */
	public List<String[]> searchAnything(String searchItem, String type, int limit) {
		SearchResult searchResult = searchInSpotify(searchItem, type, limit);
		return createSpeechList(searchResult, type);
	}

	public List<String[]> createSpeechList(SearchResult searchResult, String type) {
		ArrayList<String[]> resultList = new ArrayList<>();
		if (searchResult != null) {
			String[] entry;
			String outputString;
			switch (type.toLowerCase()) {
			case TYPE_TRACK:
				for (int i = 0; i < searchResult.getTracks().getItems().length; i++) {
					entry = new String[3];
					// generate a string with track name and artists
					outputString = "";
					outputString = outputString + i + ". Track name is "
							+ searchResult.getTracks().getItems()[i].getName() + " by ";
					for (int j = 0; j < searchResult.getTracks().getItems()[i].getArtists().length - 1; j++) {
						outputString = outputString + searchResult.getTracks().getItems()[i].getArtists()[j].getName()
								+ " and ";
					}
					if (0 < searchResult.getTracks().getItems()[i].getArtists().length) {
						outputString = outputString + searchResult.getTracks().getItems()[i]
								.getArtists()[searchResult.getTracks().getItems()[i].getArtists().length - 1].getName();
					}
					entry[1] = outputString;
					entry[0] = searchResult.getTracks().getItems()[i].getUri();
					entry[2] = "track";
					resultList.add(entry);
				}
				return resultList;
			case TYPE_PLAYLIST:
				for (int i = 0; i < searchResult.getPlaylists().getItems().length; i++) {
					entry = new String[3];
					// generate a string with playlist name and owner name
					outputString = "";
					outputString = outputString + i + ". Playlist name is "
							+ searchResult.getPlaylists().getItems()[i].getName() + " created by ";
					if (searchResult.getPlaylists().getItems()[i].getOwner().getDisplayName() != null) {
						outputString = outputString
								+ searchResult.getPlaylists().getItems()[i].getOwner().getDisplayName();
					}
					entry[1] = outputString;
					entry[0] = searchResult.getPlaylists().getItems()[i].getUri();
					entry[2] = "playlist";
					resultList.add(entry);
				}
				return resultList;
			case TYPE_ARTIST:
				for (int i = 0; i < searchResult.getArtists().getItems().length; i++) {
					entry = new String[3];
					// generate a string with artist name and genre
					outputString = "";
					outputString = outputString + i + ". Artist name is "
							+ searchResult.getArtists().getItems()[i].getName() + " in the genre ";
					for (int j = 0; j < searchResult.getArtists().getItems()[i].getGenres().length - 1; j++) {
						outputString = outputString + searchResult.getArtists().getItems()[i].getGenres()[j] + " and ";
					}
					if (0 < searchResult.getArtists().getItems()[i].getGenres().length) {
						outputString = outputString + searchResult.getArtists().getItems()[i]
								.getGenres()[searchResult.getArtists().getItems()[i].getGenres().length - 1];
					}
					entry[1] = outputString;
					entry[0] = searchResult.getArtists().getItems()[i].getUri();
					entry[2] = "artist";
					resultList.add(entry);
				}
				return resultList;
			case TYPE_ALBUM:
				for (int i = 0; i < searchResult.getAlbums().getItems().length; i++) {
					entry = new String[3];
					// generate a string with album name and artists
					outputString = "";
					outputString = outputString + i + ". Album name is "
							+ searchResult.getAlbums().getItems()[i].getName() + " by ";
					for (int j = 0; j < searchResult.getAlbums().getItems()[i].getArtists().length - 1; j++) {
						outputString = outputString + searchResult.getAlbums().getItems()[i].getArtists()[j].getName()
								+ " and ";
					}
					if (0 < searchResult.getAlbums().getItems()[i].getArtists().length) {
						outputString = outputString + searchResult.getAlbums().getItems()[i]
								.getArtists()[searchResult.getAlbums().getItems()[i].getArtists().length - 1].getName();
					}
					entry[1] = outputString;
					entry[0] = searchResult.getAlbums().getItems()[i].getUri();
					entry[2] = "album";
					resultList.add(entry);
				}
				return resultList;
			default:
				break;

			}
		}
		return resultList;
	}

	/**
	 * create a search query and output a arraylist with search results
	 * 
	 * @param searchItem
	 * @param type
	 *            what is searched for (track, artist, album, playlist)
	 * @param limit
	 *            max length of the result list
	 * @return a array list with search result entries. every entry has a hashMap
	 *         with different attributes. e.g. artist, ... every entry has a uri to
	 *         the song an the type (track, artist, album, palylist)
	 */
	public List<HashMap<String, String>> searchList(String searchItem, String type, int limit) {

		SearchResult searchResult = searchInSpotify(searchItem, type, limit);

		return createHashMap(searchResult, type);
	}

	public List<HashMap<String, String>> createHashMap(SearchResult searchResult, String type) {
		ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
		if (searchResult != null) {
			switch (type.toLowerCase()) {
			case TYPE_TRACK:
				return createTrackSearchResult(searchResult, type);
			case TYPE_PLAYLIST:
				return createPlaylistSearchResult(searchResult, type);
			case TYPE_ARTIST:
				return createArtistSearchResult(searchResult, type);
			case TYPE_ALBUM:
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
	 * @return a array list with search result entries. every entry has a hashMap
	 *         with different attributes. e.g. artist, ... every entry has a uri to
	 *         the song an the type track
	 */
	private List<HashMap<String, String>> createTrackSearchResult(SearchResult searchResult, String type) {
		HashMap<String, String> entry;
		ArrayList<HashMap<String, String>> result = new ArrayList<>();
		if(type.equals(TYPE_TRACK)) {
		for (Track track : searchResult.getTracks().getItems()) {
			entry = new HashMap<>();
			if (track.getName() != null) {
				entry.put(Search.ITEM_NAME, track.getName());
			}
			String artist_name = "";
			for (int j = 0; j < track.getArtists().length - 1; j++) {
				artist_name = artist_name.concat(track.getArtists()[j].getName()).concat(", ");
			}
			if (0 < track.getArtists().length) {
				artist_name = artist_name.concat(track.getArtists()[track.getArtists().length - 1].getName());
			}
			entry.put(Search.ARTIST_NAME, artist_name);
			entry.put(Search.ITEM_URI, track.getUri());
			entry.put(Search.ITEM_TYPE, Search.TYPE_TRACK);
			result.add(entry);
		}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is a album
	 * 
	 * @param searchResult
	 * @return a array list with search result entries. every entry has a hashMap
	 *         with different attributes. e.g. artist, ... every entry has a uri to
	 *         the song an the type album
	 */
	private List<HashMap<String, String>> createAlbumSearchResult(SearchResult searchResult, String type) {
		HashMap<String, String> entry;
		ArrayList<HashMap<String, String>> result = new ArrayList<>();
		if (type.equals(TYPE_ALBUM)) {
			for (AlbumSimplified album : searchResult.getAlbums().getItems()) {
				entry = new HashMap<>();
				if (album.getName() != null) {
					entry.put(Search.ITEM_NAME, album.getName());
				}
				String artists = "";
				for (int j = 0; j < album.getArtists().length - 1; j++) {
					artists = artists + album.getArtists()[j].getName() + ", ";
				}
				if (0 < album.getArtists().length) {
					artists = artists + album.getArtists()[album.getArtists().length - 1].getName();
				}
				entry.put(Search.ARTIST_NAME, artists);
				entry.put(Search.ITEM_URI, album.getUri());
				entry.put(Search.ITEM_TYPE, Search.TYPE_ALBUM);
				result.add(entry);
			}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is a artist
	 * 
	 * @param searchResult
	 * @return a array list with search result entries. every entry has a hashMap
	 *         with different attributes. e.g. artist, ... every entry has a uri to
	 *         the song an the type artist
	 */
	private List<HashMap<String, String>> createArtistSearchResult(SearchResult searchResult, String type) {
		HashMap<String, String> entry;
		ArrayList<HashMap<String, String>> result = new ArrayList<>();
		if (type.equals(TYPE_ARTIST)) {
			for (Artist artist : searchResult.getArtists().getItems()) {
				entry = new HashMap<>();
				if (artist.getName() != null) {
					entry.put(Search.ITEM_NAME, artist.getName());
				}
				String genre = "";
				for (int j = 0; j < artist.getGenres().length - 1; j++) {
					genre = genre + artist.getGenres()[j] + ", ";
				}
				if (0 < artist.getGenres().length) {
					genre = genre + artist.getGenres()[artist.getGenres().length - 1];
				}
				entry.put(Search.GENRE, genre);
				entry.put(Search.ITEM_URI, artist.getUri());
				entry.put(Search.ITEM_TYPE, Search.TYPE_ARTIST);
				result.add(entry);
			}
		}
		return result;
	}

	/**
	 * create a List with all SearchResults when the type is a playlist
	 * 
	 * @param searchResult
	 * @return a array list with search result entries. every entry has a hashMap
	 *         with different attributes. e.g. artist, ... every entry has a uri to
	 *         the song an the type playlist
	 */
	private List<HashMap<String, String>> createPlaylistSearchResult(SearchResult searchResult, String type) {
		HashMap<String, String> entry;
		ArrayList<HashMap<String, String>> result = new ArrayList<>();
		if (type.equals(TYPE_PLAYLIST)) {
			for (PlaylistSimplified playlist : searchResult.getPlaylists().getItems()) {
				entry = new HashMap<>();
				if (playlist.getName() != null) {
					entry.put(Search.ITEM_NAME, playlist.getName());
				}

				if (playlist.getOwner().getDisplayName() != null) {
					entry.put(Search.ARTIST_NAME, playlist.getOwner().getDisplayName());
				}
				entry.put(Search.ITEM_URI, playlist.getUri());
				entry.put(Search.ITEM_TYPE, Search.TYPE_PLAYLIST);
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
	private SearchResult searchInSpotify(String searchItem, String type, int limit) {
		SearchResult searchResult;
		if (typeCheck(type)) {
			SearchItemRequest searchItemRequest = this.auth.getSpotifyApi().searchItem(searchItem, type.toLowerCase())
					.limit(Integer.valueOf(limit)).offset(Integer.valueOf(0)).build();
			try {
				searchResult = searchItemRequest.execute();
				return searchResult;

			} catch (SpotifyWebApiException | IOException e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * this method get a list from 10 featured playlist back
	 * 
	 * @return a array list of the result entrys. every entry has at 0 the uri of
	 *         the item and at 1 a output String with information about the item
	 * 
	 */
	public ArrayList<String[]> getFeaturedPlaylists() {
		ArrayList<String[]> resultList = new ArrayList<>();
		GetListOfFeaturedPlaylistsRequest getListOfFeaturedPlaylistsRequest = this.auth.getSpotifyApi()
				.getListOfFeaturedPlaylists().country(CountryCode.DE).limit(new Integer(10)).offset(new Integer(0))
				.build();

		try {
			final FeaturedPlaylists featuredPlaylists = getListOfFeaturedPlaylistsRequest.execute();
			String outputString;
			for (int i = 0; i < featuredPlaylists.getPlaylists().getItems().length; i++) {
				outputString = "";
				String[] entry = new String[2];
				outputString = outputString + i + ". Playlist name is "
						+ featuredPlaylists.getPlaylists().getItems()[i].getName() + " created by ";
				if (featuredPlaylists.getPlaylists().getItems()[i].getOwner().getDisplayName() != null) {
					outputString = outputString
							+ featuredPlaylists.getPlaylists().getItems()[i].getOwner().getDisplayName();
				}
				entry[0] = featuredPlaylists.getPlaylists().getItems()[i].getUri();
				entry[1] = outputString;
				resultList.add(entry);
			}
			return resultList;
		} catch (IOException | SpotifyWebApiException e) {
			System.out.println("Error: " + e.getMessage());
		}
		return resultList;
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
