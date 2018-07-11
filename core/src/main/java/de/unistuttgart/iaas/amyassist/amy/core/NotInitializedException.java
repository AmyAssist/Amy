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

package de.unistuttgart.iaas.amyassist.amy.core;

/**
 * A runtime exception, that should be thrown if a class is in an illegal state for the current method call, because the
 * class was not initialized before.
 * 
 * @author Tim Neumann
 */
public class NotInitializedException extends IllegalStateException {
	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 6911623681387204728L;

	/**
	 * Constructs an NotInitializedException with no detail message. A detail message is a String that describes this
	 * particular exception.
	 */
	public NotInitializedException() {
		super();
	}

	/**
	 * Constructs an NotInitializedException with the specified detail message. A detail message is a String that
	 * describes this particular exception.
	 *
	 * @param s
	 *            the String that contains a detailed message
	 */
	public NotInitializedException(String s) {
		super(s);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 *
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this
	 * exception's detail message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
	 *            <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @since 1.5
	 */
	public NotInitializedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of
	 * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail message of
	 * <tt>cause</tt>). This constructor is useful for exceptions that are little more than wrappers for other
	 * throwables (for example, {@link java.security.PrivilegedActionException}).
	 *
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
	 *            <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @since 1.5
	 */
	public NotInitializedException(Throwable cause) {
		super(cause);
	}
}
