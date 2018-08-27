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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Streams;

/**
 * Tests for {@link TopicLevel}
 * 
 * @author Tim Neumann
 */
class TopicLevelTest {

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#TopicLevelImpl(java.lang.String)}. with
	 * a name containing a slash
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("namesWithSlash")
	void testTopicLevelImplIllegalNames1(String name) throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new TopicLevelImpl(name),
				"Should throw a illegal argument exception");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#TopicLevelImpl(java.lang.String)}. with
	 * a name containing a + or #
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("namesWithSpecialCharacters")
	void testTopicLevelImplIllegalNames2(String name) throws Exception {
		Assertions.assertThrows(TopicFormatException.class, () -> new TopicLevelImpl(name),
				"Should throw a topic format exception");
	}

	/**
	 * @return A stream of illegal topic names
	 */
	static Stream<String> namesWithSpecialCharacters() {
		List<String> vars = new ArrayList<>();
		vars.add("+topic");
		vars.add("topic+");
		vars.add("+topic+");
		vars.add("to+pic");
		vars.add("++");
		vars.add("++++");
		vars.add("#topic");
		vars.add("topic#");
		vars.add("#topic#");
		vars.add("to#pic");
		vars.add("##");
		vars.add("####");
		vars.add("$+");
		vars.add("$#");
		return vars.stream();
	}

	/**
	 * @return A stream of illegal topic names
	 */
	static Stream<String> namesWithSlash() {
		List<String> vars = new ArrayList<>();
		vars.add("/topic");
		vars.add("topic/");
		vars.add("/topic/");
		vars.add("to/pic");
		vars.add("/");
		vars.add("//");
		vars.add("/////");
		return vars.stream();
	}

	/**
	 * @return A stream of normal topic names
	 */
	static Stream<String> normalNames() {
		List<String> vars = new ArrayList<>();
		vars.add("home");
		vars.add("topic");
		vars.add("level");
		vars.add("test");
		vars.add("äüö");
		vars.add("LEVEL");
		vars.add("LEvel");
		vars.add("Level Test");
		vars.add("!\\}][{€@\"§%$&()=?,;.:-_<>|'*");
		vars.add("");
		return vars.stream();
	}

	/**
	 * @return A stream of valid topic names
	 */
	static Stream<String> validNames() {
		return Streams.concat(TopicLevelTest.normalNames(), Arrays.asList("+", "#").stream());
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#getStringRepresentation()}.
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
		TopicLevel tl = new TopicLevelImpl(name);
		Assertions.assertEquals(name, tl.getStringRepresentation(), "StringRepresentation is not the same as the name");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#isNormalLevel()}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("normalNames")
	void testIsNormalLevel(String name) throws Exception {
		TopicLevel tl = new TopicLevelImpl(name);
		Assertions.assertTrue(tl.isNormalLevel(), "Should be a normal level.");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#isNormalLevel()} when
	 * the level is not normal
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testIsNormalLevelNot() throws Exception {
		TopicLevel tl1 = new TopicLevelImpl("#");
		TopicLevel tl2 = new TopicLevelImpl("+");
		Assertions.assertFalse(tl1.isNormalLevel(), "# Should not be a normal level.");
		Assertions.assertFalse(tl2.isNormalLevel(), "+ Should not be a normal level.");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#isSingleLevelWildcard()}.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testIsSingleLevelWildcard() throws Exception {
		TopicLevel tl = new TopicLevelImpl("+");
		Assertions.assertTrue(tl.isSingleLevelWildcard(), "+ Should be a single level wildcard.");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#isSingleLevelWildcard()} when the level
	 * is not a single level wildcard
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testIsSingleLevelWildcardNot(String name) throws Exception {
		if (name.equals("+"))
			return;
		TopicLevel tl = new TopicLevelImpl(name);
		Assertions.assertFalse(tl.isSingleLevelWildcard(), "Should not be a single level wildcard.");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#isMultiLevelWildcard()}.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testIsMultiLevelWildcard() throws Exception {
		TopicLevel tl = new TopicLevelImpl("#");
		Assertions.assertTrue(tl.isMultiLevelWildcard(), "# Should be a multi-level wildcard.");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#isMultiLevelWildcard()}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testIsMultiLevelWildcardNot(String name) throws Exception {
		if (name.equals("#"))
			return;
		TopicLevel tl = new TopicLevelImpl(name);
		Assertions.assertFalse(tl.isMultiLevelWildcard(), "Should not be a multi-level wildcard.");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#toString()}.
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
		TopicLevel tl = new TopicLevelImpl(name);
		Assertions.assertEquals(name, tl.toString(), "toString is not the same as the name");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#hashCode()}.
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
		TopicLevel tl1 = new TopicLevelImpl(name);
		TopicLevel tl2 = new TopicLevelImpl(new String(name));
		Assertions.assertEquals(tl1.hashCode(), tl2.hashCode(), "Hashcodes should match.");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#equals(Object)}.
	 * 
	 * @param name
	 *            The name of the topic to test.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("validNames")
	void testEqual(String name) throws Exception {
		TopicLevel tl1 = new TopicLevelImpl(name);
		TopicLevel tl2 = new TopicLevelImpl(new String(name));
		Assertions.assertTrue(tl1.equals(tl2), "Two levels with the same name should be equal");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#equals(Object)} when
	 * they are not equal.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testEqualNot() throws Exception {
		TopicLevel tl1 = new TopicLevelImpl("level");
		TopicLevel tl2 = new TopicLevelImpl("Level");
		TopicLevel tl3 = new TopicLevelImpl("+");
		TopicLevel tl4 = new TopicLevelImpl("#");
		Assertions.assertFalse(tl1.equals(tl2), tl1 + " and " + tl2 + " should not be equal");
		Assertions.assertFalse(tl1.equals(tl3), tl1 + " and " + tl3 + " should not be equal");
		Assertions.assertFalse(tl1.equals(tl4), tl1 + " and " + tl4 + " should not be equal");
		Assertions.assertFalse(tl2.equals(tl3), tl2 + " and " + tl3 + " should not be equal");
		Assertions.assertFalse(tl2.equals(tl4), tl2 + " and " + tl4 + " should not be equal");
		Assertions.assertFalse(tl3.equals(tl4), tl3 + " and " + tl4 + " should not be equal");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.messagehub.topic.TopicLevelImpl#equals(Object)} when
	 * they are not the same type.
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@Test
	void testEqualNotSameType() throws Exception {
		TopicLevel tl1 = new TopicLevelImpl("level");
		TopicLevel tl2 = new TopicLevelImpl("+");
		TopicLevel tl3 = new TopicLevelImpl("#");
		Assertions.assertFalse(tl1.equals(new Object()), tl1 + "should not be equal to an empty object");
		Assertions.assertFalse(tl2.equals(new Object()), tl2 + "should not be equal to an empty object");
		Assertions.assertFalse(tl3.equals(new Object()), tl3 + "should not be equal to an empty object");
	}

}
