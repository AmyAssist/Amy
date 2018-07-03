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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.grammar.Grammar;

/**
 * Test Class for the Grammar Object
 * 
 * @author Kai Menzel
 */
class GrammarTest {

	private File file1;
	private File file2;

	private String mainGrammar;
	private String notMainGrammar;

	private HashMap<String, Grammar> gramList;

	/**
	 * Data setup for the tests
	 */
	@BeforeEach
	void setup() {
		this.file1 = new File("resources/one");
		this.file2 = new File("resources/two");

		this.mainGrammar = "mainGrammar";
		this.notMainGrammar = "notMainGrammar";

		this.gramList = new HashMap<>();
	}

	/**
	 * test the gramlist funktion
	 */
	void setGramList() {
		assertThat(this.gramList.isEmpty(), equalTo(true));
		this.gramList.put(this.mainGrammar, new Grammar(this.mainGrammar, this.file1));
		assertThat(this.gramList.isEmpty(), equalTo(false));
	}

	/**
	 * test the Constructor
	 */
	@Test
	void testSimpleConstrucor() {
		Grammar gram = new Grammar(this.mainGrammar, this.file1);
		assertThat(gram.getName(), equalTo(this.mainGrammar));
		assertThat(gram.getFile(), equalTo(this.file1));
		assertThat(gram.getSwitchList(), equalTo(this.gramList));
	}

	/**
	 * Test Getter and Setter
	 */
	@Test
	void testGetterAndSetter() {
		Grammar gram = new Grammar(this.mainGrammar, this.file1);
		assertThat(gram.getName(), equalTo(this.mainGrammar));
		assertThat(gram.getFile(), equalTo(this.file1));
		assertThat(gram.getSwitchList(), equalTo(this.gramList));

		setGramList();

		gram.setName(this.notMainGrammar);
		gram.setFile(this.file2);
		gram.setSwitchList(this.gramList);

		assertThat(gram.getName(), equalTo(this.notMainGrammar));
		assertThat(gram.getFile(), equalTo(this.file2));
		assertThat(gram.getSwitchList(), equalTo(this.gramList));
	}

	/**
	 * Test Constructors
	 */
	@Test
	void testComplexConstructor() {
		Grammar gram = new Grammar(this.mainGrammar, this.file1, this.gramList);
		assertThat(gram.getName(), equalTo(this.mainGrammar));
		assertThat(gram.getFile(), equalTo(this.file1));
		assertThat(gram.getSwitchList(), equalTo(this.gramList));

		gram = null;
		setGramList();

		gram = new Grammar(this.mainGrammar, this.file1, this.gramList);
		assertThat(gram.getName(), equalTo(this.mainGrammar));
		assertThat(gram.getFile(), equalTo(this.file1));
		assertThat(gram.getSwitchList(), equalTo(this.gramList));
	}

	/**
	 * test SwitchGrammars
	 */
	@Test
	void testPutSwitchGrammar() {
		Grammar gram = new Grammar(this.mainGrammar, this.file1, this.gramList);
		assertThat(gram.getName(), equalTo(this.mainGrammar));
		assertThat(gram.getFile(), equalTo(this.file1));
		assertThat(gram.getSwitchList(), equalTo(this.gramList));

		gram.putChangeGrammar(this.mainGrammar, new Grammar(this.mainGrammar, this.file1));
		this.gramList.put(this.mainGrammar, new Grammar(this.mainGrammar, this.file1));

		assertThat(gram.getName(), equalTo(this.mainGrammar));
		assertThat(gram.getFile(), equalTo(this.file1));
		assertThat(gram.getSwitchList(), equalTo(this.gramList));
	}

}
