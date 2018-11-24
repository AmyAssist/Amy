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

package io.github.amyassist.amy.messagehub.topic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Streams;

/**
 * Tests {@link AbstractTopic}
 * 
 * @author Tim Neumann
 */
class AbstractTopicTest {

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#validateTopicString(java.lang.String)}.
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
		AbstractTopic.validateTopicString(name);
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#validateTopicString(java.lang.String)}
	 * when they are not valid.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 */
	@ParameterizedTest
	@MethodSource("invalidNames")
	void testValidateTopicStringNot(String name) {
		Assertions.assertThrows(TopicFormatException.class, () -> AbstractTopic.validateTopicString(name),
				"Expected a topic format exception.");
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#validateTopicLevels(java.util.List)}.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testValidateTopicLevels() throws Exception {
		AbstractTopic.validateTopicLevels(toTopicLevels("test", "", "level"));
		AbstractTopic.validateTopicLevels(toTopicLevels("", "test", ""));
		AbstractTopic.validateTopicLevels(toTopicLevels("test", "+", "level"));
		AbstractTopic.validateTopicLevels(toTopicLevels("test", "level", "#"));
		AbstractTopic.validateTopicLevels(toTopicLevels("+", "+", "#"));
		AbstractTopic.validateTopicLevels(toTopicLevels("#"));
		AbstractTopic.validateTopicLevels(toTopicLevels("+"));
		AbstractTopic.validateTopicLevels(toTopicLevels("", "#"));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#validateTopicLevels(java.util.List)} for
	 * invalid levels.
	 */
	@Test
	void testValidateTopicLevelsNot() {
		Assertions.assertThrows(TopicFormatException.class,
				() -> AbstractTopic.validateTopicLevels(toTopicLevels("test", "#", "level")),
				"Expected topic format excpetion");
		Assertions.assertThrows(TopicFormatException.class,
				() -> AbstractTopic.validateTopicLevels(toTopicLevels("", "#", "")), "Expected topic format excpetion");
		Assertions.assertThrows(TopicFormatException.class,
				() -> AbstractTopic.validateTopicLevels(toTopicLevels("#", "level")),
				"Expected topic format excpetion");
		Assertions.assertThrows(TopicFormatException.class,
				() -> AbstractTopic.validateTopicLevels(toTopicLevels("#", "")), "Expected topic format excpetion");
		Assertions.assertThrows(TopicFormatException.class,
				() -> AbstractTopic.validateTopicLevels(toTopicLevels("", "#", "")), "Expected topic format excpetion");
		Assertions.assertThrows(TopicFormatException.class,
				() -> AbstractTopic.validateTopicLevels(toTopicLevels("#", "+")), "Expected topic format excpetion");
	}

	private List<TopicLevel> toTopicLevels(String... names) throws Exception {
		List<TopicLevel> tlList = new ArrayList<>();
		for (String name : names) {
			tlList.add(new TopicLevelImpl(name));
		}
		return tlList;
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#getStringRepresentation()}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testGetStringRepresentation(String name) throws Exception {
		AbstractTopic t = new TestingTopic(name);
		Assertions.assertEquals(name, t.getStringRepresentation(),
				"String representation should be the same as the name.");
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#getTopicLevels()}.
	 * 
	 * @param levels
	 *            The levels to use.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validLevelLists")
	void testGetTopicLevels(List<String> levels) throws Exception {
		String topicName = levels.get(0);
		for (int i = 1; i < levels.size(); i++) {
			topicName += "/";
			topicName += levels.get(i);
		}

		AbstractTopic t = new TestingTopic(topicName);

		String[] topicLevelNames = levels.toArray(new String[levels.size()]);
		List<TopicLevel> topicLevels = toTopicLevels(topicLevelNames);

		Assertions.assertEquals(topicLevels, t.getTopicLevels(), "Wrong topic levels.");
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#isSpecialTopic()}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("specialNames")
	void testIsSpecialTopic(String name) throws Exception {
		AbstractTopic t = new TestingTopic(name);
		Assertions.assertTrue(t.isSpecialTopic(), "Should be a special topic");
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#toString()}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testToString(String name) throws Exception {
		AbstractTopic t = new TestingTopic(name);
		Assertions.assertEquals(name, t.toString(), "toString should be the same as the name.");
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#hashCode()}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testHashCode(String name) throws Exception {
		AbstractTopic t1 = new TestingTopic(name);
		AbstractTopic t2 = new TestingTopic(new String(name));
		Assertions.assertEquals(t1.hashCode(), t2.hashCode(), "Hashcodes should match.");
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#equals(java.lang.Object)}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testEquals(String name) throws Exception {
		AbstractTopic t1 = new TestingTopic(name);
		AbstractTopic t2 = new TestingTopic(new String(name));
		Assertions.assertTrue(t1.equals(t2), "A topic should be equal to another topic with the same name.");
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#equals(java.lang.Object)} when they are
	 * not equal.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testEqualsNot() throws Exception {
		AbstractTopic t1 = new TestingTopic("home/test");
		AbstractTopic t2 = new TestingTopic("Home/Test");
		AbstractTopic t3 = new TestingTopic("+/+");
		AbstractTopic t4 = new TestingTopic("#");
		Assertions.assertFalse(t1.equals(t2), t1 + " and " + t2 + " should not be equal");
		Assertions.assertFalse(t1.equals(t3), t1 + " and " + t3 + " should not be equal");
		Assertions.assertFalse(t1.equals(t4), t1 + " and " + t4 + " should not be equal");
		Assertions.assertFalse(t2.equals(t3), t2 + " and " + t3 + " should not be equal");
		Assertions.assertFalse(t2.equals(t4), t2 + " and " + t4 + " should not be equal");
		Assertions.assertFalse(t3.equals(t4), t3 + " and " + t4 + " should not be equal");
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.AbstractTopic#equals(java.lang.Object)} when they are
	 * not the same type.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testEqualsNotSameType() throws Exception {
		AbstractTopic t1 = new TestingTopic("home/test");
		AbstractTopic t2 = new TestingTopic("+/+");
		AbstractTopic t3 = new TestingTopic("#");
		Assertions.assertFalse(t1.equals(new Object()), t1 + " should not be equal to a empty object.");
		Assertions.assertFalse(t2.equals(new Object()), t2 + " should not be equal to a empty object.");
		Assertions.assertFalse(t3.equals(new Object()), t3 + " should not be equal to a empty object.");
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
		vars.add("home/+/test");
		vars.add("home/+/+");
		vars.add("home/#");
		vars.add("home/+/#");
		vars.add("+");
		vars.add("#");
		return vars.stream();
	}

	/**
	 * @return A stream of special topic names
	 */
	static Stream<String> specialNames() {
		return AbstractTopicTest.normalNames().filter(s -> !(s.equals("+") || s.equals("#"))).map(s -> "$" + s);
	}

	/**
	 * @return A stream of valid topic names
	 */
	static Stream<String> validNames() {
		return Streams.concat(AbstractTopicTest.normalNames(), AbstractTopicTest.specialNames());
	}

	/**
	 * @return A stream of invalid topic names
	 */
	static Stream<String> invalidNames() {
		List<String> vars = new ArrayList<>();
		vars.add("");
		vars.add("home" + Character.toString((char) 0x0000));
		vars.add("ho" + Character.toString((char) 0x0000) + "me/topic/test");
		vars.add(StringUtils.leftPad("TestString", 65536, "*"));
		vars.add(StringUtils.leftPad("TestString", 65546, "*"));
		return vars.stream();
	}

	/**
	 * @return A stream of valid topic names split into the levels
	 */
	static Stream<List<String>> validLevelLists() {
		return AbstractTopicTest.validNames().map(s -> Arrays.asList(s.split("/", -1)));
	}

}
