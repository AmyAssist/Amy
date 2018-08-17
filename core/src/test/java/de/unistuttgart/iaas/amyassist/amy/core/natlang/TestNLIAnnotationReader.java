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
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;

/**
 * Test Cases for the AnnotationReader
 * 
 * @author Leon Kiefer, Felix Burk
 */
class TestNLIAnnotationReader {

	@Test
	public void testGrammar() {
		Set<Method> grammars = NLIAnnotationReader.getValidIntentMethods(Plugin.class);

		assertThat(grammars, hasSize(2));
	}

	@SpeechCommand
	class Plugin {
		@Intent("count")
		public String count(Map<String, EntityData> test) {
			return "1";
		}

		@Intent("say (hello|test)")
		public String say(Map<String, EntityData> test) {
			return "";
		}
	}

	@Test
	public void testIllegalTypes() {
		assertThrows(IllegalArgumentException.class, () -> NLIAnnotationReader.getValidIntentMethods(Broken.class));
	}

	@SpeechCommand
	class Broken {
		@Intent("count")
		public String count(String s) {
			return "";
		}
	}

	@Test
	public void testIllegalReturnType() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> NLIAnnotationReader.getValidIntentMethods(BrokenReturnType.class)).getMessage();
		assertThat(message, equalTo("The returntype of a method annotated with @Intent should be String."));
	}

	@SpeechCommand
	class BrokenReturnType {
		@Intent("count")
		public int count(Map<String, EntityData> tes) {
			return 0;
		}
	}

}
