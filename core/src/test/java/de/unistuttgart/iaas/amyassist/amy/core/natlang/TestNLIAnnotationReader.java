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
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * Test Cases for the AnnotationReader
 * 
 * @author Leon Kiefer
 */
class TestNLIAnnotationReader {

	@Test
	void testSpeechKeyword() {
		String[] speechKeyword = NLIAnnotationReader.getSpeechKeywords(Plugin.class);

		assertThat(speechKeyword, is(arrayWithSize(2)));
		assertThat(speechKeyword, is(arrayContainingInAnyOrder("test", "unittest")));
	}

	@Test
	public void testGrammar() {
		Set<Method> grammars = NLIAnnotationReader
				.getValidNLIMethods(Plugin.class);

		assertThat(grammars, hasSize(2));
	}

	@Test
	public void testNoSpeechKeyword() {
		assertThrows(RuntimeException.class,
				() -> NLIAnnotationReader.getSpeechKeywords(TestNLIAnnotationReader.class));
	}

	@SpeechCommand({ "test", "unittest" })
	class Plugin {
		@Grammar("count")
		public String count(String... s) {
			return "1";
		}

		@Grammar("say (hello|test)")
		public String say(String[] s) {
			return s[0];
		}
	}

	@Test
	public void testIllegalTypes() {
		assertThrows(IllegalArgumentException.class,
				() -> NLIAnnotationReader.getValidNLIMethods(Brocken.class));
	}

	@SpeechCommand({})
	class Brocken {
		@Grammar("count")
		public int count(int i) {
			return i;
		}
	}

	@Test
	public void testIllegalReturnType() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> NLIAnnotationReader.getValidNLIMethods(BrockenReturnType.class)).getMessage();
		assertThat(message, equalTo("The returntype of a method annotated with @Grammar should be String."));
	}

	@SpeechCommand({})
	class BrockenReturnType {
		@Grammar("count")
		public int count(String[] i) {
			return 0;
		}
	}

}
