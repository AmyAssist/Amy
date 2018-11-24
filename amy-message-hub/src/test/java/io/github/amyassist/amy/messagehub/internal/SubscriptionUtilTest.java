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

package de.unistuttgart.iaas.amyassist.amy.messagehub.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.unistuttgart.iaas.amyassist.amy.messagehub.annotations.Subscription;

/**
 * Test for Util
 * 
 * @author Leon Kiefer
 */
class SubscriptionUtilTest {

	@ParameterizedTest
	@MethodSource("validMethods")
	void testVaildMethods(Method validMethod) {
		SubscriptionUtil.assertValidSubscriptionMethod(validMethod);
	}

	static Stream<Method> validMethods() throws SecurityException {
		return MethodUtils.getMethodsListWithAnnotation(TestMessageReceiver.class, Subscription.class, false, true)
				.stream();

	}

	@ParameterizedTest
	@MethodSource("invalidMethods")
	void testInvaildMethods(Method invalidMethod) {
		assertThrows(IllegalArgumentException.class,
				() -> SubscriptionUtil.assertValidSubscriptionMethod(invalidMethod));
	}

	static Stream<Method> invalidMethods() throws SecurityException {
		return MethodUtils
				.getMethodsListWithAnnotation(TestInvalidMessageReceiver.class, Subscription.class, false, true)
				.stream();
	}

}
