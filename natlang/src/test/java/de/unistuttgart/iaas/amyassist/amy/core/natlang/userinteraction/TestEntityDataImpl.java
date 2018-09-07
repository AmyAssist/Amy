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

package de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.natlang.languagespecifics.ChooseLanguage;
import de.unistuttgart.iaas.amyassist.amy.natlang.userinteraction.EntityDataImpl;

/**
 * test class for the entityDataImpl
 * 
 * @author Lars Buttgereit
 *
 */
public class TestEntityDataImpl {

	@Test
	void testgetNumber() {
		EntityDataImpl input = new EntityDataImpl("10", new ChooseLanguage("en", false).getTimeUtility());
		assertThat(input.getNumber(), equalTo(10));
	}

	@Test
	void testgetInvalidNumber() {
		EntityDataImpl input = new EntityDataImpl("ABC", new ChooseLanguage("en", false).getTimeUtility());
		assertThat(input.getNumber(), equalTo(Integer.MIN_VALUE));
	}

	@Test
	void testgetString() {
		EntityDataImpl input = new EntityDataImpl("ABC", new ChooseLanguage("en", false).getTimeUtility());
		assertThat(input.getString(), equalTo("ABC"));
	}

	@Test
	void testFormatTimeNull() {
		assertThat(new EntityDataImpl(null, new ChooseLanguage("en", false).getTimeUtility()).getString(),
				equalTo(null));
	}
}
