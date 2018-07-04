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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;

/**
 * 
 * @author Lars Buttgereut
 */
@ExtendWith(FrameworkExtension.class )
class PlaylistEntityTest {

	private PlaylistEntity playlist;
	private MusicEntity[] musicEntity;
	
	@BeforeEach
	public void init() {
		this.musicEntity = new MusicEntity[0];
		this.playlist = new PlaylistEntity("Hello", musicEntity, "abc123", "image.de");
	}
	
	@Test
	public void testGetName() {
		assertThat(this.playlist.getName(), equalTo("Hello"));
	}
	@Test
	public void testGetSongs() {
		assertThat(this.playlist.getSongs(), equalTo(musicEntity));
	}
	@Test
	public void testGetUri() {
		assertThat(this.playlist.getUri(), equalTo("abc123"));
	}
	@Test
	public void testGetImageUrl() {
		assertThat(this.playlist.getImageUrl(), equalTo("image.de"));
	}
	
	@Test
	public void testSetName() {
		this.playlist.setName("name");
		assertThat(this.playlist.getName(), equalTo("name"));
	}
	@Test
	public void testSetSongs() {
		MusicEntity[] musicE = new MusicEntity[1];
		this.playlist.setSongs(musicE);
		assertThat(this.playlist.getSongs(), equalTo(musicE));
	}
	@Test
	public void testSetUri() {
		this.playlist.setUri("123abc");
		assertThat(this.playlist.getUri(), equalTo("123abc"));
	}
	@Test
	public void testSetImageUrl() {
		this.playlist.setImageUrl("testimage.de");
		assertThat(this.playlist.getImageUrl(), equalTo("testimage.de"));
	}
	
	@Test
	public void testToString() {
		assertThat(this.playlist.toString(), equalTo("name of the playlist is: Hello"));
	}

}
