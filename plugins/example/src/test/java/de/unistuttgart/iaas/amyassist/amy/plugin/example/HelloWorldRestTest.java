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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.httpserver.Server;
import de.unistuttgart.iaas.amyassist.amy.plugin.example.api.HelloWorldService;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * A Test for the Hello World Rest Service
 * 
 * @author Leon Kiefer
 */
@ExtendWith(FrameworkExtension.class)
@Disabled
class HelloWorldRestTest {

	@Reference
	private TestFramework testFramework;

	private WebTarget target;

	@BeforeEach
	public void setUp() {
		this.testFramework.setRESTResource(HelloWorldRest.class);

		Client c = ClientBuilder.newClient();
		this.target = c.target(Server.BASE_URI);
	}

	/**
	 * test to see the message "Hello World" is sent in response
	 */
	@Test
	public void test() {
		HelloWorldService mockService = this.testFramework.mockService(HelloWorldService.class);
		Mockito.when(mockService.helloWorld()).thenReturn("hello100");

		String responseMsg = this.target.path("helloworld").request().get(String.class);

		assertThat(responseMsg, equalTo("hello100"));
	}

}
