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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;

/**
 * Custom Matcher for LoggingEvents
 * 
 * @author Leon Kiefer
 */
public class LoggingEventMatcher extends TypeSafeMatcher<LoggingEvent> {
	@Nullable
	private final String message;
	@Nonnull
	private final Level level;
	@CheckForNull
	private Matcher<Throwable> throwableMatcher;
	@CheckForNull
	private Matcher<Iterable<? extends Object>> matchers;

	/**
	 * @param level
	 *            the log level
	 * @param message
	 *            the accompanying message
	 */
	public LoggingEventMatcher(@Nonnull Level level, @Nullable String message) {
		this.level = level;
		this.message = message;
	}

	/**
	 * @param level
	 *            the log level
	 * @param message
	 *            the accompanying message
	 * @param throwableMatcher
	 *            a matcher for the throwable of the LoggingEvent
	 */
	public LoggingEventMatcher(@Nonnull Level level, @Nullable String message,
			@Nullable Matcher<Throwable> throwableMatcher) {
		this.level = level;
		this.message = message;
		this.throwableMatcher = throwableMatcher;
	}

	/**
	 * @param level
	 *            the log level
	 * @param message
	 *            the accompanying message
	 * @param arguments
	 *            the arguments
	 */
	public LoggingEventMatcher(@Nonnull Level level, @Nullable String message, Object... arguments) {
		this.level = level;
		this.message = message;
		this.matchers = Matchers.contains(arguments);
	}

	/**
	 * @param level
	 *            the log level
	 * @param message
	 *            the accompanying message
	 * @param matchers
	 *            the arguments
	 */
	public LoggingEventMatcher(@Nonnull Level level, @Nullable String message, Matcher<Object>... matchers) {
		this.level = level;
		this.message = message;
		this.matchers = Matchers.contains(matchers);

	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(this.level);
		description.appendText(" LoggingEvent ");

		description.appendText("with message ");
		description.appendValue(this.message);
		if (this.matchers != null) {
			description.appendText(" and arguments ");
			description.appendDescriptionOf(this.matchers);
		}

		if (this.throwableMatcher != null) {
			description.appendText(" where the throwable is ");
			description.appendDescriptionOf(this.throwableMatcher);
		}
	}

	@Override
	protected boolean matchesSafely(LoggingEvent loggingEvent) {

		if (!this.matchesThrowable(loggingEvent))
			return false;

		if (!this.matchesArgumentMatcher(loggingEvent))
			return false;

		return loggingEvent.getLevel().equals(this.level) && loggingEvent.getMessage().equals(this.message);
	}

	private boolean matchesThrowable(LoggingEvent loggingEvent) {
		if (this.throwableMatcher == null)
			return true;

		return this.throwableMatcher.matches(loggingEvent.getThrowable().orNull());
	}

	private boolean matchesArgumentMatcher(LoggingEvent loggingEvent) {
		if (this.matchers == null)
			return true;

		return this.matchers.matches(loggingEvent.getArguments());
	}

}
