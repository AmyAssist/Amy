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
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PreDestroy;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Logic class for the email functionality, that defines all the behaviour
 * 
 * @author Patrick Singer
 */
@Service
public class EMailLogic {

	private Session session;
	private Folder inbox;
	
	@Reference
	private Properties configLoader;
	
	public static final String EMAIL_USR_KEY = "email_usr";
	public static final String EMAIL_PW_KEY = "email_pw";


	@Reference
	private Logger logger;

	/**
	 * Returns number of new messages in inbox. New refers to messages that were received while the mailbox was not
	 * opened.
	 * 
	 * @return number of messages in inbox
	 */
	public int getNewMessageCount() {
		if (this.inbox != null) {
			try {
				return this.inbox.getUnreadMessageCount();
			} catch (MessagingException e) {
				this.logger.error("Inbox fail", e);
			}
		}
		this.logger.error("Tried to access a closed inbox!");
		return -1;
	}

	/**
	 * Prints the plain text from all the mails in the inbox
	 * 
	 * @param amount
	 *            the amount of emails that should be returned
	 * @return most recent emails
	 * 
	 */
	public String printPlainTextMessages(int amount) {
		StringBuilder b = new StringBuilder();
		int count = 0;
		if (this.inbox != null) {
			Message[] messages;
			try {
				messages = this.inbox.getMessages();
				for (Message m : messages) {
					if(count <= amount) {
						b.append(concatenateMessage(m));
					}
					count++;
				}
			} catch (MessagingException | IOException e) {
				this.logger.error("couldn't fetch messages from inbox");
				return "";
			}
			
		}
		return b.toString();
	}

	/**
	 * returns if unread messages have been found
	 * 
	 * @return success
	 * @throws MessagingException
	 */
	public boolean hasUnreadMessages() throws MessagingException {
		if (this.inbox != null) {
			 Message messages[] = this.inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
			 return messages.length > 0;
		}
		return false;
	}

	/**
	 * Transforms the message into a readable String
	 * 
	 * @param message
	 *            message to transform
	 * @return concatenated String of message
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static String concatenateMessage(Message message) throws MessagingException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("\nMessage:");
		sb.append("\nFrom: " + Arrays.toString(message.getFrom()));
		sb.append("\nSubject: " + message.getSubject());
		sb.append("\nSent: " + message.getSentDate());
		sb.append("\nContent: " + message.getContent());


		// if (message.isMimeType("text/plain")) {
		// sb.append("\nNachricht ist text/plain");
		// } else if (message.isMimeType("multipart/*")) {
		// sb.append("\nVerarbeite multipart/* Nachricht");
		// Multipart mp = (Multipart) message.getContent();
		//
		// // Der erste Part ist immer die Hauptnachricht
		// if (mp.getCount() > 1) {
		// Part part = mp.getBodyPart(0);
		// sb.append("\n" + part.getContent());
		// }
		// }
		return sb.toString();
	}

	public String sendMail(String recipient, String subject, String message) throws MessagingException {
		Message msg = new MimeMessage(this.session);

		InternetAddress addressTo = new InternetAddress(recipient);
		msg.setRecipient(Message.RecipientType.TO, addressTo);

		msg.setSubject(subject);
		msg.setContent(message, "text/plain");
		Transport.send(msg);
		return "Message sent!";
	}

	/**
	 * Get credentials from file or registry
	 */
	@PostConstruct
	public void init() {
		String username = this.configLoader.getProperty(EMAIL_USR_KEY);
		String password = this.configLoader.getProperty(EMAIL_PW_KEY);
		System.out.println("logging in2");


		if(username != null && password != null) {
			System.out.println("logging in");
			startSession(username, password);
			openInboxReadOnly();
		}else {
			this.logger.error("properties file not found");
		}
	}

	/**
	 * Closes the opened inbox
	 * 
	 * @throws MessagingException
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

	/**
	 * Log into the email account
	 * 
	 * @param username
	 *            username of the account
	 * @param password
	 *            password of the account
	 */
	private void startSession(String username, String password) {
		final Properties props = new Properties();

		/*
		 * We should probably use imap instead of pop because it syncs up with the email server
		 */
		// Zum Empfangen
		props.setProperty("mail.pop3.host", "pop.gmail.com");
		props.setProperty("mail.pop3.username", username);
		props.setProperty("mail.pop3.password", password);
		props.setProperty("mail.pop3.port", "995");
		props.setProperty("mail.pop3.auth", "true");
		props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		// Zum Senden
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");

		this.session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.getProperty("mail.pop3.username"),
						props.getProperty("mail.pop3.password"));
			}
		});
	}

	/**
	 * Opens the inbox so it can be accessible. It is only allowed to read
	 */
	private void openInboxReadOnly() {
		try {
			Store store = this.session.getStore("pop3");
			store.connect();

			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			
			this.inbox = folder;
			
		} catch (NoSuchProviderException e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		} catch (MessagingException e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}

	}
}
