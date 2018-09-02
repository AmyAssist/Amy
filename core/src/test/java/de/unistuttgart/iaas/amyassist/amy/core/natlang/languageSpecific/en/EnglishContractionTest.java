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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.languageSpecific.en;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.languagespecifics.en.EnglishContraction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test class for English Contraction
 * 
 * @author Lars Buttgereit
 */
public class EnglishContractionTest {
	private EnglishContraction contraction = new EnglishContraction();

	void testIAm() {
		assertThat(this.contraction.disassemblingContraction("Hello i'm bla"), equalTo("Hello i am bla"));
	}

	void testNot() {
		assertThat(this.contraction.disassemblingContraction("Hello ain't bla"), equalTo("Hello am not bla"));
		assertThat(this.contraction.disassemblingContraction("Hello won't bla"), equalTo("Hello will not bla"));
		assertThat(this.contraction.disassemblingContraction("Hello shan't bla"), equalTo("Hello shall not bla"));
		assertThat(this.contraction.disassemblingContraction("Hello don't bla"), equalTo("Hello do not bla"));
	}

	void testLetUs() {
		assertThat(this.contraction.disassemblingContraction("Hello let's bla"), equalTo("Hello let us bla"));
	}

	void testAre() {
		assertThat(this.contraction.disassemblingContraction("you're bla"), equalTo("you are bla"));
	}

	void testHave() {
		assertThat(this.contraction.disassemblingContraction("you've bla"), equalTo("you have bla"));
	}

	void testWill() {
		assertThat(this.contraction.disassemblingContraction("you'll bla"), equalTo("you will bla"));
	}

	void testS() {
		assertThat(this.contraction.disassemblingContraction("you's bla"), equalTo("you s bla"));
	}

	void testD() {
		assertThat(this.contraction.disassemblingContraction("you'd bla"), equalTo("you d bla"));
	}

	void testWithMore() {
		assertThat(this.contraction.disassemblingContraction("you'd bla ain't won't you're"),
				equalTo("you d bla am not will not you are"));

	}
}
