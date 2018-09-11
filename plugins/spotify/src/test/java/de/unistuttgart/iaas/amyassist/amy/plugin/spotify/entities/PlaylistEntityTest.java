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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.PlaylistEntity;

/**
 * Test class for the PlaylistEntity
 * 
 * @author Lars Buttgereit
 */
class PlaylistEntityTest {

	private PlaylistEntity playlist = new PlaylistEntity();

	@Test
	void testSetGetName() {
		this.playlist.setName("name");
		assertThat(this.playlist.getName(), equalTo("name"));
	}

	@Test
	void testSetGetUri() {
		this.playlist.setUri("123abc");
		assertThat(this.playlist.getUri(), equalTo("123abc"));
	}

	@Test
	void testSetGetImageUrl() {
		this.playlist.setImageUrl("testimage.de");
		assertThat(this.playlist.getImageUrl(), equalTo("testimage.de"));
	}

	@Test
	void testToStringEmpty() {
		this.playlist = new PlaylistEntity();
		assertThat(this.playlist.toString(), equalTo("No playlist data available"));
	}

	@Test
	void testToStringWithCreator() {
		this.playlist.setPlaylistCreator("Hans");
		this.playlist.setName("Hello");
		assertThat(this.playlist.toString(), equalTo("Hello created by: Hans"));
	}
}
