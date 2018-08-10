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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PreDestroy;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MessageDTO;
import de.unistuttgart.iaas.amyassist.amy.registry.Contact;
import de.unistuttgart.iaas.amyassist.amy.registry.ContactRegistry;

/**
 * Logic class for the email functionality, that defines all the behavior
 * 
 * @author Patrick Singer, Felix Burk, Patrick Gebhardt
 */
@Service
public class EMailLogic {

	private Store store;

	/**
	 * the inbox object containing all messages. this is not private because we need to change the inbox in the unit
	 * tests
	 */
	Folder inbox;

	@Reference
	private Properties configLoader;

	@Reference
	private ContactRegistry contactRegistry;

	/**
	 * user name key for properties file
	 */
	public static final String EMAIL_USR_KEY = "email_usr";
	/**
	 * password key for properties file
	 */
	public static final String EMAIL_PW_KEY = "email_pw";

	@Reference
	private Logger logger;

	/**
	 * returns if unread messages have been found
	 * 
	 * @param checkForImportant
	 *            set this to true if you want to look for IMPORTANT new mails, set this to false if you only want to
	 *            look for new mails
	 * 
	 * @return success
	 */
	public boolean hasNewMessages(boolean checkForImportant) {
		if (checkForImportant) {
			return getNewImportantMessages().size() > 0;
		}
		return getNewMessages().size() > 0;
	}

	/**
	 * Returns number of new messages in inbox. The word "new" here refers to unread messages
	 * 
	 * @return number of messages in inbox
	 */
	public int getNewMessageCount() {
		openInboxReadOnly();
		try {
			return this.inbox.getUnreadMessageCount();
		} catch (MessagingException e) {
			this.logger.error("Couldn't get number of unread mails", e);
			return -1;
		}
	}

	/**
	 * Get amount of new important mails
	 *
	 * @return number of new important mails in inbox, -1 if something went wrong
	 */
	public int getNewImportantMessageCount() {
		return getNewImportantMessages().size();
	}

	/**
	 * Converts all new messages to a readable string
	 * 
	 * @param important
	 *            set this to true, if you only want the important mails to be converted, set this to false if you want
	 *            every message to be converted
	 * @return readable String of new messages
	 */
	public String printMessages(boolean important) {
		List<Message> messagesToPrint;
		StringBuilder sb = new StringBuilder();
		if (important) {
			messagesToPrint = getNewImportantMessages();
		} else {
			messagesToPrint = getNewMessages();
		}
		try {
			for (Message m : messagesToPrint) {
				InternetAddress from = (InternetAddress) m.getFrom()[0];
				String fromName = from.getPersonal();
				sb.append((fromName == null) ? from.getAddress() + "\n" : fromName + "\n");
				sb.append(m.getSubject() + "\n");
				sb.append(getContentFromMessage(m) + "\n\n");
			}
		} catch (MessagingException | IOException e) {
			this.logger.error("Something went wrong", e);
		}
		return sb.toString();
	}

	// ============================================================================================================================================
	// methods called only by logic class

	/**
	 * Get all unread messages from the inbox
	 * 
	 * @return all unread messages in inbox
	 */
	private List<Message> getNewMessages() {
		openInboxReadOnly();
		try {
			Message[] newMessages = this.inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
			return Arrays.asList(newMessages);
		} catch (MessagingException me) {
			this.logger.error("Searching for mails failed", me);
			return new ArrayList<>();
		}
	}

	/**
	 * Get all important messages from given message array
	 * 
	 * @return all important messages in given message array
	 */
	private List<Message> getNewImportantMessages() {
		List<Message> unseenMessages = getNewMessages();
		List<Message> importantMessages = new ArrayList<>();
		try {
			for (Message m : unseenMessages) {
				if (isImportantMessage(m)) {
					importantMessages.add(m);
				}
			}
		} catch (MessagingException e) {
			this.logger.error("Something went wrong", e);
		}
		return importantMessages;
	}

	private String getContentFromMessage(Message message) throws MessagingException, IOException {
		if (message.isMimeType("text/plain")) {
			return message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			try {
				Multipart mp = (Multipart) message.getContent();
				for (int i = 0; i < mp.getCount(); i++) {
					Part part = mp.getBodyPart(i);
					if (part.isMimeType("text/plain")) {
						return part.getContent().toString();
					}
				}
			} catch (ClassCastException cce) {
				/*
				 * Normally, casting should work fine when the content type is multipart, but there seems to be a
				 * problem with finding the mail configuration, for more info, see:
				 * https://javaee.github.io/javamail/FAQ#castmultipart
				 */
				this.logger.debug("Still getting a stream back");
			}
		}
		return "Message content not readable";

	}

	/**
	 * Get all mails in the inbox and convert them to MessageDTO objects for the REST class to send to the web app
	 * 
	 * @return all mails in the inbox in a list, because lists are better to work with than arrays
	 */
	public List<MessageDTO> getMailsForREST() {
		openInboxReadOnly();
		List<MessageDTO> messagesToSend = new ArrayList<>();
		Message[] messages;
		try {
			messages = this.inbox.getMessages();
			for (Message m : messages) {
				messagesToSend.add(new MessageDTO(getFrom(m), m.getSubject(), getContentFromMessage(m), m.getSentDate(),
						isImportantMessage(m)));
			}
			return messagesToSend;
		} catch (MessagingException | IOException e) {
			this.logger.error("Something went wrong");
			return null;
		}
	}

	/**
	 * Get all important mail addresses from the registry
	 * 
	 * @return List of important mail addresses saved in the registry
	 */
	protected List<String> getImportantMailAddresses() {
		List<String> importantAddresses = new ArrayList<>();
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
	 *             if something goes wrong
	 */
	protected boolean isImportantMessage(Message message) throws MessagingException {
		List<String> importantAddresses = getImportantMailAddresses();
		return importantAddresses.contains(getFrom(message));
	}

	/**
	 * Get the mail address of the sender of the message
	 * 
	 * @param message
	 *            the message
	 * @return e-mail address of the sender as a String
	 * @throws MessagingException
	 *             when something goes wrong
	 */
	protected static String getFrom(Message message) throws MessagingException {
		Address address = message.getFrom()[0];
		return ((InternetAddress) address).getAddress();
	}

	/**
	 * Opens the inbox so it can be accessible. It is only allowed to read
	 */
	private void openInboxReadOnly() {
		if (this.store.isConnected()) {
			this.logger.error("Inbox opened");
			try {
				this.inbox = this.store.getFolder("INBOX");
				this.inbox.open(Folder.READ_ONLY);
			} catch (MessagingException e) {
				this.logger.error("Could not open inbox", e);
			}
		} else {
			init();
		}
		assert (this.inbox != null);
	}

	/**
	 * Set up connection to mail server with credentials from registry
	 */
	@PostConstruct
	private void init() {
		String username = this.configLoader.getProperty(EMAIL_USR_KEY);
		String password = this.configLoader.getProperty(EMAIL_PW_KEY);

		if (username != null && password != null) {
			// set up session
			try {
				Session session = Session.getInstance(System.getProperties(), null);
				this.store = session.getStore("imaps");
				this.store.connect("imap.gmail.com", username, password);
				openInboxReadOnly();
			} catch (MessagingException me) {
				this.logger.error("Couldn't connect to service", me);
			}
		} else {
			this.logger.error("properties file not found");
		}
	}

	/**
	 * closes opened inbox
	 */
	@PreDestroy
	public void closeInbox() {
		try {
			this.inbox.close(false);
			this.inbox.getStore().close();
			this.inbox = null;
		} catch (MessagingException e) {
			this.logger.error("Closing inbox or closing store failed", e);
		}
	}
}
