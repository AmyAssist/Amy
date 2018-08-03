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
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;

/**
 * Test class for MusicEntity
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
class MusicEntityTest {

	private MusicEntity musicEntity;
	
	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity#MusicEntity(String, String)}.
	 */
	@Test @BeforeEach
	void init() {
		this.musicEntity = new MusicEntity("Flames", "David Guetta");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity#toString()}.
	 */
	@Test
	void testToString() {
		assertThat(this.musicEntity.toString(), equalTo("Flames David Guetta"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity#getArtist()}.
	 */
	@Test
	void testGetArtist() {
		assertThat(this.musicEntity.getArtist(), equalTo("David Guetta"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity#setArtist(java.lang.String)}.
	 */
	@Test
	void testSetArtist() {
		this.musicEntity.setArtist("Justin Timberlake");
		assertThat(this.musicEntity.getArtist(), equalTo("Justin Timberlake"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity#getTitle()}.
	 */
	@Test
	void testGetTitle() {
		assertThat(this.musicEntity.getTitle(), equalTo("Flames"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities.MusicEntity#setTitle(java.lang.String)}.
	 */
	@Test
	void testSetTitle() {
		this.musicEntity.setTitle("Say Something");
		assertThat(this.musicEntity.getTitle(), equalTo("Say Something"));
	}

}
