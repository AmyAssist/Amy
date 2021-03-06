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

package io.github.amyassist.amy.plugin.example;

import java.util.Map;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.natlang.EntityData;
import io.github.amyassist.amy.core.natlang.Intent;
import io.github.amyassist.amy.core.natlang.SpeechCommand;
import io.github.amyassist.amy.plugin.example.api.HelloWorldService;

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
	 * @param entities
	 *            [Not used] The parameters of the sentence.
	 * @return The response of the system
	 */
	@Intent
	public String say(Map<String, EntityData> entities) {
		return this.helloWorld.helloWorld();
	}
	
	@Intent
	public String repeat(Map<String, EntityData> entities) {
		StringBuilder b = new StringBuilder();
		for(int i=0; i < entities.get("int").getNumber(); i++) {
			b.append(entities.get("somestring").getString() + " ");
		}
		return b.toString();
	}

	@Intent
	public String getContacts(Map<String, EntityData> entities) {
		return this.helloWorld.demonstrateContactRegistry();
	}

	@Intent
	public String testContacts(Map<String, EntityData> entities) {
		return this.helloWorld.testContactRegistry();
	}

	@Intent
	public String testLocation(Map<String, EntityData> entities) {
		return this.helloWorld.testLocationRegistry();
	}

	@Intent
	public String testCustom(Map<String, EntityData> entities) {
		return this.helloWorld.testCustomRegistry();
	}
}
