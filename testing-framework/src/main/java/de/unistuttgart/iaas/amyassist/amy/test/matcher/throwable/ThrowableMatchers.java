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

import org.hamcrest.Matcher;

/**
 * A collection of matchers for exceptions.
 * 
 * @author Tim Neumann
 */
public class ThrowableMatchers {

	private ThrowableMatchers() {
	}

	/**
	 * Create a matcher that matches a throwable of the given kind or any subclass with the message and the cause.
	 * 
	 * @param kind
	 *            the type of throwable that matches. Subtypes of that also match.
	 * @param message
	 *            the message that a throwable must have to match
	 * @param cause
	 *            The cause that a throwable must have to match
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossed(Class<? extends Throwable> kind, String message, Matcher<Throwable> cause) {
		return new ThrowableMatcher(kind, message, cause, false);
	}

	/**
	 * Create a matcher that matches a throwable of exactly the given kind with the message and the cause.
	 * 
	 * @param kind
	 *            the type of throwable that matches.
	 * @param message
	 *            the message that a throwable must have to match
	 * @param cause
	 *            The cause that a throwable must have to match
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossedExactly(Class<? extends Throwable> kind, String message,
			Matcher<Throwable> cause) {
		return new ThrowableMatcher(kind, message, cause, true);
	}

	/**
	 * Create a matcher that matches a throwable of the given kind or any subclass with the message.
	 * 
	 * @param kind
	 *            the type of throwable that matches. Subtypes of that also match.
	 * @param message
	 *            the message that a throwable must have to match
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossed(Class<? extends Throwable> kind, String message) {
		return new ThrowableMatcher(kind, message, false);
	}

	/**
	 * Create a matcher that matches a throwable of exactly the given kind with the message.
	 * 
	 * @param kind
	 *            the type of throwable that matches.
	 * @param message
	 *            the message that a throwable must have to match
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossedExactly(Class<? extends Throwable> kind, String message) {
		return new ThrowableMatcher(kind, message, true);
	}

	/**
	 * Create a matcher that matches a throwable of the given kind or any subclass with the cause.
	 * 
	 * @param kind
	 *            the type of throwable that matches. Subtypes of that also match.
	 * @param cause
	 *            The cause that a throwable must have to match
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossed(Class<? extends Throwable> kind, Matcher<Throwable> cause) {
		return new ThrowableMatcher(kind, cause, false);
	}

	/**
	 * Create a matcher that matches a throwable of exactly the given kind with the cause.
	 * 
	 * @param kind
	 *            the type of throwable that matches.
	 * @param cause
	 *            The cause that a throwable must have to match
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossedExactly(Class<? extends Throwable> kind, Matcher<Throwable> cause) {
		return new ThrowableMatcher(kind, cause, true);
	}

	/**
	 * Create a matcher that matches a throwable of the given kind or any subclass.
	 * 
	 * @param kind
	 *            the type of throwable that matches. Subtypes of that also match.
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossed(Class<? extends Throwable> kind) {
		return new ThrowableMatcher(kind, false);
	}

	/**
	 * Create a matcher that matches a throwable of exactly the given kind.
	 * 
	 * @param kind
	 *            the type of throwable that matches.
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossedExactly(Class<? extends Throwable> kind) {
		return new ThrowableMatcher(kind, true);
	}
}
