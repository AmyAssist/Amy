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

package de.unistuttgart.iaas.amyassist.amy.core.speech.grammar;

import java.io.File;
import java.nio.file.Path;

/**
 * Enum that holds the mainGrammar and the tempGrammar
 * 
 * @author Kai Menzel
 */
public enum Grammar {
	/**
	 * No Grammar is Currently Active
	 */
	NONE,
	/**
	 * The Main Grammar Recognition is currently Active
	 */
	MAIN,
	/**
	 * The Temp Grammar Recognition is currently Active
	 */
	TEMP,
	/**
	 * The Google Speech Recognition is currently Active
	 */
	GOOGLE;

	private Path path;

	/**
	 * Setter
	 * 
	 * @param path
	 *            to Grammar
	 */
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * Getter
	 * 
	 * @return Path Object of the Path to Grammar File
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * Getter
	 * 
	 * @return File Object of the Path to the Grammar File
	 */
	public File getFile() {
		return this.path.toFile();
	}
}
