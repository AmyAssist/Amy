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

package io.github.amyassist.amy.plugin.social;

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
	static final String[] greeting = {
			"Hi", "Hello there", "Good to see you"
	};
	
	/**
	 * whatsup constant
	 */
	static final String[] whatsUp = {
			"Not much", "Taking over the world", "Answering your questions", "Doing my duty.",
			"They're taking the Hobbits to Isengard!"
	};
	
	/**
	 * how are you constant
	 */
	static final String[] howAreYou = {
			"Fine, thanks", "I'm feeling pretty great!"
	};
	
	/**
	 * how are you constant
	 */
	static final String[] myNameIs = {
			"They call me Amy.", "My name is Amy, nice to meet you.", "Amy is my name. How can I assist you?"
	};
	
	/**
	 * fox says constant
	 */
	static final String[] foxSays = {
			"Ring-ding-ding-ding-dingeringeding!", "Wa-pa-pa-pa-pa-pa-pow!", "Hatee-hatee-hatee-ho!",
			"Joff-tchoff-tchoffo-tchoffo-tchoff!"
	};
	
	/**
	 * one does not simply constant
	 */
	static final String[] oneDoesNotSimply = {
			"...walk into Mordor.", "...pass Theo on the first try.", "...get the top grade on the project.",
			"...build a personal assistant system from scratch."
	};
}
