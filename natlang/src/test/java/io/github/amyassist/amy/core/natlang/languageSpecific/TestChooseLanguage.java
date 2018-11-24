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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.languageSpecific;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.ChooseLanguage;
import de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.en.EnglishContraction;
import de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.en.EnglishDateTimeUtility;
import de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.en.EnglishNumberConversion;
import de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.en.EnglishStemmer;

/**
 * Test class for ChooseLanguage
 * 
 * @author Lars Buttgereit
 */
public class TestChooseLanguage {

	@Test
	void testChooseEnglish() {
		ChooseLanguage chooseLanguage = new ChooseLanguage("en", true);
		assertThat(chooseLanguage.getNumberConversion().getClass(), equalTo(EnglishNumberConversion.class));
		assertThat(chooseLanguage.getStemmer().getClass(), equalTo(EnglishStemmer.class));
		assertThat(chooseLanguage.getTimeUtility().getClass(), equalTo(EnglishDateTimeUtility.class));
		assertThat(chooseLanguage.getContraction().getClass(), equalTo(EnglishContraction.class));
	}

}
