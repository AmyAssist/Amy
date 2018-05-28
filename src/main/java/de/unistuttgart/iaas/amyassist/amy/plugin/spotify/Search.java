/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TODO: Description
 * 
 * @author Lars Buttgereit
 */
public class Search {

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
	 *         the item and at 1 a output String with information about the item
	 */
	public static ArrayList<String[]> SearchAnything(String searchItem, String type, int limit, SpotifyApi spotifyApi) {
		SearchResult searchResult;
		if (typeCheck(type)) {
			SearchItemRequest searchItemRequest = spotifyApi.searchItem(searchItem, type.toLowerCase())
					.limit(new Integer(limit)).offset(new Integer(0)).build();
			try {
				searchResult = searchItemRequest.execute();
				return buildResultList(searchResult, type);

			} catch (SpotifyWebApiException | IOException e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
		return null;
	}

	private static ArrayList<String[]> buildResultList(SearchResult searchResult, String type) {
		ArrayList<String[]> resultList = new ArrayList<>();
		String[] entry;
		String outputString;
		switch (type.toLowerCase()) {
		case "track":
			for (int i = 0; i < searchResult.getTracks().getItems().length; i++) {
				entry = new String[3];
				// generate a string with track name and artists
				outputString = "";
				outputString = outputString + i + ". Track name is " + searchResult.getTracks().getItems()[i].getName()
						+ " by ";
				for (int j = 0; j < searchResult.getTracks().getItems()[i].getArtists().length - 1; i++) {
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
				if(searchResult.getPlaylists().getItems()[i].getOwner().getDisplayName() != null) {
				outputString = outputString + searchResult.getPlaylists().getItems()[i].getOwner().getDisplayName();
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
				for (int j = 0; j < searchResult.getArtists().getItems()[i].getGenres().length - 1; i++) {
					outputString = outputString + searchResult.getArtists().getItems()[i].getGenres()[j]
							+ " and ";
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
				outputString = outputString + i + ". Album name is " + searchResult.getAlbums().getItems()[i].getName()
						+ " by ";
				for (int j = 0; j < searchResult.getAlbums().getItems()[i].getArtists().length - 1; i++) {
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
