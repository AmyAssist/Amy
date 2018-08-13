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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher for throwables
 * 
 * @author Tim Neumann
 */
public class ThrowableMatcher extends TypeSafeMatcher<Throwable> {

	private Matcher<Class<? extends Throwable>> cls;
	private Matcher<String> msg;
	private Matcher<Throwable> cs;

	/**
	 * Creates a matcher that must match the kind of throwable, the message and the cause
	 * 
	 * @param kind
	 *            A matcher for the kind of throwable to match. If this is null it means that the kind does not matter.
	 * @param message
	 *            A matcher for the message to match. If this is null it means that the message does not matter.
	 * @param cause
	 *            A matcher for the causes to match. If this is null it means that the cause does not matter.
	 */
	public ThrowableMatcher(Matcher<Class<? extends Throwable>> kind, Matcher<String> message,
			Matcher<Throwable> cause) {
		this.cls = kind;
		this.msg = message;
		this.cs = cause;
	}

	/**
	 * Creates a matcher that must match the kind of throwable, and the message
	 * 
	 * @param kind
	 *            A matcher for the kind of throwable to match.
	 * @param message
	 *            A matcher for the message to match
	 */
	public ThrowableMatcher(Matcher<Class<? extends Throwable>> kind, Matcher<String> message) {
		this(kind, message, null);
	}

	/**
	 * Creates a matcher that must match the kind of throwable
	 * 
	 * @param kind
	 *            A matcher for the kind of throwable to match.
	 */
	public ThrowableMatcher(Matcher<Class<? extends Throwable>> kind) {
		this(kind, null, null);
	}

	/**
	 * Creates a matcher for any throwable
	 */
	public ThrowableMatcher() {
		this(null, null, null);
	}

	/**
	 * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
	 */
	@Override
	public void describeTo(Description description) {
		description.appendText("A Throwable");
		if (this.cls != null) {
			description.appendText(" which is ");
			this.cls.describeTo(description);
		}

		if (this.msg != null) {
			description.appendText(" with the message ");
			this.msg.describeTo(description);
		}

		if (this.cs != null) {
			description.appendText(" with the cause: ");
			this.cs.describeTo(description);
		}
	}

	/**
	 * @see org.hamcrest.TypeSafeMatcher#describeMismatchSafely(java.lang.Object, org.hamcrest.Description)
	 */
	@Override
	protected void describeMismatchSafely(Throwable item, Description mismatchDescription) {
		mismatchDescription.appendText("was a Throwable");
		if (this.cls != null && !this.cls.matches(item)) {
			mismatchDescription.appendText(" which is an instance of ");
			mismatchDescription.appendText(item.getClass().toString());
		}

		if (this.msg != null && !this.msg.matches(item.getMessage())) {
			mismatchDescription.appendText(" where the message ");
			this.msg.describeMismatch(item.getMessage(), mismatchDescription);
		}

		if (this.cs != null && !this.cs.matches(item.getCause())) {
			mismatchDescription.appendText(" where the cause ");
			this.cs.describeMismatch(item.getCause(), mismatchDescription);
		}
	}

	/**
	 * @see org.hamcrest.TypeSafeMatcher#matchesSafely(java.lang.Object)
	 */
	@Override
	protected boolean matchesSafely(Throwable item) {
		return ((this.cls == null || this.cls.matches(item))
				&& (this.msg == null || this.msg.matches(item.getMessage()))
				&& (this.cs == null || this.cs.matches(item.getCause())));
	}

}
