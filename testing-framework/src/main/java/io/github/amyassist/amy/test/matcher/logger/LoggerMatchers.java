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

package io.github.amyassist.amy.test.matcher.logger;

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

	private LoggerMatchers() {
		// hide constructor
	}

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
	 * Create a matcher that matches a message logged at ERROR level
	 * 
	 * @param message
	 *            the message that should be matched
	 */
	public static Matcher<LoggingEvent> error(String message) {
		return new LoggingEventMatcher(Level.ERROR, message);
	}

	/**
	 * Create a matcher that matches the message and arguments at ERROR level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param arguments
	 *            the arguments that was logged to the message
	 */
	public static Matcher<LoggingEvent> error(String message, Object... arguments) {
		return new LoggingEventMatcher(Level.ERROR, message, arguments);
	}

	/**
	 * Create a matcher that matches the throwable and the accompanying message at ERROR level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param runtimeException
	 *            the throwable matcher used to match the logged throwable
	 */
	public static Matcher<LoggingEvent> error(String message, Matcher<Throwable> runtimeException) {
		return new LoggingEventMatcher(Level.ERROR, message, runtimeException);
	}

	/**
	 * Create a matcher that matches a message logged at WARN level
	 * 
	 * @param message
	 *            the message that should be matched
	 */
	public static Matcher<LoggingEvent> warn(String message) {
		return new LoggingEventMatcher(Level.WARN, message);
	}

	/**
	 * Create a matcher that matches the message and arguments at WARN level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param arguments
	 *            the arguments that was logged to the message
	 */
	public static Matcher<LoggingEvent> warn(String message, Object... arguments) {
		return new LoggingEventMatcher(Level.WARN, message, arguments);
	}

	/**
	 * Create a matcher that matches the throwable and the accompanying message at WARN level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param runtimeException
	 *            the throwable matcher used to match the logged throwable
	 */
	public static Matcher<LoggingEvent> warn(String message, Matcher<Throwable> runtimeException) {
		return new LoggingEventMatcher(Level.WARN, message, runtimeException);
	}

	/**
	 * Create a matcher that matches a message logged at INFO level
	 * 
	 * @param message
	 *            the message that should be matched
	 */
	public static Matcher<LoggingEvent> info(String message) {
		return new LoggingEventMatcher(Level.INFO, message);
	}

	/**
	 * Create a matcher that matches the message and arguments at INFO level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param arguments
	 *            the arguments that was logged to the message
	 */
	public static Matcher<LoggingEvent> info(String message, Object... arguments) {
		return new LoggingEventMatcher(Level.INFO, message, arguments);
	}

	/**
	 * Create a matcher that matches the throwable and the accompanying message at INFO level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param runtimeException
	 *            the throwable matcher used to match the logged throwable
	 */
	public static Matcher<LoggingEvent> info(String message, Matcher<Throwable> runtimeException) {
		return new LoggingEventMatcher(Level.INFO, message, runtimeException);
	}

	/**
	 * Create a matcher that matches a message logged at DEBUG level
	 * 
	 * @param message
	 *            the message that should be matched
	 */
	public static Matcher<LoggingEvent> debug(String message) {
		return new LoggingEventMatcher(Level.DEBUG, message);
	}

	/**
	 * Create a matcher that matches the message and arguments at DEBUG level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param arguments
	 *            the arguments that was logged to the message
	 */
	public static Matcher<LoggingEvent> debug(String message, Object... arguments) {
		return new LoggingEventMatcher(Level.DEBUG, message, arguments);
	}

	/**
	 * Create a matcher that matches the throwable and the accompanying message at DEBUG level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param runtimeException
	 *            the throwable matcher used to match the logged throwable
	 */
	public static Matcher<LoggingEvent> debug(String message, Matcher<Throwable> runtimeException) {
		return new LoggingEventMatcher(Level.DEBUG, message, runtimeException);
	}

	/**
	 * Create a matcher that matches a message logged at TRACE level
	 * 
	 * @param message
	 *            the message that should be matched
	 */
	public static Matcher<LoggingEvent> trace(String message) {
		return new LoggingEventMatcher(Level.TRACE, message);
	}

	/**
	 * Create a matcher that matches the message and arguments at TRACE level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param arguments
	 *            the arguments that was logged to the message
	 */
	public static Matcher<LoggingEvent> trace(String message, Object... arguments) {
		return new LoggingEventMatcher(Level.TRACE, message, arguments);
	}

	/**
	 * Create a matcher that matches the throwable and the accompanying message at TRACE level
	 * 
	 * @param message
	 *            the message that should be matched
	 * @param runtimeException
	 *            the throwable matcher used to match the logged throwable
	 */
	public static Matcher<LoggingEvent> trace(String message, Matcher<Throwable> runtimeException) {
		return new LoggingEventMatcher(Level.TRACE, message, runtimeException);
	}
}
