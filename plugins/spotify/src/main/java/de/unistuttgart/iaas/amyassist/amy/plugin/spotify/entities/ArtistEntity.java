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
 * In this class the data from a artist is stored. For example after a search query
 * 
 * @author Lars Buttgereit
 */
public class ArtistEntity extends Item {
	private String[] genres;
	/**
	 * default constructor. no data is set
	 */
	public ArtistEntity() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name of the artist
	 * @param genres of the artist
	 * @param uri of the artist
	 */
	public ArtistEntity(String name, String[] genres, String uri) {
		super(name, uri);
		this.genres = genres;
	}

	/**
	 * get the genre from the artist
	 * 
	 * @return generes
	 */
	public String[] getGenre() {
		return this.genres;
	}

	/**
	 * set the genre from the artist
	 * 
	 * @param genres of a artist
	 */
	public void setGenre(String[] genres) {
		this.genres = genres;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		if (getName() != null) {
			stringBuilder.append(getName());
			if (this.genres != null) {
				stringBuilder = stringBuilder.append(" in the genre: ");
				for (String genre : this.genres) {
					stringBuilder.append(genre).append(", ");
				}
			}
			return stringBuilder.toString();
		}
		return "No artist data is available";
	}

}
