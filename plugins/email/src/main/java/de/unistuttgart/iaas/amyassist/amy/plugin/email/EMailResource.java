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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Method;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Parameter;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.ResourceEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.Types;

/**
 * Rest Resource for email
 * 
 * @author Muhammed Kaya
 */
@Path(EMailResource.PATH)
public class EMailResource implements Resource {

	/**
	 * the resource path for this plugin
	 */
	public static final String PATH = "email";

	@Reference
	private EMailLogic logic;

	@Context
	private UriInfo info;

	/**
	 * Returns number of new messages in inbox. New refers to messages that were received while the mailbox was not
	 * opened.
	 * 
	 * @return number of messages in inbox
	 */
	@GET
	@Path("new/count")
	@Produces(MediaType.TEXT_PLAIN)
	public int getNewMessageCount() {
		int count = this.logic.getNewMessageCount();
		if (count == -1) {
			throw new WebApplicationException("Initialize before accessing the inbox.", Status.UNAUTHORIZED);
		}
		return count;
	}

	/**
	 * Prints the plain text from all the mails in the inbox or only from important people
	 * 
	 * @param important
	 *            used to print mails only from important people: add /important to the path
	 * @param amount
	 *            the amount of messages that should be returned, -1 to have all messages returned default is 5
	 * @return most recent emails
	 */
	@POST
	@Path("plains{important : (/important)?}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String printPlainTextMessages(@PathParam("important") String important,
			@QueryParam("amount") @DefaultValue("5") int amount) {
		String plains;
		if (important.isEmpty()) {
			plains = this.logic.printPlainTextMessages(amount);
		} else {
			plains = this.logic.printImportantMessages(amount);
		}
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
	 * @param mail
	 *            the mail object with recipient, subject, message
	 * @return success string
	 */
	@POST
	@Path("new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String sendMail(MailEntity mail) {
		checkMail(mail);
		String response = this.logic.sendMail(mail.getRecipient(), mail.getSubject(), mail.getMessage());
		if (response.equals("Message could not be sent")) {
			throw new WebApplicationException("Message could not be sent.", Status.CONFLICT);
		}
		return response;
	}

	/**
	 * Checks if the given email belongs to an important person
	 * 
	 * @return if found, a List with important Mail Addresses
	 */
	@GET
	@Path("addresses")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getImportantMailAddresses() {
		List<String> addressesList = this.logic.getImportantMailAddresses();
		if (addressesList.isEmpty()) {
			throw new WebApplicationException("No important mail addresses were found.", Status.CONFLICT);
		}
		return addressesList;
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

	private boolean checkMail(MailEntity mail) {
		if (mail != null && mail.getRecipient() != null && mail.getSubject() != null && mail.getMessage() != null) {
			return true;
		}
		throw new WebApplicationException("Fill the missing fields.", Status.CONFLICT);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginDescripion()
	 */
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceEntity getPluginDescripion() {
		ResourceEntity resource = new ResourceEntity();
		resource.setName("EMail");
		resource.setDescription("Plugin for checking, showing and sending EMails");
		resource.setMethods(this.getPluginMethods());
		resource.setLink(this.info.getBaseUriBuilder().path(EMailResource.class).build());
		return resource;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.utility.rest.Resource#getPluginMethods()
	 */
	@Override
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method[] getPluginMethods() {
		Method[] methods = new Method[7];
		methods[0] = createGetNewMessageCountMethod();
		methods[1] = createPrintPlainTextMessagesMethod();
		methods[2] = createHasUnreadMessagesMethod();
		methods[3] = createSendMailMethod();
		methods[4] = createGetImportantMailAddressesMethod();
		methods[5] = createInitMethod();
		methods[6] = createCloseInboxMethod();
		return methods;
	}

	/**
	 * returns the method describing the getNewMessageCount method
	 * 
	 * @return the describing method object
	 */
	@Path("new/count")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetNewMessageCountMethod() {
		Method newCount = new Method();
		newCount.setName("Count new messages");
		newCount.setDescription("Returns number of new messages in inbox");
		newCount.setLink(this.info.getBaseUriBuilder().path(EMailResource.class)
				.path(EMailResource.class, "getNewMessageCount").build());
		newCount.setType(Types.GET);
		return newCount;
	}

	/**
	 * returns the method describing the printPlainTextMessages method
	 * 
	 * @return the describing method object
	 */
	@Path("plains{important : (/important)?}")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createPrintPlainTextMessagesMethod() {
		Method plains = new Method();
		plains.setName("Plain text");
		plains.setDescription("Returns the plain text of all messages or only from important people");
		plains.setLink(this.info.getBaseUriBuilder().path(EMailResource.class)
				.path(EMailResource.class, "printPlainTextMessages").build());
		plains.setType(Types.POST);
		plains.setParameters(getPrintPlainTextParameters());
		return plains;
	}

	/**
	 * returns the method describing the hasUnreadMessages method
	 * 
	 * @return the describing method object
	 */
	@Path("unread")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createHasUnreadMessagesMethod() {
		Method unread = new Method();
		unread.setName("Has unread messages");
		unread.setDescription("Indicates whether new messages are available or not");
		unread.setLink(this.info.getBaseUriBuilder().path(EMailResource.class)
				.path(EMailResource.class, "hasUnreadMessages").build());
		unread.setType(Types.GET);
		return unread;
	}

	/**
	 * returns the method describing the sendMail method
	 * 
	 * @return the describing method object
	 */
	@Path("new")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createSendMailMethod() {
		Method plains = new Method();
		plains.setName("Send new mail");
		plains.setDescription("Method to send a new mail");
		plains.setLink(
				this.info.getBaseUriBuilder().path(EMailResource.class).path(EMailResource.class, "sendMail").build());
		plains.setType(Types.POST);
		plains.setParameters(getMailAsParameter());
		return plains;
	}

	/**
	 * returns the method describing the getImportantMailAddresses method
	 * 
	 * @return the describing method object
	 */
	@Path("addresses")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createGetImportantMailAddressesMethod() {
		Method addresses = new Method();
		addresses.setName("Important mail addresses");
		addresses.setDescription("Returns the mail addresses of important people");
		addresses.setLink(this.info.getBaseUriBuilder().path(EMailResource.class)
				.path(EMailResource.class, "getImportantMailAddresses").build());
		addresses.setType(Types.GET);
		return addresses;
	}

	/**
	 * returns the method describing the init method
	 * 
	 * @return the describing method object
	 */
	@Path("init")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createInitMethod() {
		Method init = new Method();
		init.setName("Init");
		init.setDescription("Used to access the inbox of the email account");
		init.setLink(this.info.getBaseUriBuilder().path(EMailResource.class).path(EMailResource.class, "init").build());
		init.setType(Types.POST);
		return init;
	}

	/**
	 * returns the method describing the closeInbox method
	 * 
	 * @return the describing method object
	 */
	@Path("close")
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	public Method createCloseInboxMethod() {
		Method close = new Method();
		close.setName("Close inbox");
		close.setDescription("Method to send a new mail");
		close.setLink(this.info.getBaseUriBuilder().path(EMailResource.class).path(EMailResource.class, "closeInbox")
				.build());
		close.setType(Types.POST);
		return close;
	}

	private Parameter[] getPrintPlainTextParameters() {
		Parameter[] params = new Parameter[2];
		// important
		params[0] = new Parameter();
		params[0].setName("Important");
		params[0].setRequired(false);
		params[0].setParamType(Types.PATH);
		params[0].setValueType(Types.STRING);
		// amount
		params[1] = new Parameter();
		params[1].setName("Amount");
		params[1].setRequired(false);
		params[1].setParamType(Types.QUERY);
		params[1].setValueType(Types.INTEGER);
		return params;
	}

	private Parameter[] getMailAsParameter() {
		Parameter[] params = new Parameter[3];
		// recipient
		params[0] = new Parameter();
		params[0].setName("Recipient");
		params[0].setRequired(true);
		params[0].setParamType(Types.BODY);
		params[0].setValueType(Types.STRING);
		// subject
		params[1] = new Parameter();
		params[1].setName("Subject");
		params[1].setRequired(true);
		params[1].setParamType(Types.BODY);
		params[1].setValueType(Types.STRING);
		// message
		params[2] = new Parameter();
		params[2].setName("Message");
		params[2].setRequired(true);
		params[2].setParamType(Types.BODY);
		params[2].setValueType(Types.STRING);
		return params;
	}

}
