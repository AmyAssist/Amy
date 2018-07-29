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

package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for stemming
 * 
 * @author Lars Buttgereit
 */
class TestStemming {

	private Stemming stemming;

	@BeforeEach
	void init() {
		this.stemming = new Stemming();
	}

	@Test
	void testSSES_SS() {
		assertThat(this.stemming.stem("caresses"), equalTo("caress"));
	}

	@Test
	void testIES_I() {
		assertThat(this.stemming.stem("ponies"), equalTo("poni"));
	}

	@Test
	void testss_SS() {
		assertThat(this.stemming.stem("caress"), equalTo("caress"));
	}

	@Test
	void testS_() {
		assertThat(this.stemming.stem("cats"), equalTo("cat"));
	}

	@Test
	void testEED_EE() {
		assertThat(this.stemming.stem("feed"), equalTo("feed"));
		assertThat(this.stemming.stem("agreed"), equalTo("agree"));
	}
	
	@Test
	void testED_() {
		assertThat(this.stemming.stem("plastered"), equalTo("plaster"));
		assertThat(this.stemming.stem("bled"), equalTo("bled"));
	}
	
	@Test
	void testING_() {
		assertThat(this.stemming.stem("motoring"), equalTo("motor"));
		assertThat(this.stemming.stem("sing"), equalTo("sing"));
	}
	
	@Test
	void testAT_ATE() {
		assertThat(this.stemming.stem("conflated"), equalTo("conflate"));
	}
	
	@Test
	void testBL_BLE() {
		assertThat(this.stemming.stem("troubled"), equalTo("trouble"));
	}
	
	@Test
	void testIZ_IZE() {
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
	}
	
	@Test
	void testcvc_E() {
		assertThat(this.stemming.stem("failing"), equalTo("fail"));
		assertThat(this.stemming.stem("filing"), equalTo("file"));
	}
	
	@Test
	void testY_I() {
		assertThat(this.stemming.stem("happy"), equalTo("happi"));
		assertThat(this.stemming.stem("sky"), equalTo("sky"));
	}

	@Test
	void testWithManyWords() {
		try (BufferedReader inputReader = new BufferedReader(new FileReader(
				"src/test/resources/de/unistuttgart/iaas/amyassist/amy/core/natlang/stemmingTest/input.txt"));
				BufferedReader outputReader = new BufferedReader(new FileReader(
						"src/test/resources/de/unistuttgart/iaas/amyassist/amy/core/natlang/stemmingTest/output.txt"))) {
			String inputLine;
			String outputLine;
			do {
				inputLine = inputReader.readLine();
				outputLine = outputReader.readLine();
				if (inputLine != null || outputLine != null) {
					assertThat(this.stemming.stem(inputLine), equalTo(outputLine));
				}
			} while (inputLine != null || outputLine != null);
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

}
