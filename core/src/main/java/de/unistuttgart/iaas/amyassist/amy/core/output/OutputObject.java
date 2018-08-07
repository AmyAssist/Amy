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

package de.unistuttgart.iaas.amyassist.amy.core.output;

/**
 * Object Time the Output Interface Uses
 * 
 * @author Kai Menzel
 */
public class OutputObject {

	private OutputType type;
	private String message;

	/**
	 * @param type
	 *            of the output-message
	 * @param message
	 *            to say or path to sound
	 * 
	 */
	public OutputObject(OutputType type, String message) {
		this.type = type;
		this.message = message;
	}

	/**
	 * return Type of OutputObject
	 * 
	 * @return OutputType
	 */
	public OutputType getType() {
		return this.type;
	}

	/**
	 * return Path to Sound file or voice Message
	 * 
	 * @return Path to Sound file or message to voice
	 */
	public String getMessage() {
		return this.message;
	}

}
