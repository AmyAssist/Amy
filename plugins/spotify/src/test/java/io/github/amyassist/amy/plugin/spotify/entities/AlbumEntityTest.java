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

package io.github.amyassist.amy.plugin.spotify.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 
 * Test class for the AlbumEntity
 * 
 * @author Lars Buttgereit
 */
class AlbumEntityTest {
	private static final String NAME = "Next to me";
	private static final String[] ARTIST = { "Dieter", "Hans" };
	private static final String URI = "testuri123";
	private static final String IMAGE_URL = "image.de";
	private AlbumEntity album;

	@BeforeEach
	void init() {
		this.album = new AlbumEntity();
		this.album.setName(NAME);
		this.album.setArtists(ARTIST);
		this.album.setUri(URI);
		this.album.setImageUrl(IMAGE_URL);
	}

	@Test
	void testGetters() {
		assertThat(this.album.getName(), equalTo(NAME));
		assertThat(this.album.getArtists(), equalTo(ARTIST));
		assertThat(this.album.getUri(), equalTo(URI));
	}


	@Test
	void testToString() {
		assertThat(this.album.toString(), equalTo(NAME + " from: Dieter, Hans, "));
		this.album = new AlbumEntity();
		assertThat(this.album.toString(), equalTo("No album data is available"));
	}
}
