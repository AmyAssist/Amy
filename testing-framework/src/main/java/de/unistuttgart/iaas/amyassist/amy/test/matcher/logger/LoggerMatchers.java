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

package de.unistuttgart.iaas.amyassist.amy.test.matcher.logger;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import com.google.common.collect.ImmutableList;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;

/**
 * A Collection of common Matcher for the Logger
 * 
 * @author Leon Kiefer
 */
public class LoggerMatchers {

	/**
	 * Creates a matcher that matches if the given LoggingEvent was logged in the logger
	 * 
	 * @param loggingEvent
	 *            a matcher that matches the LoggingEvent that should have been logged
	 */
	public static Matcher<TestLogger> hasLogged(Matcher<LoggingEvent> loggingEvent) {
		return new TypeSafeMatcher<TestLogger>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("logged ");
				description.appendDescriptionOf(loggingEvent);
			}

			@Override
			protected boolean matchesSafely(TestLogger item) {
				ImmutableList<LoggingEvent> allLoggingEvents = item.getAllLoggingEvents();
				return Matchers.hasItem(loggingEvent).matches(allLoggingEvents);
			}

			/**
			 * @see org.hamcrest.TypeSafeMatcher#describeMismatchSafely(java.lang.Object, org.hamcrest.Description)
			 */
			@Override
			protected void describeMismatchSafely(TestLogger item, Description mismatchDescription) {
				mismatchDescription.appendValueList("", ", ", "", item.getAllLoggingEvents());
			}
		};

	}

	/**
	 * Create a Matcher that matches the count of LoggingEvents in the logger
	 * 
	 * @param countMatcher
	 *            a matcher for the count of LoggingEvents in the logger
	 */
	public static Matcher<TestLogger> loggingEventCount(Matcher<Integer> countMatcher) {
		return new TypeSafeMatcher<TestLogger>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("logged ");
				description.appendDescriptionOf(countMatcher);
				description.appendText("events");
			}

			@Override
			protected boolean matchesSafely(TestLogger item) {
				int count = item.getAllLoggingEvents().size();
				return countMatcher.matches(count);
			}
		};

	}

	/**
	 * 
	 * @param message
	 */
	public static Matcher<LoggingEvent> error(String message) {
		return new LoggingEventMatcher(Level.ERROR, message);
	}

	/**
	 * 
	 * @param message
	 * @param arguments
	 */
	public static Matcher<LoggingEvent> error(String message, Object... arguments) {
		return new LoggingEventMatcher(Level.ERROR, message, arguments);
	}

	/**
	 * @param string
	 * @param runtimeException
	 */
	public static Matcher<LoggingEvent> error(String message, Matcher<Throwable> runtimeException) {
		return new LoggingEventMatcher(Level.ERROR, message, runtimeException);
	}

	/**
	 * 
	 * @param message
	 */
	public static Matcher<LoggingEvent> info(String message) {
		return new LoggingEventMatcher(Level.INFO, message);
	}

	/**
	 * 
	 * @param message
	 * @param arguments
	 */
	public static Matcher<LoggingEvent> info(String message, Object... arguments) {
		return new LoggingEventMatcher(Level.INFO, message, arguments);
	}

	/**
	 * @param string
	 * @param runtimeException
	 */
	public static Matcher<LoggingEvent> info(String message, Matcher<Throwable> runtimeException) {
		return new LoggingEventMatcher(Level.INFO, message, runtimeException);
	}

	// TODO other log levels
}
