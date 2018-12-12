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

package io.github.amyassist.amy.core.configuration;

/**
 * Exception that is thrown if a specific configuration was not found but must be loaded.
 * 
 * @author Leon Kiefer
 */
public class ConfigurationNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6884409079204932996L;

	/**
	 * Create a new exception with a message.
	 * 
	 * @param message
	 *            the string message
	 */
	public ConfigurationNotFoundException(String message) {
		super(message);
	}

}
