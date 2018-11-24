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

package io.github.amyassist.amy.core.natlang.languageSpecific.en;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.amyassist.amy.natlang.languagespecifics.Stemmer;
import io.github.amyassist.amy.natlang.languagespecifics.en.EnglishStemmer;

/**
 * Test class for the stemming class
 * 
 * @author Lars Buttgereit
 */
class TestEnglishStemmer {

	private Stemmer stemming;

	@BeforeEach
	void init() {
		this.stemming = new EnglishStemmer();
	}

	@Test
	void testSSES() {
		assertThat(this.stemming.stem("caresses"), equalTo("caress"));
	}

	@Test
	void testIES() {
		assertThat(this.stemming.stem("ponies"), equalTo("poni"));
	}

	@Test
	void testSS() {
		assertThat(this.stemming.stem("caress"), equalTo("caress"));
	}

	@Test
	void testS() {
		assertThat(this.stemming.stem("cats"), equalTo("cat"));
	}

	@Test
	void testEED() {
		assertThat(this.stemming.stem("feed"), equalTo("feed"));
		assertThat(this.stemming.stem("agreed"), equalTo("agre"));
		assertThat(this.stemming.stem("agree"), equalTo("agre"));
	}

	@Test
	void testED() {
		assertThat(this.stemming.stem("plastered"), equalTo("plaster"));
		assertThat(this.stemming.stem("bled"), equalTo("bled"));
	}

	@Test
	void testING() {
		assertThat(this.stemming.stem("motoring"), equalTo("motor"));
		assertThat(this.stemming.stem("sing"), equalTo("sing"));
	}

	@Test
	void testAT() {
		assertThat(this.stemming.stem("conflated"), equalTo("conflat"));
	}

	@Test
	void testBL() {
		assertThat(this.stemming.stem("troubled"), equalTo("troubl"));
	}

	@Test
	void testIZ() {
		assertThat(this.stemming.stem("sized"), equalTo("size"));
	}

	@Test
	void testDoubleConsonant() {
		assertThat(this.stemming.stem("hopping"), equalTo("hop"));
		assertThat(this.stemming.stem("tanned"), equalTo("tan"));
		assertThat(this.stemming.stem("falling"), equalTo("fall"));
		assertThat(this.stemming.stem("hissing"), equalTo("hiss"));
		assertThat(this.stemming.stem("fizzed"), equalTo("fizz"));
		assertThat(this.stemming.stem("hissing"), equalTo("hiss"));
		assertThat(this.stemming.stem("controlled"), equalTo("control"));
		assertThat(this.stemming.stem("rolled"), equalTo("roll"));
	}

	@Test
	void testcvc_E() {
		assertThat(this.stemming.stem("failing"), equalTo("fail"));
		assertThat(this.stemming.stem("filing"), equalTo("file"));
	}

	@Test
	void testWithManyWords() throws IOException {
		BufferedReader inputReader = new BufferedReader(new FileReader(
				"src/test/resources/de/unistuttgart/iaas/amyassist/amy/core/natlang/stemmingTest/input.txt"));
		BufferedReader outputReader = new BufferedReader(new FileReader(
				"src/test/resources/de/unistuttgart/iaas/amyassist/amy/core/natlang/stemmingTest/output.txt"));
		String inputLine = "";
		String outputLine = "";
		while (inputLine != null || outputLine != null) {
			inputLine = inputReader.readLine();
			outputLine = outputReader.readLine();
			if (inputLine != null && outputLine != null) {
				assertThat(this.stemming.stem(inputLine), equalTo(outputLine));
			}
		}
		inputReader.close();
		outputReader.close();
	}

}
