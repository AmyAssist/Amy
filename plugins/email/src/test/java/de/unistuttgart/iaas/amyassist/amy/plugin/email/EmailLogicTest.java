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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MessageDTO;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.session.MailSession;
import de.unistuttgart.iaas.amyassist.amy.registry.Contact;
import de.unistuttgart.iaas.amyassist.amy.registry.ContactRegistry;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for the email logic class
 * 
 * @author Felix Burk, Patrick Singer
 */
@ExtendWith(FrameworkExtension.class)
public class EmailLogicTest {

	private Session session;

	private Folder inboxMock;

	private static Contact importantContact;

	private static Contact otherContact;

	private static List<Contact> contacts = new ArrayList<>();

	@Reference
	private TestFramework framework;

	private MailSession mailSession;

	private ContactRegistry contactRegistry;

	private EMailLogic emailLogic;

	private static final String IMPORTANT_ADDRESS = "mail@example.com";

	/**
	 * Initializes the class variables before each test
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@BeforeEach
	public void setup() throws MessagingException {
		this.mailSession = this.framework.mockService(MailSession.class);
		this.contactRegistry = this.framework.mockService(ContactRegistry.class);
		this.emailLogic = this.framework.setServiceUnderTest(EMailLogic.class);

		// initialize mocks and class fields
		this.session = Session.getInstance(System.getProperties(), null);
		this.inboxMock = mock(Folder.class);
		importantContact = mock(Contact.class);
		otherContact = mock(Contact.class);
		contacts.add(importantContact);
		contacts.add(otherContact);

		// set mock return values
		when(this.mailSession.getInbox()).thenReturn(this.inboxMock);
		when(this.contactRegistry.getAll()).thenReturn(contacts);
		when(new Boolean(importantContact.isImportant())).thenReturn(new Boolean(true));
		when(new Boolean(otherContact.isImportant())).thenReturn(new Boolean(false));
		when(importantContact.getEmail()).thenReturn(IMPORTANT_ADDRESS);
	}

	/**
	 * Tests {@link EMailLogic#hasNewMessages(boolean)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testHasNewMessages() throws MessagingException {
		/*
		 * check all mails
		 */

		// "normal" case
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(15, 7));
		assertEquals(new Boolean(true), new Boolean(this.emailLogic.hasNewMessages(false)));

		// no mails
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(0, 0));
		assertEquals(new Boolean(false), new Boolean(this.emailLogic.hasNewMessages(false)));

		/*
		 * check important
		 */

		// "normal" case
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(15, 7));
		assertEquals(new Boolean(true), new Boolean(this.emailLogic.hasNewMessages(true)));

		// no important mails
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(10, 0));
		assertEquals(new Boolean(false), new Boolean(this.emailLogic.hasNewMessages(true)));

		// no mails
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(0, 0));
		assertEquals(new Boolean(false), new Boolean(this.emailLogic.hasNewMessages(true)));
	}

	/**
	 * Test method for {@link EMailLogic#getNewMessageCount(boolean)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testGetNewMessageCount() throws MessagingException {

		/*
		 * check all mails
		 */

		// "normal" case
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(15, 7));
		assertEquals(15, this.emailLogic.getNewMessageCount(false));

		// no mails
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(0, 0));
		assertEquals(0, this.emailLogic.getNewMessageCount(false));

		/*
		 * check important
		 */

		// "normal" case
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(15, 7));
		assertEquals(7, this.emailLogic.getNewMessageCount(true));

		// no important mails
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(10, 0));
		assertEquals(0, this.emailLogic.getNewMessageCount(true));

		// no mails
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(0, 0));
		assertEquals(0, this.emailLogic.getNewMessageCount(true));
	}

	/**
	 * Test method for {@link EMailLogic#printMessages(int, boolean)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testPrintMessages() throws MessagingException {
		// not important
		Message normalMessage = createMessage("randomMailAddress", "randomSubject", "randomContent",
				LocalDateTime.now());
		Message importantMessage = createMessage(IMPORTANT_ADDRESS, "randomImportantSubject", "randomImportantContent",
				LocalDateTime.now());
		Message[] messages = new Message[] { normalMessage, importantMessage };
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages);

		// all mails
		String messageString = this.emailLogic.printMessages(-1, false);
		Boolean containsStrings = new Boolean(messageString.contains("randomMailAddress")
				&& messageString.contains("randomSubject") && messageString.contains("randomContent")
				&& messageString.contains(IMPORTANT_ADDRESS) && messageString.contains("randomImportantSubject")
				&& messageString.contains("randomImportantContent"));
		assertEquals(new Boolean(true), containsStrings);

		// important
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages);
		messageString = this.emailLogic.printMessages(-1, true);
		containsStrings = new Boolean(
				messageString.contains(IMPORTANT_ADDRESS) && messageString.contains("randomImportantSubject")
						&& messageString.contains("randomImportantContent"));
	}

	/**
	 * Test method for {@link EMailLogic#getNewMessages()}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testGetNewMessages() throws MessagingException {
		Message[] messages = createRandomMessages(15, 7);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages);
		assertEquals(Arrays.asList(messages), this.emailLogic.getNewMessages());
	}

	/**
	 * Test method for {@link EMailLogic#getNewImportantMessages()}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testGetNewImportantMessages() throws MessagingException {
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(15, 7));
		assertEquals(7, this.emailLogic.getNewImportantMessages().size());

		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(createRandomMessages(15, 0));
		assertEquals(0, this.emailLogic.getNewImportantMessages().size());
	}

	/**
	 * Test method for {@link EMailLogic#getContentFromMessage(Message)}
	 * 
	 * @throws IOException
	 *             if something goes wrong
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testGetContentFromMessage() throws IOException, MessagingException {
		Message message = createMessage(null, null, "foo", null);
		assertEquals("foo", this.emailLogic.getContentFromMessage(message));

		// TODO: test multipart messages
	}

	/**
	 * Tests {@link EMailLogic#getMailsForREST(int)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	public void testGetMailsForREST() throws MessagingException, IOException {
		Message[] messages = createRandomMessages(15, 7);
		when(this.inboxMock.getMessages()).thenReturn(messages);

		// all
		List<MessageDTO> transferMessages = this.emailLogic.getMailsForREST(-1);
		assertEquals(new Boolean(true), new Boolean(messagesForRESTAreEqual(messages, transferMessages)));

		// distinctive amount
		transferMessages = this.emailLogic.getMailsForREST(10);
		assertEquals(new Boolean(true), new Boolean(messagesForRESTAreEqual(messages, transferMessages)));
	}

	/**
	 * Tests {@link EMailLogic#getImportantMailAddresses()}
	 */
	@Test
	protected void testGetImportantMailAddresses() {
		Set<String> addresses = new HashSet<>();
		addresses.add(IMPORTANT_ADDRESS);
		assertEquals(addresses, this.emailLogic.getImportantMailAddresses());
	}

	/**
	 * Tests {@link EMailLogic#isImportantMessage(Message)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	protected void testIsImportantMessage() throws MessagingException {
		Message importantMessage = createMessage(IMPORTANT_ADDRESS, null, null, null);
		Message otherMessage = createMessage("randomMailAddress", null, null, null);
		assertEquals(new Boolean(true), new Boolean(this.emailLogic.isImportantMessage(importantMessage)));
		assertEquals(new Boolean(false), new Boolean(this.emailLogic.isImportantMessage(otherMessage)));
	}

	/**
	 * Tests {@link EMailLogic#getFrom(Message)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	protected void testGetFrom() throws MessagingException {
		Message message1 = createMessage(IMPORTANT_ADDRESS, null, null, null);
		assertEquals(IMPORTANT_ADDRESS, EMailLogic.getFrom(message1));

		final String mailAddress = "exampleMailAddress";
		Message message2 = createMessage(mailAddress, null, null, null);
		assertEquals(mailAddress, EMailLogic.getFrom(message2));
	}

	private boolean messagesForRESTAreEqual(Message[] messages, List<MessageDTO> transferMessages)
			throws MessagingException, IOException {
		for (int i = 0; i < transferMessages.size(); i++) {
			Message message = messages[messages.length - 1 - i];
			MessageDTO transferMessage = transferMessages.get(i);
			boolean senderEqual = ((InternetAddress) message.getFrom()[0]).getAddress()
					.equals(transferMessage.getFrom());
			boolean subjectEqual = message.getSubject().equals(transferMessage.getSubject());
			boolean contentEqual = message.getContent().toString().equals(transferMessage.getContent());
			boolean dateEqual = message.getSentDate().equals(Timestamp.valueOf(transferMessage.getSentDate()));
			if (!senderEqual || !subjectEqual || !contentEqual || !dateEqual) {
				return false;
			}
		}
		return true;
	}

	private Message[] createRandomMessages(int amount, int amountImportant) {
		Message[] messages = new Message[amount];
		for (int i = 0; i < amount; i++) {
			String inputString = "testMail" + i;
			if (i < amount - amountImportant) {
				// normal message
				messages[i] = createMessage(inputString, inputString, inputString, LocalDateTime.now());
			} else {
				// important message
				messages[i] = createMessage(IMPORTANT_ADDRESS, inputString, inputString, LocalDateTime.now());
			}
		}
		return messages;
	}

	/**
	 * Create a message with the given parameters. Put null for every parameter that is not important
	 * 
	 * @param from
	 *            mail address of the sender as string
	 * @param subject
	 *            subject of the message
	 * @param content
	 *            content of the message as plain text string
	 * @param sentDate
	 *            date that the message was sent
	 * @return new Message object with the given parameters, returns null if something went wrong
	 */
	private Message createMessage(String from, String subject, String content, LocalDateTime sentDate) {
		Message message = new MimeMessage(this.session);
		try {
			if (from != null) {
				InternetAddress address = new InternetAddress(from);
				message.setFrom(address);
			}
			if (subject != null) {
				message.setSubject(subject);
			}
			if (content != null) {
				message.setContent(content, "text/plain");
			}
			if (sentDate != null) {
				message.setSentDate(Timestamp.valueOf(sentDate));
			}
		} catch (MessagingException me) {
			return null;
		}
		return message;
	}
}
