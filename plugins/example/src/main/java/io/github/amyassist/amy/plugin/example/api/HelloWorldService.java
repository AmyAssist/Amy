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

package io.github.amyassist.amy.plugin.example.api;

/**
 * The api provided by the HelloWorldService
 * 
 * @author Leon Kiefer
 */
public interface HelloWorldService {

	/**
	 * The method that does all the logic.
	 * 
	 * @return the response
	 */
	String helloWorld();

	/**
	 * Create a String with the given amount of <i>helloword</i> in it.
	 * 
	 * @param times
	 *            the amount how often the string <i>helloword</i> should be repeated
	 * @return the created String
	 */
	String helloWorldXTimes(int times);

	/**
	 * Demonstrate the registry's functionality
	 * 
	 * @return human-readable text
	 */
	String demonstrateContactRegistry();

	/**
	 * Test the registry for correctness
	 * 
	 * @return human-readable text
	 */
	String testContactRegistry();

	/**
	 * Test the location registry
	 * 
	 * @return human-readable text
	 */
	String testLocationRegistry();

	/**
	 * Test custom registry
	 * 
	 * @return human-readable text
	 */
	String testCustomRegistry();
}
