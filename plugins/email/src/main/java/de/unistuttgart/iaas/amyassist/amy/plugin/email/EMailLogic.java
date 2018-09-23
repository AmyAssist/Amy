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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.EMailCredentials;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MessageDTO;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.session.MailSession;
import de.unistuttgart.iaas.amyassist.amy.registry.Contact;
import de.unistuttgart.iaas.amyassist.amy.registry.ContactRegistry;

/**
 * Logic class for the email functionality, that defines all the behavior
 * 
 * @author Patrick Singer, Felix Burk, Patrick Gebhardt
 */
@Service
public class EMailLogic {

	@Reference
	private MailSession mailSession;

	@Reference
	private MailUpdateService updateService;

	@Reference
	private ContactRegistry contactRegistry;

	@Reference
	private Properties configLoader;

	@Reference
	private IStorage storage;

	@Reference
	private Logger logger;

	/**
	 * Key for the Amy mail address in the config - declared protected for tests
	 */
	protected static final String AMY_MAIL_ADDRESS_KEY = "email_usr";

	/**
	 * Key for the Amy mail password in the config - declared protected for tests
	 */
	protected static final String AMY_MAIL_PW_KEY = "email_pw";

	private static final String AMY_MAIL_HOST = "imap.gmail.com";

	private static final String USERNAME_CRED_KEY = "usernameCred";

	private static final String PASSOWRD_CRED_KEY = "passwordCred";

	private static final String IMAPSERVER_CRED_KEY = "imapServerCred";

	/**
	 * returns if unread messages have been found
	 * 
	 * @param checkForImportant
	 *            set this to true if you want to look for IMPORTANT new mails, set this to false if you only want to
	 *            look for new mails
	 * 
	 * @return true, if messages have been found, else false
	 */
	public boolean hasNewMessages(boolean checkForImportant) {
		if (checkForImportant) {
			return !getNewImportantMessages().isEmpty();
		}
		return !getNewMessages().isEmpty();
	}

	/**
	 * Returns number of new messages in inbox. The word "new" here refers to unread messages
	 * 
	 * @param checkForImportant
	 *            put true here if you want the amount of new important mails
	 * 
	 * @return number of messages in inbox
	 */
	public int getNewMessageCount(boolean checkForImportant) {
		if (checkForImportant) {
			return getNewImportantMessages().size();
		}
		return getNewMessages().size();
	}

	/**
	 * Converts all new messages to a readable string
	 * 
	 * @param amount
	 *            amount of mails to print, put -1 here if you want every mail to be printed
	 * 
	 * @param important
	 *            set this to true, if you only want the important mails to be converted, set this to false if you want
	 *            every message to be converted
	 * @return readable String of new messages
	 */
	public String printMessages(int amount, boolean important) {
		List<Message> messagesToPrint;
		StringBuilder sb = new StringBuilder();
		if (important) {
			messagesToPrint = getNewImportantMessages();
		} else {
			messagesToPrint = getNewMessages();
		}
		int amountToPrint;
		if (amount > messagesToPrint.size() || amount == -1) {
			amountToPrint = messagesToPrint.size();
		} else {
			amountToPrint = amount;
		}

		try {
			for (int i = 0; i < amountToPrint; i++) {
				Message m = messagesToPrint.get(i);
				sb.append(getFrom(m) + "\n");
				sb.append(m.getSubject() + "\n");
			}
		} catch (MessagingException me) {
			this.logger.error("Operations on the message failed", me);
		}
		return sb.toString();
	}

	// ===========================================================================================================
	// methods called only by logic class

	/**
	 * Get all unread messages from the inbox
	 * 
	 * @return all unread messages in inbox, from newest to oldest
	 */
	List<Message> getNewMessages() {
		try {
			List<Message> messages = Arrays
					.asList(this.mailSession.getInbox().search(new FlagTerm(new Flags(Flag.SEEN), false)));
			Collections.reverse(messages);
			return messages;
		} catch (MessagingException me) {
			this.logger.error("Searching for mails failed", me);
			return Collections.emptyList();
		}
	}

	/**
	 * Get all important messages from given message array
	 * 
	 * @return all important messages in given message array
	 */
	List<Message> getNewImportantMessages() {
		List<Message> unseenMessages = getNewMessages();
		List<Message> importantMessages = new ArrayList<>();
		try {
			for (Message m : unseenMessages) {
				if (isImportantMessage(m)) {
					importantMessages.add(m);
				}
			}
			return importantMessages;
		} catch (MessagingException e) {
			this.logger.error("Couldn't determine whether the message is important", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Get all mails in the inbox and convert them to MessageDTO objects for the REST class to send to the web app
	 * 
	 * @param amount
	 *            the amount of mails, put -1 here if you want all mails
	 * 
	 * @return array with the requested mails, from newest to oldest, empty array if there was an error
	 */
	public MessageDTO[] getMailsForREST(int amount) {
		Message[] messages;
		MessageDTO[] messagesToSend;
		try (Folder inbox = this.mailSession.getInbox()) {
			int amountInInbox = inbox.getMessageCount();
			int lowerIndex = (amount == -1) ? 1 : Math.max(amountInInbox - amount, 1);
			messages = inbox.getMessages(lowerIndex, amountInInbox);
			messagesToSend = new MessageDTO[messages.length];

			for (int i = 0; i < messages.length; i++) {
				// we switch order because we get the messages from the inbox from oldest to newest
				Message m = messages[messages.length - 1 - i];
				LocalDateTime sentDate = m.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				messagesToSend[i] = new MessageDTO(getFrom(m), m.getSubject(), sentDate, isImportantMessage(m),
						m.isSet(Flag.SEEN));
			}
			return messagesToSend;
		} catch (MessagingException e) {
			this.logger.error("There were problems handling the messages", e);
			return new MessageDTO[0];
		}
	}

	/**
	 * Get all important mail addresses from the registry
	 * 
	 * @return List of important mail addresses saved in the registry
	 */
	Set<String> getImportantMailAddresses() {
		Set<String> importantAddresses = new HashSet<>();
		List<Contact> contacts = this.contactRegistry.getAll();
		for (Contact c : contacts) {
			if (c.isImportant())
				importantAddresses.add(c.getEmail());
		}
		return importantAddresses;
	}

	/**
	 * Check if a message was sent from an important person
	 * 
	 * @param message
	 *            the message to be checked
	 * @return true if message sender is important, else false
	 * @throws MessagingException
	 *             if getting the address of the sender failed
	 */
	boolean isImportantMessage(Message message) throws MessagingException {
		Set<String> importantAddresses = getImportantMailAddresses();
		// we can't use getFrom() because it may return a personal name, but a mail address is needed here
		InternetAddress address = (InternetAddress) message.getFrom()[0];
		return importantAddresses.contains(address.getAddress());
	}

	/**
	 * Set up connection to mail server with given parameters. Put empty strings into all parameters if you want to
	 * connect with the Amy mail account
	 * 
	 * @param credentials
	 *            the email credentials
	 * 
	 * @return true if connecting was successful, else false
	 */
	public boolean connectToMailServer(EMailCredentials credentials) {
		if (credentials == null) {
			// if no credentials are given, use the standard ones for Amy
			String amyUsername = this.configLoader.getProperty(AMY_MAIL_ADDRESS_KEY);
			String amyPassword = this.configLoader.getProperty(AMY_MAIL_PW_KEY);
			String amyHost = AMY_MAIL_HOST;

			credentials = new EMailCredentials(amyUsername, amyPassword, amyHost);
		}
		this.storage.put(USERNAME_CRED_KEY, credentials.getUsername());
		this.storage.put(PASSOWRD_CRED_KEY, credentials.getPassword());
		this.storage.put(IMAPSERVER_CRED_KEY, credentials.getImapServer());
		return this.mailSession.startNewMailSession(credentials);
	}

	/**
	 * Check if mail is currently connected to a mail server
	 * 
	 * @return true, if connected to mail server, else false
	 */
	public boolean isConnectedToMailServer() {
		return this.mailSession.isConnected();
	}

	/**
	 * Disconnect from currently connected mail service
	 */
	public void disconnectFromMailServer() {
		this.mailSession.endSession();
		this.storage.delete(USERNAME_CRED_KEY);
		this.storage.delete(PASSOWRD_CRED_KEY);
		this.storage.delete(IMAPSERVER_CRED_KEY);
	}

	/**
	 * Get the currently saved mail credentials
	 * 
	 * @return mail credentials if existing, else null
	 */
	public EMailCredentials getCredentials() {
		if (!this.storage.has(USERNAME_CRED_KEY) || !this.storage.has(PASSOWRD_CRED_KEY)
				|| !this.storage.has(IMAPSERVER_CRED_KEY)) {
			return null;
		}
		final String username = this.storage.get(USERNAME_CRED_KEY);
		final String password = this.storage.get(PASSOWRD_CRED_KEY);
		final String imapServer = this.storage.get(IMAPSERVER_CRED_KEY);
		return new EMailCredentials(username, password, imapServer);
	}

	/**
	 * Get the mail address or personal name of the sender of the message
	 * 
	 * @param message
	 *            the message
	 * @return if exists, the personal name of the sender, else the e-mail address of the sender as a String
	 * @throws MessagingException
	 *             when something goes wrong
	 */
	static String getFrom(Message message) throws MessagingException {
		InternetAddress address = (InternetAddress) message.getFrom()[0];
		String personal = address.getPersonal();
		return (personal == null) ? address.getAddress() : personal;
	}

	/**
	 * Connect to mail server with stored credentials, if there are any
	 */
	@PostConstruct
	public void connectToMailServer() {
		final EMailCredentials credentials = getCredentials();
		if (credentials != null) {
			connectToMailServer(credentials);
		}
		this.updateService.run();
	}
}
