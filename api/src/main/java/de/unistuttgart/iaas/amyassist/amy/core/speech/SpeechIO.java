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

package de.unistuttgart.iaas.amyassist.amy.core.speech;

/**
 * A interface that defines an Speech Input Output component. The component is executed in its own Thread and must use
 * the SpeechInputHandler to pass the user speech input to the application.
 * 
 * @author Leon Kiefer
 */
public interface SpeechIO extends Runnable {

	/**
	 * Setter for the SpeechInputHandler. The SpeechInputHandler is used to process the user input.
	 * 
	 * @param handler
	 *            the SpeechInputHandler to use
	 */
	void setSpeechInputHandler(SpeechInputHandler handler);
}
