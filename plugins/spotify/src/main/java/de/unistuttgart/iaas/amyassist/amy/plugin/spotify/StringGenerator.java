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

import java.util.List;
import java.util.Map;

/**
 * This class generate from the search result or other maps or lists with
 * tracks, playlists or albums one output string
 * 
 * @author Lars Buttgereit
 *
 */
public class StringGenerator {

	/**
	 * generate one String out of the search result list. Useful for example for
	 * console or speech output
	 * 
	 * @param input
	 *            the list from a search or a list with maps with attributes from
	 *            track, album, playlist
	 * @return a single String with useful information for the user from all
	 *         elements from the list
	 */
	public String generateSearchOutputString(List<Map<String, String>> input) {
		String result = "";
		for (int i = 0; i < input.size(); i++) {
			Map<String, String> entry = input.get(i);
			String itemString = generateSearchOutputString(entry);
			if (!itemString.equals("")) {
				result = result.concat(String.valueOf(i)).concat(". ").concat(itemString).concat("\n");
			}
		}
		return result;
	}

	/**
	 * generate one String out of the search result map or other maps with track,
	 * album or playlist attributes. Useful for example for console or speech output
	 * 
	 * @param input
	 *            the map from a search or a map with attributes from track, album,
	 *            playlist
	 * @return a single String with useful information for the user
	 */
	public String generateSearchOutputString(Map<String, String> input) {
		switch (input.get(SpotifyConstants.ITEM_TYPE)) {
		case SpotifyConstants.TYPE_ALBUM:
			return albumOutputString(input);
		case SpotifyConstants.TYPE_ARTIST:
			return artistOutputString(input);
		case SpotifyConstants.TYPE_PLAYLIST:
			return playlistOutputString(input);
		case SpotifyConstants.TYPE_TRACK:
			return trackOutputString(input);
		default:
			return "";
		}
	}

	/**
	 * build a single string out of a map with the type "track"
	 * 
	 * @param track
	 * @return a single String with useful information for the user about the track
	 */
	private String trackOutputString(Map<String, String> track) {
		String result = "";
		result = result.concat("Track name is ").concat(track.get(SpotifyConstants.ITEM_NAME)).concat(" by ")
				.concat(track.get(SpotifyConstants.ARTIST_NAME));
		return result;
	}

	/**
	 * build a single string out of a map with the type "album"
	 * 
	 * @param album
	 * @return a single String with useful information for the user about the album
	 */
	private String albumOutputString(Map<String, String> album) {
		String result = "";
		result = result.concat("Album name is ").concat(album.get(SpotifyConstants.ITEM_NAME)).concat(" by ")
				.concat(album.get(SpotifyConstants.ARTIST_NAME));
		return result;
	}

	/**
	 * build a single string out of a map with the type "artist"
	 * 
	 * @param artist
	 * @return a single String with useful information for the user
	 */
	private String artistOutputString(Map<String, String> artist) {
		String result = "";
		result = result.concat("Artist name is ").concat(artist.get(SpotifyConstants.ARTIST_NAME))
				.concat(" in the genre ").concat(artist.get(SpotifyConstants.GENRE));
		return result;
	}

	/**
	 * build a single string out of a map with the type "playlist"
	 * 
	 * @param playlist
	 * @return a single String with useful information for the user about the
	 *         playlist
	 */
	private String playlistOutputString(Map<String, String> playlist) {
		String result = "";
		result = result.concat("Playlist name is ").concat(playlist.get(SpotifyConstants.ITEM_NAME))
				.concat(" created by ").concat(playlist.get(SpotifyConstants.ARTIST_NAME));
		return result;
	}
}
