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
 * In this class the data from a album is stored. For example after a search query
 * 
 * @author Lars Buttgereit
 */
public class AlbumEntity extends Item {
	private String[] artists;
	private String imageUrl;

	/**
	 * default constructor. no data is set
	 */
	public AlbumEntity() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param artists
	 * @param uri
	 */
	public AlbumEntity(String name, String[] artists, String uri) {
		super(name, uri);
		this.artists = artists;
	}

	/**
	 * get the artists from the album
	 * 
	 * @return
	 */
	public String[] getArtists() {
		return artists;
	}

	/**
	 * set the artists from the album
	 * 
	 * @param artists
	 */
	public void setArtists(String[] artists) {
		this.artists = artists;
	}

	/**
	 * Get's {@link #imageUrl imageUrl}
	 * 
	 * @return imageUrl
	 */
	public String getImageUrl() {
		return this.imageUrl;
	}

	/**
	 * Set's {@link #imageUrl imageUrl}
	 * 
	 * @param imageUrl
	 *            imageUrl
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		if (this.artists != null && getName() != null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder = stringBuilder.append(getName()).append(" from: ");
			for (String name : this.artists) {
				stringBuilder = stringBuilder.append(name).append(", ");
			}
			return stringBuilder.toString();
		}
		return "No album data is available";
	}
}
