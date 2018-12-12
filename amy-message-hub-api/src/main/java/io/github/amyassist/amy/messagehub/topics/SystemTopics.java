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

package io.github.amyassist.amy.messagehub.topics;

/**
 * All system (top level) topics used.
 * 
 * @author Tim Neumann
 */
public class SystemTopics {
	/** The system topic */
	public static final String SYSTEM = "system";
	/** The smarthome topic */
	public static final String SMARTHOME = "smarthome";
	/** The user topic */
	public static final String USER = "user";
	/** The info topic */
	public static final String INFO = "info";

	private SystemTopics() {
		// hide constructor
	}
}
