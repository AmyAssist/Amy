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

package de.unistuttgart.iaas.amyassist.amy.restresources.chat;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * The home resource of Amy
 * 
 * @author Christian Bräuner, Felix Burk
 */
@Path(ChatResource.PATH)
public class ChatResource {

	/**
	 * the Path of this resource
	 */
	public static final String PATH = "chat";
	
	@Reference
	private DialogHandler handler;
	
	@Reference
	private ChatService chatService;

	/**
	 * registers a new conversation
	 *
	 * @return generated uuid
	 */
	@POST
	@Path("register")
	@Produces(MediaType.TEXT_PLAIN)
	public String registerUser() {
		AnswerConsumer w = new AnswerConsumer();
		UUID uuid = this.handler.createDialog(w::addToQueue);
		this.chatService.addUser(uuid);
		w.setQueue(this.chatService.getQueue(uuid));
		return uuid.toString();
	}

	/**
	 * handles chat input from a client
	 * 
	 * @param input
	 *            the input from the client
	 * @param uuid
	 *            of the user
	 */
	@POST
	@Path("input")
	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Process natural language text input",
			description = "This is the remote amy chatbot."
					+ " It can be used to interact with the natural language interface of amy."
					+ " The possible intents are the same as using the local console or the speech interaction",
			tags = "chat")
	@ApiResponse(responseCode = "200",
			description = "the natural language text input has been processed successfully and the response contains the answer",
			content = @Content(examples = @ExampleObject(
					summary = "This response represent that you don't have new mails.", name = "new mails")))
	@ApiResponse(responseCode = "500", description = "the input could not be precessed")
	public void useAmy(@QueryParam("langInput") String input, @QueryParam("clientUUID") String uuid) {
		try {
			this.handler.process(input, UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException("can't handle input: " + input, e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * receive queued responses
	 * 
	 * @param uuid
	 *            of user
	 * @return current response
	 */
	@POST
	@Path("response")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String receiveResponse(String uuidString) {
		try {
			UUID uuid = UUID.fromString(uuidString);
			if (this.chatService.getQueue(uuid) != null) {
				String s = this.chatService.getQueue(uuid).poll();
				if (s == null) {
					return "";
				}
				return s;
			}
			return "";

		} catch (IllegalArgumentException e) {
			throw new WebApplicationException("can't handle input: " + uuidString, e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
