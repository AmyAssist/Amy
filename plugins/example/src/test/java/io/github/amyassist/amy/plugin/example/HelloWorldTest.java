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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import io.github.amyassist.amy.plugin.example.registry.ColorRegistry;
import io.github.amyassist.amy.registry.ContactRegistry;
import io.github.amyassist.amy.registry.LocationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.plugin.api.IStorage;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * A Test for the Hello World Plugin
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
public class HelloWorldTest {
	@Reference
	private TestFramework testFramework;

	private HelloWorldImpl helloWorld;

	@BeforeEach
	public void setup() {
		this.testFramework.mockService(ContactRegistry.class);
		this.testFramework.mockService(LocationRegistry.class);
		this.testFramework.mockService(ColorRegistry.class);
		this.helloWorld = this.testFramework.setServiceUnderTest(HelloWorldImpl.class);
	}

	@Test
	public void testInit() {
		IStorage storage = this.testFramework.storage();

		assertThat(this.helloWorld.helloWorld(), equalTo("hello 1"));

		Mockito.verify(storage).put("hellocount", "1");
	}

	@Test
	public void testcount() {
		IStorage storage = this.testFramework.storage();
		storage.put("hellocount", "10");

		assertThat(this.helloWorld.helloWorld(), equalTo("hello 11"));

		Mockito.verify(storage).put("hellocount", "11");
	}

	@Test
	public void textHelloWorldXTimes() {
		assertThat(this.helloWorld.helloWorldXTimes(3), equalTo("hello hello hello"));
	}
}
