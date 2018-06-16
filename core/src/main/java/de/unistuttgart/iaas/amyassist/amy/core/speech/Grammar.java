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

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Grammar Object, that contains all important Information of a Grammar
 * @author Kai Menzel
 */
public class Grammar {
	
	private String name;
	private File file;
	private HashMap<String, Grammar> switchList = new HashMap<>();

	/**
	 * @param name Name of the Grammar
	 * @param file Path to the Grammar
	 * 
	 */
	public Grammar(String name, File file) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.file = file;
	}
	
	/**
	 * @param name Name of the Grammar
	 * @param file Path to the Grammar
	 * @param switchList List of all possible Grammar changes
	 */
	public Grammar(String name, File file, HashMap<String, Grammar> switchList) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.file = file;
		this.switchList = switchList;
	}
	
	/**
	 * Add a new Grammar that can be switched to
	 * @param switchCommand Command to say to switch to new Grammar
	 * @param gram Grammar to change to by said Grammarswitch
	 */
	public void putChangeGrammar(String switchCommand, Grammar grammar){
		this.switchList.put(switchCommand, grammar);
	}

	public String getName() {
		return this.name;
	}

	public File getFile() {
		return this.file;
	}

	public Map<String, Grammar> getSwitchList() {
		return this.switchList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setSwitchList(HashMap<String, Grammar> switchList) {
		this.switchList = switchList;
	}

	

}
