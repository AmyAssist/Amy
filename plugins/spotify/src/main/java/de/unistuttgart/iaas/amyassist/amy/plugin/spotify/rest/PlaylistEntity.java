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

import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;

/**
 * Playlist entity for JSON
 * 
 * @author Christian Bräuner, Muhammed Kaya, Lars Buttgereit
 */
@XmlRootElement
public class PlaylistEntity extends Entity{

	/**
	 * constructor for a Playlist
	 */
	public PlaylistEntity() {

	}

	/**
	* constructor for a Playlist with set values
	* 
	* @param name
	*            of the playlist
	* @param songs
	* @param uri
	*            from a playlist to play it in spotify
	* @param imageUrl
	*            a url to image from the playlist. can be null
	*/
	public PlaylistEntity(String name, MusicEntity[] songs, String uri, String imageUrl) {
		this.name = name;
		this.songs = songs;
		this.uri = uri;
		this.imageUrl = imageUrl;
	}

	/**
	 * the name of the playlist
	 */
	private String name = "";

	/**
	 * the songs in the playlist
	 */
	private MusicEntity[] songs;

	/**
	 * uri of the playlist
	 */
	private String uri;

	/**
	 * a Url to a image from the playlist
	 */
	private String imageUrl;

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return songs
	 */
	public MusicEntity[] getSongs() {
		return this.songs;
	}

	/**
	 * @param songs
	 *            to set
	 */
	public void setSongs(MusicEntity[] songs) {
		this.songs = songs;
	}

	/**
	 * get the uri from the playlist
	 * 
	 * @return the uri
	 */
	public String getUri() {
		return this.uri;
	}

	/**
	 * set the uri form the playlist
	 * 
	 * @param uri
	 *            to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * get the image URL from the playlist image
	 * 
	 * @return url to the image
	 */
	public String getImageUrl() {
		return this.imageUrl;
	}

	/**
	 * set the image url from the playlist image
	 * 
	 * @param imageUrl
	 *            url to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "name of the playlist is: ".concat(getName());
	}
}
