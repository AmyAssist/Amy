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

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class NTupleTest {

	@Test
	void testHashCode() {
		assertThat(new NTuple<>(0).hashCode(), is(0));
	}

	@ParameterizedTest
	@MethodSource("dataSource")
	void testHashCodeForValue(int integer) {
		NTuple<Object> nTuple = new NTuple<>(1);
		nTuple.set(0, integer);
		assertThat(nTuple.hashCode(), is(integer));
	}

	@SuppressWarnings("unused")
	private static IntStream dataSource() {
		return new Random().ints(100);
	}

	@ParameterizedTest
	@MethodSource("objectSource")
	void testEquals(Object object) {
		assertThat(new NTuple<>(0), not(equalTo(object)));
		NTuple<Object> nTuple = new NTuple<>(1);
		nTuple.set(0, 564565);
		assertThat(nTuple, not(equalTo(object)));
	}

	@SuppressWarnings("unused")
	private static Stream<Object> objectSource() {
		return Stream.of("", "0", new Object(), 1, 0, -1, new NTuple<>(1), null);
	}

	@Test
	void testHashCodeOfNull() {
		assertThat(new NTuple<>(10).hashCode(), is(0));
	}

}
