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
 * Playlist entity for JSON
 * 
 * @author Christian Bräuner, Muhammed Kaya, Lars Buttgereit
 */
public class PlaylistEntity extends Item {

	private String playlistCreator;

	/**
	 * constructor for a Playlist
	 */
	public PlaylistEntity() {

	}

	/**
	 * Get's {@link #playlistCreator playlistCreator}
	 * 
	 * @return playlistCreator
	 */
	public String getPlaylistCreator() {
		return this.playlistCreator;
	}

	/**
	 * Set's {@link #playlistCreator playlistCreator}
	 * 
	 * @param playlistCreator
	 *            playlistCreator
	 */
	public void setPlaylistCreator(String playlistCreator) {
		this.playlistCreator = playlistCreator;
	}

	@Override
	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		if (getName() != null) {
			if (this.playlistCreator != null) {
				return stringbuilder.append(getName()).append(" created by: ").append(this.playlistCreator).toString();
			}
			return stringbuilder.append(getName()).toString();
		}
		return "No playlist data available";
	}
}
