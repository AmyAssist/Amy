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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class StringGeneratorTest {
	@Reference
	private TestFramework testFramework;
	private StringGenerator stringGenerator;
	private HashMap<String, String> trackMap1 = new HashMap<>();
	private HashMap<String, String> albumMap1 = new HashMap<>();
	private HashMap<String, String> artistMap1 = new HashMap<>();
	private HashMap<String, String> playlistMap1 = new HashMap<>();
	private HashMap<String, String> trackMap2 = new HashMap<>();
	private HashMap<String, String> albumMap2 = new HashMap<>();
	private HashMap<String, String> artistMap2 = new HashMap<>();
	private HashMap<String, String> playlistMap2 = new HashMap<>();
	private HashMap<String, String> wrongTypeMap = new HashMap<>();
	private List<Map<String, String>> tracks = new ArrayList<>();
	private List<Map<String, String>> albums = new ArrayList<>();
	private List<Map<String, String>> playlists = new ArrayList<>();
	private List<Map<String, String>> artists = new ArrayList<>();
	private List<Map<String, String>> wrongTypeList = new ArrayList<>();

	@BeforeEach
	public void init() {

		this.stringGenerator = this.testFramework.setServiceUnderTest(StringGenerator.class);
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
		assertThat(this.stringGenerator.generateSearchOutputString(this.tracks), equalTo(
				"0. Track name is Flames by David Guetta\n1. Track name is Say Something by Justin Timberlake\n"));
	}

	@Test
	public void testSearchSpeechStringArtist() {
		assertThat(this.stringGenerator.generateSearchOutputString(this.artists), equalTo(
				"0. Artist name is David Guetta in the genre pop\n1. Artist name is Justin Timberlake in the genre rock\n"));
	}

	@Test
	public void testSearchSpeechStringPlaylist() {
		assertThat(this.stringGenerator.generateSearchOutputString(this.playlists), equalTo(
				"0. Playlist name is Flames created by David Guetta\n1. Playlist name is Say Something created by Justin Timberlake\n"));
	}

	@Test
	public void testSearchSpeechStringAlbum() {
		assertThat(this.stringGenerator.generateSearchOutputString(this.albums), equalTo(
				"0. Album name is Flames by David Guetta\n1. Album name is Say Something by Justin Timberlake\n"));
	}

	@Test
	public void testWrongType() {

		assertThat(this.stringGenerator.generateSearchOutputString(this.wrongTypeList), equalTo(""));
	}
}
