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
import java.util.HashMap;
import java.util.Map;

/**
 * Grammar Object, that contains all important Information of a Grammar
 * 
 * @author Kai Menzel
 */
public class Grammar {

	private String name;
	private Path path;
	private Map<String, Grammar> switchList = new HashMap<>();

	/**
	 * @param name
	 *            Name of the Grammar
	 * @param path
	 *            Path to the Grammar
	 * 
	 */
	public Grammar(String name, Path path) {
		this.name = name;
		this.path = path;
	}

	/**
	 * @param name
	 *            Name of the Grammar
	 * @param path
	 *            Path to the Grammar
	 * @param switchList
	 *            List of all possible Grammars with their call String
	 */
	public Grammar(String name, Path path, Map<String, Grammar> switchList) {
		this.name = name;
		this.path = path;
		this.switchList = switchList;
	}

	/**
	 * Add a new Grammar that can be switched to
	 * 
	 * @param switchCommand
	 *            Command to say to switch to new Grammar
	 * @param grammar
	 *            Grammar to change to
	 */
	public void putChangeGrammar(String switchCommand, Grammar grammar) {
		this.switchList.put(switchCommand, grammar);
	}

	/**
	 * Getter
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter
	 * 
	 * @return file
	 */
	public File getFile() {
		return this.path.toFile();
	}

	/**
	 * Getter
	 * 
	 * @return switchList
	 */
	public Map<String, Grammar> getSwitchList() {
		return this.switchList;
	}

}
