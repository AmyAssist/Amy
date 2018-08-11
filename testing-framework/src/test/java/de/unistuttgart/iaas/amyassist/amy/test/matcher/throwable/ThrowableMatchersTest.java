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
import static org.hamcrest.Matchers.not;

import java.io.IOException;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ThrowableMatchers}
 * 
 * @author Tim Neumann
 */
class ThrowableMatchersTest {

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossed(java.lang.Class, java.lang.String, org.hamcrest.Matcher)}.
	 */
	@Test
	void testTossedClassOfQextendsThrowableStringMatcherOfThrowable() {
		Matcher<Throwable> matcher = tossed(RuntimeException.class, "Message", tossed(IOException.class));

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
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossedExactly(java.lang.Class, java.lang.String, org.hamcrest.Matcher)}.
	 */
	@Test
	void testTossedExactlyClassOfQextendsThrowableStringMatcherOfThrowable() {
		Matcher<Throwable> matcher = tossedExactly(RuntimeException.class, "Message", tossed(IOException.class));

		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new IllegalStateException("Message", new IOException()), not(matcher));
		MatcherAssert.assertThat(new Exception("Message", new IOException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException("msg", new IOException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException("Message", new RuntimeException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException("Message"), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(), not(matcher));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossed(java.lang.Class, java.lang.String)}.
	 */
	@Test
	void testTossedClassOfQextendsThrowableString() {
		Matcher<Throwable> matcher = tossed(RuntimeException.class, "Message");

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
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossedExactly(java.lang.Class, java.lang.String)}.
	 */
	@Test
	void testTossedExactlyClassOfQextendsThrowableString() {
		Matcher<Throwable> matcher = tossedExactly(RuntimeException.class, "Message");

		MatcherAssert.assertThat(new RuntimeException("Message"), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new IllegalStateException("Message"), not(matcher));
		MatcherAssert.assertThat(new Exception("Message"), not(matcher));
		MatcherAssert.assertThat(new RuntimeException("msg"), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(), not(matcher));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossed(java.lang.Class, org.hamcrest.Matcher)}.
	 */
	@Test
	void testTossedClassOfQextendsThrowableMatcherOfThrowable() {
		Matcher<Throwable> matcher = tossed(RuntimeException.class, tossed(IOException.class));

		MatcherAssert.assertThat(new RuntimeException(new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException(new IOException()), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);
		MatcherAssert.assertThat(new IllegalStateException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new Exception(new IOException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(new RuntimeException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(), not(matcher));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossedExactly(java.lang.Class, org.hamcrest.Matcher)}.
	 */
	@Test
	void testTossedExactlyClassOfQextendsThrowableMatcherOfThrowable() {
		Matcher<Throwable> matcher = tossedExactly(RuntimeException.class, tossed(IOException.class));

		MatcherAssert.assertThat(new RuntimeException(new IOException()), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new IllegalStateException(new IOException()), not(matcher));
		MatcherAssert.assertThat(new Exception(new IOException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(new RuntimeException()), not(matcher));
		MatcherAssert.assertThat(new RuntimeException(), not(matcher));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossed(java.lang.Class)}.
	 */
	@Test
	void testTossedClassOfQextendsThrowable() {
		Matcher<Throwable> matcher = tossed(RuntimeException.class);

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
	 * {@link de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers#tossedExactly(java.lang.Class)}.
	 */
	@Test
	void testTossedExactlyClassOfQextendsThrowable() {
		Matcher<Throwable> matcher = tossedExactly(RuntimeException.class);

		MatcherAssert.assertThat(new RuntimeException(), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message"), matcher);
		MatcherAssert.assertThat(new RuntimeException(new IOException()), matcher);
		MatcherAssert.assertThat(new RuntimeException("Message", new IOException()), matcher);

		MatcherAssert.assertThat(new IllegalStateException(), not(matcher));
		MatcherAssert.assertThat(new Exception(), not(matcher));
	}

}
