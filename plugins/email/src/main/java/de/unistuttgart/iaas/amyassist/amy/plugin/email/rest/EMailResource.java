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

package de.unistuttgart.iaas.amyassist.amy.plugin.email.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.EMailLogic;

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
	 * Get the currently saved mail credentials
	 * 
	 * @return mail credentials
	 */
	@GET
	@Path("getCredentials")
	@Produces(MediaType.APPLICATION_JSON)
	public EMailCredentials getCredentials() {
		return this.logic.getCredentials();
	}

	/**
	 * Connect to a mail service with the given parameters
	 * 
	 * @param credentials
	 *            the email credentials
	 * 
	 * @return true if connecting was successful, else false
	 */
	@POST
	@Path("connect")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean connect(EMailCredentials credentials) {
		return this.logic.connectToMailServer(credentials);
	}

	/**
	 * REST implementation of {@link EMailLogic#getMailsForREST(int)}
	 * 
	 * @param amount
	 *            the amount of mails, put -1 here if you want all mails
	 * 
	 * @return Array of all mails in inbox
	 */
	@GET
	@Path("getMails/{amount}")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageDTO[] getMails(@PathParam("amount") int amount) {
		return this.logic.getMailsForREST(amount);
	}

	/**
	 * Disconnect the currently running connection to the mail server
	 */
	@POST
	@Path("disconnect")
	public void disconnect() {
		this.logic.disconnectFromMailServer();
	}
}
