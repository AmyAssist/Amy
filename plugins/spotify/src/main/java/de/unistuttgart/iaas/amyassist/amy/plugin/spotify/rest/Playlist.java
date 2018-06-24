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

/**
 * Playlist entity for JSON
 * 
 * @author Christian Bräuner, Muhammed Kaya
 */
@XmlRootElement
public class Playlist {
	
	/**
	 * constructor for a Playlist 
	 */
	public Playlist() {
		
	}
	
	/**
	 * constructor for a Playlist with set values
	 */
	public Playlist(String title, MusicEntity[] songs) {
		this.name = title;
		this.songs = songs;
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
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name to set
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
	 * @param songs to set
	 */
	public void setSongs(MusicEntity[] songs) {
		this.songs = songs;
	}

}
