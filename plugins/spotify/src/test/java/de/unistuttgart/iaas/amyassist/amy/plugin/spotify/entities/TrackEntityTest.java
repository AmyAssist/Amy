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
 * test class for the TrackEntity
 * @author Lars Buttgereit
 */
class TrackEntityTest {
	private static final String NAME = "Next to me";
	private static final String[] ARTIST = {"Dieter", "Hans"};
	private static final String URI = "testuri123";
	private static final int DURATION = 20;
	private TrackEntity track;
	@BeforeEach
	void init() {
		this.track = new TrackEntity(NAME, ARTIST, URI);
	}

	@Test
	void testConstructorAndGetters() {
		assertThat(this.track.getName(), equalTo(NAME));
		assertThat(this.track.getArtists(), equalTo(ARTIST));
		assertThat(this.track.getUri(), equalTo(URI));
	}

	@Test
	void testSetter() {
		this.track = new TrackEntity();
		this.track.setName(NAME);
		this.track.setArtists(ARTIST);
		this.track.setUri(URI);
		this.track.setDurationInMs(DURATION);
		assertThat(this.track.getName(), equalTo(NAME));
		assertThat(this.track.getArtists(), equalTo(ARTIST));
		assertThat(this.track.getUri(), equalTo(URI));
		assertThat(this.track.getDurationInMs(), equalTo(DURATION));
	}
	
	@Test
	void testToString() {
		assertThat(this.track.toString(), equalTo(NAME + " from: Dieter, Hans, "));
		this.track = new TrackEntity();
		assertThat(this.track.toString(), equalTo("No song data available"));
	}
}
