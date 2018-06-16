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
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

@ExtendWith({ MockitoExtension.class, FrameworkExtention.class })
public class StringGeneratorTest {
	StringGenerator stringGenerator;
	HashMap<String, String> trackMap1 = new HashMap<>();
	HashMap<String, String> albumMap1 = new HashMap<>();
	HashMap<String, String> artistMap1 = new HashMap<>();
	HashMap<String, String> playlistMap1 = new HashMap<>();
	HashMap<String, String> trackMap2 = new HashMap<>();
	HashMap<String, String> albumMap2 = new HashMap<>();
	HashMap<String, String> artistMap2 = new HashMap<>();
	HashMap<String, String> playlistMap2 = new HashMap<>();
	HashMap<String, String> wrongTypeMap = new HashMap<>();
	List<Map<String, String>> tracks = new ArrayList<>();
	List<Map<String, String>> albums = new ArrayList<>();
	List<Map<String, String>> playlists = new ArrayList<>();
	List<Map<String, String>> artists = new ArrayList<>();
	List<Map<String, String>> wrongTypeList = new ArrayList<>();

	@BeforeEach
	public void init() {

		this.stringGenerator = new StringGenerator();
		createLists();
	}

	public void createLists() {
		trackMap1.put(SpotifyConstants.ITEM_NAME, "Flames");
		trackMap1.put(SpotifyConstants.ARTIST_NAME, "David Guetta");
		trackMap1.put(SpotifyConstants.ITEM_TYPE, "track");
		trackMap2.put(SpotifyConstants.ITEM_NAME, "Say Something");
		trackMap2.put(SpotifyConstants.ARTIST_NAME, "Justin Timberlake");
		trackMap2.put(SpotifyConstants.ITEM_TYPE, "track");

		albumMap1.put(SpotifyConstants.ITEM_NAME, "Flames");
		albumMap1.put(SpotifyConstants.ARTIST_NAME, "David Guetta");
		albumMap1.put(SpotifyConstants.ITEM_TYPE, "album");
		albumMap2.put(SpotifyConstants.ITEM_NAME, "Say Something");
		albumMap2.put(SpotifyConstants.ARTIST_NAME, "Justin Timberlake");
		albumMap2.put(SpotifyConstants.ITEM_TYPE, "album");
		
		playlistMap1.put(SpotifyConstants.ITEM_NAME, "Flames");
		playlistMap1.put(SpotifyConstants.ARTIST_NAME, "David Guetta");
		playlistMap1.put(SpotifyConstants.ITEM_TYPE, "playlist");
		playlistMap2.put(SpotifyConstants.ITEM_NAME, "Say Something");
		playlistMap2.put(SpotifyConstants.ARTIST_NAME, "Justin Timberlake");
		playlistMap2.put(SpotifyConstants.ITEM_TYPE, "playlist");		
		
		artistMap1.put(SpotifyConstants.GENRE, "pop");
		artistMap1.put(SpotifyConstants.ARTIST_NAME, "David Guetta");
		artistMap1.put(SpotifyConstants.ITEM_TYPE, "artist");
		artistMap2.put(SpotifyConstants.GENRE, "rock");
		artistMap2.put(SpotifyConstants.ARTIST_NAME, "Justin Timberlake");
		artistMap2.put(SpotifyConstants.ITEM_TYPE, "artist");
		
		wrongTypeMap.put(SpotifyConstants.ITEM_TYPE, "P");

		tracks.add(trackMap1);
		tracks.add(trackMap2);
		artists.add(artistMap1);
		artists.add(artistMap2);
		albums.add(albumMap1);
		albums.add(albumMap2);
		playlists.add(playlistMap1);
		playlists.add(playlistMap2);
		wrongTypeList.add(wrongTypeMap);
	}

	@Test
	public void testSearchSpeechStringTrack() {
		assertEquals("0. Track name is Flames by David Guetta\n1. Track name is Say Something by Justin Timberlake\n",
				stringGenerator.generateSearchOutputString(tracks));
	}

	@Test
	public void testSearchSpeechStringArtist() {
		assertEquals("0. Artist name is David Guetta in the genre pop\n1. Artist name is Justin Timberlake in the genre rock\n",
				stringGenerator.generateSearchOutputString(artists));
	}

	@Test
	public void testSearchSpeechStringPlaylist() {
		assertEquals(
				"0. Playlist name is Flames created by David Guetta\n1. Playlist name is Say Something created by Justin Timberlake\n",
				stringGenerator.generateSearchOutputString(playlists));
	}

	@Test
	public void testSearchSpeechStringAlbum() {
		assertEquals("0. Album name is Flames by David Guetta\n1. Album name is Say Something by Justin Timberlake\n",
				stringGenerator.generateSearchOutputString(albums));
	}
	@Test
	public void testWrongType() {
		
		assertEquals("", stringGenerator.generateSearchOutputString(wrongTypeList));
	}
}
