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

package de.unistuttgart.iaas.amyassist.amy.messagehub.topic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.test.matcher.throwable.ThrowableMatchers;

/**
 * Tests {@link TopicFactory}
 * 
 * @author Tim Neumann
 */
@ExtendWith(FrameworkExtension.class)
class TopicFactoryTest {
	@Reference
	private TestFramework testFramework;
	private TopicFactory tf;

	@BeforeEach
	void init() throws Exception {
		this.tf = this.testFramework.setServiceUnderTest(TopicFactoryService.class);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactoryService#createTopicFilter(java.lang.String)}.
	 * 
	 * @param name
	 *            The name of the topic filter.
	 */
	@ParameterizedTest
	@MethodSource("normalNames")
	void testCreateTopicFilter(String name) {
		Assertions.assertEquals(name, this.tf.createTopicFilter(name).getStringRepresentation());
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactoryService#createTopicName(java.lang.String)}.
	 * 
	 * @param name
	 *            The name of the topic filter.
	 */
	@ParameterizedTest
	@MethodSource("normalNames")
	void testCreateTopicName(String name) {
		Assertions.assertEquals(name, this.tf.createTopicName(name).getStringRepresentation());
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactoryService#createTopicFilter(java.lang.String)}
	 * when passing a invalid filter string
	 */
	@Test
	void testCreateTopicFilterException() {
		Exception exc = null;
		try {
			this.tf.createTopicFilter("/+test/");
		} catch (IllegalArgumentException e) {
			exc = e;
		}

		MatcherAssert.assertThat("Expected excpetion not thrown", exc,
				ThrowableMatchers.tossedWithCause(Matchers.instanceOf(IllegalArgumentException.class),
						ThrowableMatchers.tossed(Matchers.instanceOf(TopicFormatException.class))));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicFactoryService#createTopicName(java.lang.String)}
	 * when passing a invalid name string
	 */
	@Test
	void testCreateTopicNameException() {
		Exception exc = null;
		try {
			this.tf.createTopicName("/+/");
		} catch (IllegalArgumentException e) {
			exc = e;
		}

		MatcherAssert.assertThat("Expected excpetion not thrown", exc,
				ThrowableMatchers.tossedWithCause(Matchers.instanceOf(IllegalArgumentException.class),
						ThrowableMatchers.tossed(Matchers.instanceOf(TopicFormatException.class))));
	}

	/**
	 * @return A stream of normal topic names
	 */
	static Stream<String> normalNames() {
		List<String> vars = new ArrayList<>();
		vars.add("home");
		vars.add("/home");
		vars.add("home/topic/test");
		vars.add("/");
		vars.add("/home/topic/test");
		vars.add("home/topic/test/");
		vars.add("/home/topic/test/");
		vars.add("home/TOPIC/test");
		vars.add("LEvel");
		vars.add("Test Topic");
		vars.add("Test Topic/Level");
		vars.add("!\\}][{€@\"§%$&()=?,;.:-_<>|'*");
		return vars.stream();
	}
}
