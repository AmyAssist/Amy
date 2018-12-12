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
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link TopicFilter}
 * 
 * @author Tim Neumann
 */
class TopicFilterTest {

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.TopicFilterImpl#doesFilterMatch(io.github.amyassist.amy.messagehub.topic.TopicName)}
	 * without any wildcards.
	 * 
	 * @param name
	 *            The name for the filter and the topic name
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("simpleNames")
	void testDoesFilterMatchNormal(String name) throws Exception {
		Assertions.assertTrue(new TopicFilterImpl(name).doesFilterMatch(new TopicNameImpl(name)));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.TopicFilterImpl#doesFilterMatch(io.github.amyassist.amy.messagehub.topic.TopicName)}
	 * without any wildcards and not matching.
	 * 
	 * @param pair
	 *            A pair with a filter and a topic name
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("simpleNotMatchingPairs")
	void testDoesFilterMatchNormalNot(FilterNamePair pair) throws Exception {
		Assertions
				.assertFalse(new TopicFilterImpl(pair.getFilter()).doesFilterMatch(new TopicNameImpl(pair.getName())));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.TopicFilterImpl#doesFilterMatch(io.github.amyassist.amy.messagehub.topic.TopicName)}
	 * with single level wildcard.
	 * 
	 * @param pair
	 *            A pair with a filter and a topic name
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("singleLevelWcMatchingPairs")
	void testDoesFilterMatchSingleLevleWc(FilterNamePair pair) throws Exception {
		Assertions.assertTrue(new TopicFilterImpl(pair.getFilter()).doesFilterMatch(new TopicNameImpl(pair.getName())));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.TopicFilterImpl#doesFilterMatch(io.github.amyassist.amy.messagehub.topic.TopicName)}
	 * with single level wildcard and not matching.
	 * 
	 * @param pair
	 *            A pair with a filter and a topic name
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("singleLevelWcNotMatchingPairs")
	void testDoesFilterMatchSingleLevleWcNot(FilterNamePair pair) throws Exception {
		Assertions
				.assertFalse(new TopicFilterImpl(pair.getFilter()).doesFilterMatch(new TopicNameImpl(pair.getName())));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.TopicFilterImpl#doesFilterMatch(io.github.amyassist.amy.messagehub.topic.TopicName)}
	 * with multi level wildcard.
	 * 
	 * @param pair
	 *            A pair with a filter and a topic name
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("multiLevelWcMatchingPairs")
	void testDoesFilterMatchMultiLevleWc(FilterNamePair pair) throws Exception {
		Assertions.assertTrue(new TopicFilterImpl(pair.getFilter()).doesFilterMatch(new TopicNameImpl(pair.getName())));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.messagehub.topic.TopicFilterImpl#doesFilterMatch(io.github.amyassist.amy.messagehub.topic.TopicName)}
	 * with multi level wildcard and not matching.
	 * 
	 * @param pair
	 *            A pair with a filter and a topic name
	 * 
	 * @throws Exception
	 *             When an error occurs.
	 */
	@ParameterizedTest
	@MethodSource("multiLevelWcNotMatchingPairs")
	void testDoesFilterMatchMultiLevleWcNot(FilterNamePair pair) throws Exception {
		Assertions
				.assertFalse(new TopicFilterImpl(pair.getFilter()).doesFilterMatch(new TopicNameImpl(pair.getName())));
	}

	/**
	 * @return A stream of normal topic names
	 */
	static Stream<String> simpleNames() {
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
		vars.add("////");
		vars.add("$test/topic");
		vars.add("$/test/topic");
		vars.add("$");
		vars.add("$/TOPIC");
		return vars.stream();
	}

	/**
	 * @return A stream of normal filters and topics that should not match
	 */
	static Stream<FilterNamePair> simpleNotMatchingPairs() {
		List<FilterNamePair> vars = new ArrayList<>();
		vars.add(new FilterNamePair("/home/test", "home/test"));
		vars.add(new FilterNamePair("home/test/", "home/test"));
		vars.add(new FilterNamePair("/home/test/", "/home/test"));
		vars.add(new FilterNamePair("$/home/test/", "$home/test/"));
		vars.add(new FilterNamePair("$/home/test/", "$home/test"));
		vars.add(new FilterNamePair("/HOME/test", "/home/test"));
		vars.add(new FilterNamePair("/Home/test", "/home/test"));
		vars.add(new FilterNamePair("$/home/Test", "$/home/test"));
		vars.add(new FilterNamePair("$/home/test", "$/home/tEst"));
		return vars.stream();
	}

	/**
	 * @return A stream of filters with single level wildcards and topics that should match
	 */
	static Stream<FilterNamePair> singleLevelWcMatchingPairs() {
		List<FilterNamePair> vars = new ArrayList<>();
		vars.add(new FilterNamePair("/home/+/test", "/home/level/test"));
		vars.add(new FilterNamePair("/home/level/+", "/home/level/test"));
		vars.add(new FilterNamePair("/+/level/test", "/home/level/test"));
		vars.add(new FilterNamePair("/+/level/+", "/home/level/test"));
		vars.add(new FilterNamePair("/+/+/+", "/home/level/test"));
		vars.add(new FilterNamePair("/home/+/test", "/home/LEVEL/test"));
		vars.add(new FilterNamePair("/home/+/test", "/home/topic/test"));
		vars.add(new FilterNamePair("+/level/test", "home/level/test"));
		vars.add(new FilterNamePair("/home/level/+/", "/home/level/test/"));
		vars.add(new FilterNamePair("/home/level/+", "/home/level/"));
		return vars.stream();
	}

	/**
	 * @return A stream of filters with single level wildcards and topics that should not match
	 */
	static Stream<FilterNamePair> singleLevelWcNotMatchingPairs() {
		List<FilterNamePair> vars = new ArrayList<>();
		vars.add(new FilterNamePair("/home/+/test", "/home/level/TEST"));
		vars.add(new FilterNamePair("/home/level/+", "/home/level/test/"));
		vars.add(new FilterNamePair("+", "/home/level/test"));
		vars.add(new FilterNamePair("/+/level/", "home/level/"));
		vars.add(new FilterNamePair("/home/level/+", "/home/level"));
		vars.add(new FilterNamePair("+/test", "$home/test"));
		return vars.stream();
	}

	/**
	 * @return A stream of filters with multi-level wildcards and topics that should match
	 */
	static Stream<FilterNamePair> multiLevelWcMatchingPairs() {
		List<FilterNamePair> vars = new ArrayList<>();
		vars.add(new FilterNamePair("/home/#", "/home/level/test"));
		vars.add(new FilterNamePair("/home/#", "/home/LEVEL/test"));
		vars.add(new FilterNamePair("/home/#", "/home/"));
		vars.add(new FilterNamePair("/home/#", "/home"));
		vars.add(new FilterNamePair("/home/+/#", "/home/level/test"));
		vars.add(new FilterNamePair("#", "/home/level/test"));
		vars.add(new FilterNamePair("#", "home/level/test"));
		return vars.stream();
	}

	/**
	 * @return A stream of filters with multi-level wildcards and topics that should not match
	 */
	static Stream<FilterNamePair> multiLevelWcNotMatchingPairs() {
		List<FilterNamePair> vars = new ArrayList<>();
		vars.add(new FilterNamePair("/#", "home/level/test"));
		vars.add(new FilterNamePair("/home/#", "/test"));
		vars.add(new FilterNamePair("/+/test/#", "/home/level/whatever"));
		vars.add(new FilterNamePair("#", "$home/test"));
		vars.add(new FilterNamePair("#", "$home"));
		return vars.stream();
	}

	private static class FilterNamePair {
		private String f;
		private String n;

		/**
		 * Creates a new pair with the given values
		 * 
		 * @param filter
		 *            The filter
		 * @param name
		 *            The name
		 */
		public FilterNamePair(String filter, String name) {
			this.f = filter;
			this.n = name;
		}

		/**
		 * @return The filter
		 */
		public String getFilter() {
			return this.f;
		}

		/**
		 * @return The name
		 */
		public String getName() {
			return this.n;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.f + "|" + this.n;
		}
	}

}
