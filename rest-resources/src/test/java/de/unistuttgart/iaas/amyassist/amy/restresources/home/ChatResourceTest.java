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

package de.unistuttgart.iaas.amyassist.amy.restresources.home;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.IDialogHandler;
import de.unistuttgart.iaas.amyassist.amy.restresources.chat.ChatResource;
import de.unistuttgart.iaas.amyassist.amy.restresources.chat.ChatService;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Tests ChatResource class
 * 
 * @author Felix Burk
 */
@ExtendWith(FrameworkExtension.class)
public class ChatResourceTest {
	
	@Reference
	private TestFramework testFramework;
	
	private ChatService chatService;
	
	private IDialogHandler handler;
	
	private WebTarget target;
	
	private String uuidString;
	
	/**
	 * init necessary mocks
	 */
	@BeforeEach()
	public void init() {
		this.handler =this.testFramework.mockService(IDialogHandler.class);
		this.chatService = this.testFramework.mockService(ChatService.class);
		UUID uuid = UUID.randomUUID();
		this.uuidString = uuid.toString();
		when(this.handler.createDialog(ArgumentMatchers.any())).thenReturn(uuid);
		this.target = this.testFramework.setRESTResource(ChatResource.class);
	}
	
	/**
	 * tests the register user method
	 */
	@Test
	public void registerUser() {
		Response r = this.target.path("register").request().post(null);
		String s = r.readEntity(String.class);
		assertEquals(new Boolean(true), new Boolean(s.equals(this.uuidString)));
		assertEquals(200, r.getStatus());
		r.close();
	}
	
	/**
	 * tests natural language input from rest
	 */
	@Test
	public void testInput() {
		Response r = this.target.path("input").queryParam("langInput", "test")
				.queryParam("clientUUID", this.uuidString).request().post(null);
		assertEquals(204, r.getStatus());
	}
	
	/**
	 * tests correct response
	 */
	@Test
	public void testResponse() {
		Response r = this.target.path("input").queryParam("langInput", "test")
				.queryParam("clientUUID", this.uuidString).request().post(null);
		Response req = this.target.path("response").request().post(null);
		assertEquals(204, r.getStatus());
		assertEquals(500, req.getStatus());

	}
	

}
