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

package de.unistuttgart.iaas.amyassist.amy.core.speech.data;

import java.net.URL;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Class that holds System Sounds
 * 
 * @author Kai Menzel, Tim Neumann
 */
@Service
public enum Sounds {
	/**
	 * Beep that gets Played to Signal the single call start
	 */
	SINGLE_CALL_START_BEEP("single_call_start_beep.wav"),
	/**
	 * Beep that gets Played to Signal the single call stop
	 */
	SINGLE_CALL_STOP_BEEP("single_call_stop_beep.wav");

	private URL url;

	/**
	 * Create Sound
	 * 
	 * @param fileName
	 *            The file name of the sound
	 */
	Sounds(String fileName) {
		this.url = this.getClass().getResource(fileName);
	}

	/**
	 * return Maven Resource Path as String
	 * 
	 * @return Path-String from MavenResource to file
	 */
	public String getFileAsString() {
		return this.url.getFile();
	}

	/**
	 * Get's resource url.
	 * 
	 * @return resource url.
	 */
	public URL getUrl() {
		return this.url;
	}
}
