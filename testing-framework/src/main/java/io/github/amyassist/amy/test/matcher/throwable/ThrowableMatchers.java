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
	 * Create a matcher that matches a throwable of the given kind with the message and the cause.
	 * 
	 * @param kind
	 *            A matcher for the type of throwable. If this is null it means that the kind does not matter.
	 * @param message
	 *            A matcher for the the message. If this is null it means that the message does not matter.
	 * @param cause
	 *            A matcher for the cause. If this is null it means that the cause does not matter.
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossed(Matcher<Class<? extends Throwable>> kind, Matcher<String> message,
			Matcher<Throwable> cause) {
		return new ThrowableMatcher(kind, message, cause);
	}

	/**
	 * Create a matcher that matches a throwable of the given kind with the message and the cause.
	 * 
	 * @param kind
	 *            A matcher for the type of throwable.
	 * @param message
	 *            A matcher for the the message.
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossed(Matcher<Class<? extends Throwable>> kind, Matcher<String> message) {
		return new ThrowableMatcher(kind, message);
	}

	/**
	 * Create a matcher that matches a throwable of the given kind with the message and the cause.
	 * 
	 * @param kind
	 *            A matcher for the type of throwable.
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossed(Matcher<Class<? extends Throwable>> kind) {
		return new ThrowableMatcher(kind);
	}

	/**
	 * Create a matcher that matches a throwable of the given kind with the message and the cause.
	 * 
	 * @param kind
	 *            A matcher for the type of throwable.
	 * @param cause
	 *            A matcher for the cause.
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossedWithCause(Matcher<Class<? extends Throwable>> kind,
			Matcher<Throwable> cause) {
		return new ThrowableMatcher(kind, null, cause);
	}

	/**
	 * Create a matcher that matches a throwable of the given kind with the message and the cause.
	 * 
	 * @return The matcher
	 */
	public static Matcher<Throwable> tossedAny() {
		return new ThrowableMatcher();
	}
}
