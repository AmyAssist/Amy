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

	private boolean matchClsStrict;
	private boolean matchMessage;
	private boolean matchCause;

	private Class<? extends Throwable> cls;
	private String msg;
	private Matcher<Throwable> cause;

	/**
	 * Creates a matcher that must match the kind of throwable, the message and the cause
	 * 
	 * @param kind
	 *            The kind of throwable to match.
	 * @param message
	 *            The message to match
	 * @param causeMatcher
	 *            A matcher for the causes to match
	 * @param matchKindStrict
	 *            Whether to only the exact given type(true) or to allow subclasses(false).
	 */
	public ThrowableMatcher(Class<? extends Throwable> kind, String message, Matcher<Throwable> causeMatcher,
			boolean matchKindStrict) {
		this.matchMessage = true;
		this.matchCause = true;
		this.cls = kind;
		this.msg = message;
		this.cause = causeMatcher;
		this.matchClsStrict = matchKindStrict;
	}

	/**
	 * Creates a matcher that must match the kind of throwable, and the message
	 * 
	 * @param kind
	 *            The kind of throwable to match.
	 * 
	 * @param message
	 *            The message to match
	 * @param matchKindStrict
	 *            Whether the kind need to be the exact given class(true) or to allow subclasses(false).
	 */
	public ThrowableMatcher(Class<? extends Throwable> kind, String message, boolean matchKindStrict) {
		this(kind, message, null, matchKindStrict);
		this.matchCause = false;
	}

	/**
	 * Creates a matcher that must match the kind of throwable, and the cause
	 * 
	 * @param kind
	 *            The kind of throwable to match.
	 * @param causeMatcher
	 *            A matcher for the causes to match
	 * @param matchKindStrict
	 *            Whether the kind need to be the exact given class(true) or to allow subclasses(false).
	 */
	public ThrowableMatcher(Class<? extends Throwable> kind, Matcher<Throwable> causeMatcher, boolean matchKindStrict) {
		this(kind, null, causeMatcher, matchKindStrict);
		this.matchMessage = false;
	}

	/**
	 * Creates a matcher that must match the kind of throwable
	 * 
	 * @param kind
	 *            The kind of throwable to match.
	 * @param matchKindStrict
	 *            Whether the kind need to be the exact given class(true) or to allow subclasses(false).
	 */
	public ThrowableMatcher(Class<? extends Throwable> kind, boolean matchKindStrict) {
		this(kind, null, null, matchKindStrict);
		this.matchMessage = false;
		this.matchCause = false;
	}

	/**
	 * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
	 */
	@Override
	public void describeTo(Description description) {
		description.appendText("Throwable of type ");
		description.appendText(this.cls.getName());
		if (!this.matchClsStrict) {
			description.appendText(" or a subtype");
		}

		if (this.matchMessage) {
			description.appendText(" with the message \"");
			description.appendText(this.msg);
			description.appendText("\"");
		}
		if (this.matchCause) {
			if (this.matchMessage) {
				description.appendText(" and a cause that is a ");
			} else {
				description.appendText(" with a cause that is a ");
			}
			description.appendDescriptionOf(this.cause);
		}
		description.appendText(".");
	}

	/**
	 * @see org.hamcrest.TypeSafeMatcher#matchesSafely(java.lang.Object)
	 */
	@Override
	protected boolean matchesSafely(Throwable item) {
		if (this.matchClsStrict) {
			if (!this.cls.equals(item.getClass()))
				return false;
		} else {
			if (!this.cls.isAssignableFrom(item.getClass()))
				return false;
		}

		if (this.matchMessage && !this.msg.equals(item.getMessage()))
			return false;

		if (this.matchCause && !this.cause.matches(item.getCause()))
			return false;

		return true;
	}

}
