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

import java.util.Random;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Logic for this social plugin
 * 
 * @author Felix Burk
 */
@Service
public class SocialLogic {

	/**
	 * receive a random greeting
	 * @return the greeting string
	 */
	protected String getGreeting() {
		return generateRandomAnswer(SocialConstants.greeting);
	}
	
	/**
	 * receive a random string answer
	 * @return answer
	 */
	protected String getWhatsUp() {
		return generateRandomAnswer(SocialConstants.whatsUp);
	}
	
	protected String getHowAreYou() {
		return generateRandomAnswer(SocialConstants.howAreYou);
	}
	
	/*
	 * generates a random answer from a string array
	 */
	private String generateRandomAnswer(String[] strings) {
		Random rand = new Random();
		int rndm = rand.nextInt(strings.length);
		return strings[rndm];
	}
}
