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

package io.github.amyassist.amy.core.di.util;

/**
 * Exception that indicate an injection error
 * 
 * @author Leon Kiefer
 */
public class InjectionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 253410991980293413L;
	private final String object;
	private final String instance;

	/**
	 * @param object
	 *            the object that should be injected
	 * @param instance
	 *            the instance in which to inject
	 * @param throwable
	 *            the cause to this exception
	 */
	public InjectionException(Object object, Object instance, Throwable throwable) {
		super(throwable);
		this.object = object.toString();
		this.instance = instance.toString();
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "Tryed to inject the object " + this.object + " into " + this.instance + " but failed";
	}
}
