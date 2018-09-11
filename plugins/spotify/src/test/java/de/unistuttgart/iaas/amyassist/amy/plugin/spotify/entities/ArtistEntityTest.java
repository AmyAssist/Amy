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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * 
 * Test class for the ArtistEntity
 * @author Lars Buttgereit
 */
public class ArtistEntityTest {
	private static final String NAME = "Next to me";
	private static final String[] GENRE = {"Rock", "Pop"};
	private static final String URI = "testuri123";
	private static final String IMAGE_URL = "image.de";
	private ArtistEntity artist;

	@BeforeEach
	void init() {
		this.artist = new ArtistEntity();
		this.artist.setName(NAME);
		this.artist.setGenre(GENRE);
		this.artist.setUri(URI);
		this.artist.setImageUrl(IMAGE_URL);
	}
	
	@Test
	public void testSetterGetter() {
		assertThat(this.artist.getName(), equalTo(NAME));
		assertThat(this.artist.getGenre(), equalTo(GENRE));
		assertThat(this.artist.getUri(), equalTo(URI));
		assertThat(this.artist.getImageUrl(), equalTo(IMAGE_URL));
	}
	
	@Test
	void testToString() {
		assertThat(this.artist.toString(), equalTo(NAME + " in the genre: Rock, Pop, "));
		this.artist = new ArtistEntity();
		assertThat(this.artist.toString(), equalTo("No artist data is available"));
	}
}
