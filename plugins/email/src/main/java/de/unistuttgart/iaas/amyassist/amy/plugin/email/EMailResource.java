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

package de.unistuttgart.iaas.amyassist.amy.plugin.email;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Rest Resource for email
 * 
 * @author Muhammed Kaya
 */
@Path(EMailResource.PATH)
public class EMailResource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "email";

	@Reference
	private EMailLogic logic;

	/**
	 * Returns number of new messages in inbox. New refers to messages that were received while the mailbox was not
	 * opened.
	 * 
	 * @return number of messages in inbox
	 */
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public int getNewMessageCount() {
		int count = this.logic.getNewMessageCount();
		if (count == -1) {
			throw new WebApplicationException("Initialize before accessing the inbox.", Status.UNAUTHORIZED);
		}
		return count;
	}

	/**
	 * Prints the plain text from all the mails in the inbox
	 * 
	 * @param amount
	 *            the amount of emails that should be returned
	 * @return most recent emails
	 */
	@POST
	@Path("plains")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String printPlainTextMessages(@QueryParam("amount") @DefaultValue("5") int amount) {
		String plains = this.logic.printPlainTextMessages(amount);
		if (plains.equals("")) {
			throw new WebApplicationException("Could not fetch messages from inbox.", Status.CONFLICT);
		}
		return plains;
	}

	/**
	 * Looks for unread messages
	 * 
	 * @return true if unread messages have been found else false
	 */
	@GET
	@Path("unread")
	@Produces(MediaType.TEXT_PLAIN)
	public boolean hasUnreadMessages() {
		return this.logic.hasUnreadMessages();
	}
	
	/**
	 * Sends a message to some recipient
	 * 
	 * @param recipient
	 *            the recipient
	 * @param subject
	 *            mail subject
	 * @param message
	 *            the mail body
	 * @return success string
	 */
	@POST
	@Path("new")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String sendMail(@QueryParam("recipient") @DefaultValue("") String recipient,
			@QueryParam("subject") @DefaultValue("") String subject,
			@QueryParam("message") @DefaultValue("") String message) {
		String response = this.logic.sendMail(recipient, subject, message);
		if (response.equals("Message could not be sent")) {
			throw new WebApplicationException("Message could not be sent.", Status.CONFLICT);
		}
		return response;
	}

	/**
	 * Get credentials from file or registry
	 */
	@POST
	@Path("init")
	public void init() {
		this.logic.init();
	}

	/**
	 * Closes the opened inbox
	 */
	@POST
	@Path("close")
	public void closeInbox() {
		this.logic.closeInbox();
	}

}
