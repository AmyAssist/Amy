/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.FeaturedPlaylists;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.requests.data.browse.GetListOfFeaturedPlaylistsRequest;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * TODO: Description
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
	 * @return a array list of the result entrys. every entry has at 0 the uri
	 *         of the item and at 1 a output String with information about the
	 *         item and at 2 the type of the item. if no element found a empty
	 *         list is returned
	 */
	public ArrayList<String[]> SearchAnything(String searchItem, String type, int limit) {
		SearchResult searchResult = searchInSpotify(searchItem, type, limit);
		return createSpeechList(searchResult, type);
	}
	
	public ArrayList<String[]> createSpeechList(SearchResult searchResult, String type){
		ArrayList<String[]> resultList = new ArrayList<>();
		if (searchResult != null) {
			String[] entry;
			String outputString;
			switch (type.toLowerCase()) {
			case "track":
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
			case "playlist":
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
			case "artist":
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
			case "album":
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
	 * @return a array list with search result entries. every entry has a
	 *         hashMap with different attributes. e.g. artist, ... every entry
	 *         has a uri to the song an the type (track, artist, album,
	 *         palylist)
	 */
	public ArrayList<HashMap<String, String>> searchList(String searchItem, String type, int limit) {

		SearchResult searchResult = searchInSpotify(searchItem, type, limit);
		
		return createHashMap(searchResult, type);
	}
	
	public ArrayList<HashMap<String, String>> createHashMap(SearchResult searchResult, String type){
		ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
		if (searchResult != null) {
			HashMap<String, String> entry;
			switch (type.toLowerCase()) {
			case "track":
				for (int i = 0; i < searchResult.getTracks().getItems().length; i++) {
					entry = new HashMap<>();
					if (searchResult.getTracks().getItems()[i].getName() != null) {
						entry.put(Search.ITEM_NAME, searchResult.getTracks().getItems()[i].getName());
					}
					String artist_name = "";
					for (int j = 0; j < searchResult.getTracks().getItems()[i].getArtists().length - 1; j++) {
						artist_name = artist_name + searchResult.getTracks().getItems()[i].getArtists()[j].getName()
								+ ", ";
					}
					if (0 < searchResult.getTracks().getItems()[i].getArtists().length) {
						artist_name = artist_name + searchResult.getTracks().getItems()[i]
								.getArtists()[searchResult.getTracks().getItems()[i].getArtists().length - 1].getName();
					}
					entry.put(Search.ARTIST_NAME, artist_name);
					entry.put(Search.ITEM_URI, searchResult.getTracks().getItems()[i].getUri());
					entry.put(Search.ITEM_TYPE, Search.TYPE_TRACK);
					resultList.add(entry);
				}
				return resultList;
			case "playlist":
				for (int i = 0; i < searchResult.getPlaylists().getItems().length; i++) {
					entry = new HashMap<>();
					if (searchResult.getPlaylists().getItems()[i].getName() != null) {
						entry.put(Search.ITEM_NAME, searchResult.getPlaylists().getItems()[i].getName());
					}
					
					if (searchResult.getPlaylists().getItems()[i].getOwner().getDisplayName() != null) {
						entry.put(Search.ARTIST_NAME,
								searchResult.getPlaylists().getItems()[i].getOwner().getDisplayName());
					}
					entry.put(Search.ITEM_URI, searchResult.getPlaylists().getItems()[i].getUri());
					entry.put(Search.ITEM_TYPE, Search.TYPE_PLAYLIST);
					resultList.add(entry);
				}
				return resultList;
			case "artist":
				for (int i = 0; i < searchResult.getArtists().getItems().length; i++) {
					entry = new HashMap<>();
					if(searchResult.getArtists().getItems()[i].getName() != null) {
					entry.put(Search.ITEM_NAME, searchResult.getArtists().getItems()[i].getName());
					}
					String genre = "";		
					for (int j = 0; j < searchResult.getArtists().getItems()[i].getGenres().length - 1; j++) {
						genre = genre + searchResult.getArtists().getItems()[i].getGenres()[j] + ", ";
					}
					if (0 < searchResult.getArtists().getItems()[i].getGenres().length) {
						genre = genre + searchResult.getArtists().getItems()[i]
								.getGenres()[searchResult.getArtists().getItems()[i].getGenres().length - 1];
					}
					entry.put(Search.GENRE, genre);
					entry.put(Search.ITEM_URI, searchResult.getArtists().getItems()[i].getUri());
					entry.put(Search.ITEM_TYPE, Search.TYPE_ARTIST);
					resultList.add(entry);
				}
				return resultList;
			case "album":
				for (int i = 0; i < searchResult.getAlbums().getItems().length; i++) {
					entry = new HashMap<>();
					if(searchResult.getAlbums().getItems()[i].getName() != null) {
					entry.put(Search.ITEM_NAME, searchResult.getAlbums().getItems()[i].getName());
					}
					String artists = "";
					for (int j = 0; j < searchResult.getAlbums().getItems()[i].getArtists().length - 1; j++) {
						artists = artists + searchResult.getAlbums().getItems()[i].getArtists()[j].getName()
								+ ", ";
					}
					if (0 < searchResult.getAlbums().getItems()[i].getArtists().length) {
						artists = artists + searchResult.getAlbums().getItems()[i]
								.getArtists()[searchResult.getAlbums().getItems()[i].getArtists().length - 1].getName();
					}
					entry.put(Search.ARTIST_NAME, artists);
					entry.put(Search.ITEM_URI, searchResult.getAlbums().getItems()[i].getUri());
					entry.put(Search.ITEM_TYPE, Search.TYPE_ALBUM);
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
					.limit(new Integer(limit)).offset(new Integer(0)).build();
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
	 * @return a array list of the result entrys. every entry has at 0 the uri
	 *         of the item and at 1 a output String with information about the
	 *         item
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
		return null;
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
