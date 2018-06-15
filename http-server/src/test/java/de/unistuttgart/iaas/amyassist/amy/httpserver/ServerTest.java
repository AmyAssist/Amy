/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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

package de.unistuttgart.iaas.amyassist.amy.httpserver;

import static java.time.Duration.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.core.di.DependencyInjection;

/**
 * 
 * @author Leon Kiefer
 */
class ServerTest {

	private DependencyInjection dependencyInjection;

	@BeforeEach
	void setup() {
		this.dependencyInjection = new DependencyInjection();
	}

	@Test
	void test() {
		assertTimeout(ofSeconds(2), () -> {
			Server server = this.dependencyInjection.create(Server.class);
			server.start(TestRestResource.class);
			server.shutdown();
		}, "The Server start and shotdown takes longer then 2 Seconds");

	}

	@Test
	void testCantStartServerTwice() {
		Server server = this.dependencyInjection.create(Server.class);
		String message = assertThrows(IllegalStateException.class, () -> {
			server.start(TestRestResource.class);
			server.start(TestRestResource.class);
		}, "The Server dont throw an IllegalStateException if its started twice").getMessage();
		server.shutdown();
		assertThat(message, equalTo("The Server is already started"));
	}

	@Test
	void testRegister() {
		Server server = this.dependencyInjection.create(Server.class);
		server.register(TestRestResource.class);
		server.start();
		server.shutdown();
	}

	@Test
	void testRegisterNonResourceClass() {
		Server server = this.dependencyInjection.create(Server.class);
		assertThrows(IllegalArgumentException.class, () -> {
			server.register(ServerTest.class);
		}, "The Server dont throw an IllegalArgumentException if a registered class is not a Rest Resource");
	}
}
