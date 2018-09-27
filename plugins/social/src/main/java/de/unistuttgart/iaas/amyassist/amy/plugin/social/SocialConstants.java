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

package de.unistuttgart.iaas.amyassist.amy.plugin.social;

/**
 * Constant answers 
 * @author Felix Burk, Florian Bauer
 */
class SocialConstants {

	
	private SocialConstants() {
		//hide constructor
	}
	
	/**
	 * greeting constant
	 */
	protected static final String[] greeting = {
			"Hi", "Hello there", "Good to see you"
	};
	
	/**
	 * whatsup constant
	 */
	protected static final String[] whatsUp = {
			"Not much", "Taking over the world", "Answering your questions", "Doing my duty."
	};
	
	/**
	 * how are you constant
	 */
	protected static final String[] howAreYou = {
			"Fine, thanks", "I'm feeling pretty great!"
	};
	
	/**
	 * how are you constant
	 */
	protected static final String[] myNameIs = {
			"They call me Amy.", "My name is Amy, nice to meet you.", "Amy is my name. How can I assist you?"
	};
	
	/**
	 * fox says constant
	 */
	protected static final String[] foxSays = {
			"Ring-ding-ding-ding-dingeringeding!", "Wa-pa-pa-pa-pa-pa-pow!", "Hatee-hatee-hatee-ho!",
			"Joff-tchoff-tchoffo-tchoffo-tchoff!"
	};
}
