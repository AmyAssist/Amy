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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Streams;

/**
 * Tests {@link TopicName}
 * 
 * @author Tim Neumann
 */
class TopicNameTest {
	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicNameImpl#validateTopicString(java.lang.String)}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testValidateTopicString(String name) throws Exception {
		TopicNameImpl topicName = new TopicNameImpl("test");
		topicName.validateTopicString(name);
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicNameImpl#validateTopicString(java.lang.String)}
	 * when the string is not valid
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("namesWithWildcard")
	void testValidateTopicStringNot(String name) throws Exception {
		TopicNameImpl topicName = new TopicNameImpl("test");
		Assertions.assertThrows(TopicFormatException.class, () -> topicName.validateTopicStringForName(name),
				"Expected topic format exception");
	}

	/**
	 * @return A stream of normal topic names
	 */
	static Stream<String> validNames() {
		List<String> vars = new ArrayList<>();
		vars.add("home");
		vars.add("/home");
		vars.add("home/topic/test");
		vars.add("/");
		vars.add("/home/topic/test");
		vars.add("home/topic/test/");
		vars.add("home/topic/test");
		vars.add("home/TOPIC/test");
		vars.add("LEvel");
		vars.add("!\\}][{€@\"§%$&()=?,;.:-_<>|'*");
		return vars.stream();
	}

	/**
	 * @return A stream of topic names with wildcards
	 */
	static Stream<String> namesWithWildcard() {
		List<String> vars = new ArrayList<>();
		vars.add("home/+");
		vars.add("/home/#");
		vars.add("home/+/test");
		vars.add("+");
		vars.add("#");
		return vars.stream();
	}

	/**
	 * @return A stream of illegal topic names (which are illegal for filters and topic names)
	 */
	static Stream<String> alwaysInvalidNames() {
		List<String> vars = new ArrayList<>();
		vars.add("");
		vars.add("home" + Character.toString((char) 0x0000));
		vars.add("ho" + Character.toString((char) 0x0000) + "me/topic/test");
		vars.add(StringUtils.leftPad("TestString", 65536, "*"));
		vars.add(StringUtils.leftPad("TestString", 65546, "*"));
		return vars.stream();
	}

	/**
	 * @return A stream of invalid names for topic names
	 */
	static Stream<String> invalidNames() {
		return Streams.concat(TopicNameTest.namesWithWildcard(), TopicNameTest.alwaysInvalidNames());
	}

}
