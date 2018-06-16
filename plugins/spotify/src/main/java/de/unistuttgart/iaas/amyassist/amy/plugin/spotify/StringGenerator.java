package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringGenerator {

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

	private String trackOutputString(Map<String, String> track) {
		String result = "";
		result = result.concat("Track name is ").concat(track.get(SpotifyConstants.ITEM_NAME)).concat(" by ")
				.concat(track.get(SpotifyConstants.ARTIST_NAME));
		return result;
	}

	private String albumOutputString(Map<String, String> track) {
		String result = "";
		result = result.concat("Album name is ").concat(track.get(SpotifyConstants.ITEM_NAME)).concat(" by ")
				.concat(track.get(SpotifyConstants.ARTIST_NAME));
		return result;
	}

	private String artistOutputString(Map<String, String> track) {
		String result = "";
		result = result.concat("Artist name is ").concat(track.get(SpotifyConstants.ARTIST_NAME))
				.concat(" in the genre ").concat(track.get(SpotifyConstants.GENRE));
		return result;
	}

	private String playlistOutputString(Map<String, String> track) {
		String result = "";
		result = result.concat("Playlist name is ").concat(track.get(SpotifyConstants.ITEM_NAME)).concat(" created by ")
				.concat(track.get(SpotifyConstants.ARTIST_NAME));
		return result;
	}
}
