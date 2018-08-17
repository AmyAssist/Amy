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

package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;

/**
 * A example plugin
 * 
 * @author Leon Kiefer, Tim Neumann, Benno Krauß
 */
@SpeechCommand
public class HelloWorldSpeech {
	
	/**
	 * The logic class of this plugin.
	 */
	@Reference
	private HelloWorldService helloWorld;

	/**
	 * A method that says hello
	 * 
	 * @param params
	 *            [Not used] The parameters of the sentence.
	 * @return The response of the system
	 */
	@Intent("say")
	public String say(String... params) {
		return this.helloWorld.helloWorld();
	}

	@Intent("sayHelloXTimes")
	public String sayHelloXTimes(String... params) {
		return "done";
	}

	@Intent("getContacts")
	public String getContacts(String... params) {
		return this.helloWorld.demonstrateContactRegistry();
	}

	@Intent("testContacts")
	public String testContacts(String... params) {
		return this.helloWorld.testContactRegistry();
	}

	@Intent("testLocation")
	public String testLocation(String... params) {
		return this.helloWorld.testLocationRegistry();
	}

	@Intent("testCustom")
	public String testCustom(String... params) {
		return this.helloWorld.testCustomRegistry();
	}
}
