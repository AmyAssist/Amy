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

package de.unistuttgart.iaas.amyassist.amy.core.di.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.unistuttgart.iaas.amyassist.amy.core.di.NotAService;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service1;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service12;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service19;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service2;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service3;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service4;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service5;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service7API;
import de.unistuttgart.iaas.amyassist.amy.core.di.Service8;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceWithConstructor;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceWithPrivateConstructor;

/**
 * Tests for the DI Util
 * 
 * @author Leon Kiefer
 */
class UtilTest {

	@ParameterizedTest
	@ValueSource(classes = { Service1.class, Service2.class, Service3.class, Service4.class, Service5.class,
			Service12.class })
	void testIsValidServiceClassTrue(Class<?> testClass) {
		assertThat(Util.isValidServiceClass(testClass), is(true));
	}

	@ParameterizedTest
	@ValueSource(classes = { ServiceWithConstructor.class, WrongAnnotationUse.class, Service8.class, Service7API.class,
			NotAService.class, Service19.class })
	void testIsValidServiceClassFalse(Class<?> testClass) {
		assertThat(Util.isValidServiceClass(testClass), is(false));
	}

	@ParameterizedTest
	@ValueSource(classes = { Service1.class, WrongAnnotationUse.class })
	void testHasValidConstructorsTrue(Class<?> testClass) {
		assertThat(Util.hasValidConstructors(testClass), is(true));
	}

	@ParameterizedTest
	@ValueSource(classes = { ServiceWithConstructor.class, ServiceWithPrivateConstructor.class, Service8.class })
	void testHasValidConstructorsFalse(Class<?> testClass) {
		assertThat(Util.hasValidConstructors(testClass), is(false));
	}

	@ParameterizedTest
	@MethodSource("postConstructMe")
	void testPostConstruct(Object instance) {
		assertThrows(RuntimeException.class, () -> Util.postConstruct(instance));
	}

	static Stream<Object> postConstructMe() {
		return Stream.of(new Service19());
	}

	@Test
	void testInjectException() {
		assertThrows(IllegalArgumentException.class,
				() -> Util.inject(new Service2(), new Object(), FieldUtils.getField(Service2.class, "service1", true)));

		Util.inject(new Service2(), null, FieldUtils.getField(Service2.class, "service1", true));
	}

}
