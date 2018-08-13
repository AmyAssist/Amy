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

package de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable;

import static de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ThrowableMatchers} and {@link ThrowableMatcher}
 * 
 * @author Tim Neumann
 */
class ThrowableMatchersTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossed(org.hamcrest.Matcher, org.hamcrest.Matcher, org.hamcrest.Matcher)}.
	 */
	@Test
	void testTossedClassStringThrowable() {
		Matcher<Throwable> matcher = tossed(instanceOf(RuntimeException.class), equalTo("Message"),
				tossed(instanceOf(IOException.class)));

		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new Exception("Message", new IOException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException("msg", new IOException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException("Message", new RuntimeException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException("Message"), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(), not(matcher));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossed(org.hamcrest.Matcher, org.hamcrest.Matcher)}.
	 */
	@Test
	void testTossedClassOfString() {
		Matcher<Throwable> matcher = tossed(instanceOf(RuntimeException.class), equalTo("Message"));

		MatcherAssert.assertThat(new RuntimeException("Message"), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message"), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new Exception("Message"), not(matcher));
		MatcherAssert.assertThat(new RuntimeException("msg"), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(), not(matcher));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossed(org.hamcrest.Matcher)}.
	 */
	@Test
	void testTossedClass() {
		Matcher<Throwable> matcher = tossed(instanceOf(RuntimeException.class));

		MatcherAssert.assertThat(new RuntimeException(), matcher);
		MatcherAssert.assertThat(new IllegalStateException(), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message"), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message"), matcher);
		MatcherAssert.assertThat(new RuntimeException(new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException(new IOException()), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new Exception(), not(matcher));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossedWithCause(org.hamcrest.Matcher, org.hamcrest.Matcher)}.
	 */
	@Test
	void testTossedWithCause() {
		Matcher<Throwable> matcher = tossedWithCause(instanceOf(RuntimeException.class),
				tossed(instanceOf(IOException.class)));

		MatcherAssert.assertThat(new RuntimeException(new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException(new IOException()), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new Exception(new IOException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(new RuntimeException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(), not(matcher));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossedAny()}.
	 */
	@Test
	void testTossedAny() {
		Matcher<Throwable> matcher = tossedAny();

		MatcherAssert.assertThat(new Throwable(), matcher);
		MatcherAssert.assertThat(new RuntimeException(), matcher);
		MatcherAssert.assertThat(new IllegalStateException(), matcher);
		MatcherAssert.assertThat(new Throwable("Message"), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message"), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message"), matcher);
		MatcherAssert.assertThat(new Throwable(new IOException()), matcher);
		MatcherAssert.assertThat(new RuntimeException(new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException(new IOException()), matcher);
		MatcherAssert.assertThat(new Throwable("Message", new IOException()), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message", new IOException()), matcher);
	}

}
