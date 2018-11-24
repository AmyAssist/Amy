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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.natlang.NLIAnnotationReader;
import de.unistuttgart.iaas.amyassist.amy.natlang.userinteraction.EntityDataImpl;

/**
 * Test Cases for the AnnotationReader
 *
 * @author Felix Burk
 */
class TestNLIAnnotationReader {

	@Test
	public void testIntent() {
		Set<Method> intents = NLIAnnotationReader.getValidIntentMethods(Plugin.class);

		assertThat(intents, hasSize(2));
	}

	@SpeechCommand
	class Plugin {
		@Intent
		public String count(Map<String, EntityDataImpl> test) {
			return "1";
		}

		@Intent
		public String say(Map<String, EntityDataImpl> test) {
			return "";
		}
	}

	@Test
	public void testIllegalTypes() {
		assertThrows(IllegalArgumentException.class, () -> NLIAnnotationReader.getValidIntentMethods(Broken.class));
	}

	@SpeechCommand
	class Broken {
		@Intent
		public String count(String s) {
			return "";
		}
	}

	@Test
	public void testIllegalReturnType() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> NLIAnnotationReader.getValidIntentMethods(BrokenReturnType.class)).getMessage();
		assertThat(message, equalTo("The returntype of a method annotated with @Intent should be either " +
				"String or Response."));
	}

	@SpeechCommand
	class BrokenReturnType {
		@Intent
		public int count(Map<String, EntityDataImpl> tes) {
			return 0;
		}
	}

	@Test
	public void testIllegalReturnTypeEntityProvider() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> NLIAnnotationReader.getValidEnityProviderMethod(BrokenReturnTypeEntityProvider.class, ""))
						.getMessage();
		assertThat(message,
				equalTo("The returntype of a method annotated with @EntityProvider should be List<String>."));
	}

	class BrokenReturnTypeEntityProvider {
		@EntityProvider("test")
		public int count() {
			return 0;
		}
	}

	@Test
	void testGetValidEntityProviderMethod() {
		assertThat(NLIAnnotationReader.getValidEnityProviderMethod(GoodEntityProvider.class, "test").getName(),
				equalTo("count"));
		assertThat(NLIAnnotationReader.getValidEnityProviderMethod(GoodEntityProvider.class, "bla").getName(),
				equalTo("bla"));
		assertThat(NLIAnnotationReader.getValidEnityProviderMethod(GoodEntityProvider.class, "blub").getName(),
				equalTo("blub"));
		assertThat(NLIAnnotationReader.getValidEnityProviderMethod(GoodEntityProvider.class, "bl"), equalTo(null));
	}

	@SpeechCommand
	class GoodEntityProvider {
		@EntityProvider("bla")
		public List<String> bla() {
			return new ArrayList<>();
		}

		@EntityProvider("blub")
		@EntityProvider("blubblub")
		public List<String> blub() {
			return new ArrayList<>();
		}

		@EntityProvider("test")
		public List<String> count() {
			return new ArrayList<>();
		}
	}

	@Test
	public void testIllegalParameterTypeEntityProvider() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> NLIAnnotationReader.getValidEnityProviderMethod(BrokenParamterEntityProvider.class, ""))
						.getMessage();
		assertThat(message, equalTo("The method " + BrokenParamterEntityProvider.class.getMethods()[0].toString()
				+ " should not have paramters"));
	}

	@SpeechCommand
	class BrokenParamterEntityProvider {
		@EntityProvider("test")
		public List<String> count(int i) {
			return new ArrayList<>();
		}
	}

	@Test
	public void testIllegalExceptionEntityProvider() {
		String message = assertThrows(IllegalArgumentException.class,
				() -> NLIAnnotationReader.getValidEnityProviderMethod(IllegalExceptionEntityProvider.class, ""))
						.getMessage();
		assertThat(message, equalTo("The method annotated with @EntityProvider should should not throw exceptions."));
	}

	class IllegalExceptionEntityProvider {
		@EntityProvider("test")
		public List<String> count() throws IllegalArgumentException {
			return new ArrayList<>();
		}
	}
}
