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

/**
 * In this class the data from a track is stored. For example after a search query
 * 
 * @author Lars Buttgereit
 */
public class TrackEntity extends Item {
	private String[] artists;
	private int durationInMs;

	/**
	 * default constructor. no data is set
	 */
	public TrackEntity() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param artists
	 * @param uri
	 */
	public TrackEntity(String name, String[] artists, String uri) {
		super(name, uri);
		this.artists = artists;
	}

	/**
	 * get the artists from the track
	 * 
	 * @return
	 */
	public String[] getArtist() {
		return artists;
	}

	/**
	 * set the artists from the track
	 * 
	 * @param playlistOwner
	 */
	public void setArtists(String[] artist) {
		this.artists = artist;
	}

	/**
	 * Get's {@link #durationInMs duration}
	 * 
	 * @return duration
	 */
	public int getDurationInMs() {
		return this.durationInMs;
	}

	/**
	 * Set's {@link #durationInMs duration} 
	 * 
	 * @param duration
	 *            duration
	 */
	public void setDurationInMs(int duration) {
		this.durationInMs = duration;
	}

	@Override
	public String toString() {
		if(this.artists != null && getName() != null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder = stringBuilder.append(getName()).append(" from: ");
			for(String name : this.artists) {
				stringBuilder = stringBuilder.append(name).append(", ");
			}
			return stringBuilder.toString();
		}
		return "No song data available";
	}
}
