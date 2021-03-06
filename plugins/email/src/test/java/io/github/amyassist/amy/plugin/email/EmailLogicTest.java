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

package io.github.amyassist.amy.plugin.email;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.plugin.email.rest.EMailCredentials;
import io.github.amyassist.amy.plugin.email.rest.MessageDTO;
import io.github.amyassist.amy.plugin.email.session.MailSession;
import io.github.amyassist.amy.registry.Contact;
import io.github.amyassist.amy.registry.ContactRegistry;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test for the email logic class
 * 
 * @author Felix Burk, Patrick Singer
 */
@ExtendWith(FrameworkExtension.class)
public class EmailLogicTest {

	@Reference
	private TestFramework framework;

	private MailSession mailSession;

	private ContactRegistry contactRegistry;

	private EMailLogic emailLogic;

	private Properties properties;

	private Folder inboxMock;

	private List<Contact> contacts = new ArrayList<>();

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
		this.properties = this.framework.mockService(Properties.class);
		this.emailLogic = this.framework.setServiceUnderTest(EMailLogic.class);

		// initialize mocks
		this.inboxMock = mock(Folder.class);

		// set mock return values
		when(this.mailSession.getInbox()).thenReturn(this.inboxMock);
		when(this.contactRegistry.getAll()).thenReturn(this.contacts);

	}

	/**
	 * Tests {@link EMailLogic#hasNewMessages(boolean)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	public void testHasNewMessagesNormalCase() throws MessagingException, IOException {
		// normal and important mails
		Message[] messages1 = createMockMessages(15, 7);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages1);

		assertEquals(true, this.emailLogic.hasNewMessages(false));
		assertEquals(true, this.emailLogic.hasNewMessages(true));

		// only normal mails
		Message[] messages2 = createMockMessages(10, 0);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages2);

		assertEquals(true, this.emailLogic.hasNewMessages(false));
		assertEquals(false, this.emailLogic.hasNewMessages(true));

		// only important mails
		Message[] messages3 = createMockMessages(10, 10);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages3);

		assertEquals(true, this.emailLogic.hasNewMessages(false));
		assertEquals(true, this.emailLogic.hasNewMessages(true));
	}

	/**
	 * Tests {@link EMailLogic#hasNewMessages(boolean)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testHasNewMessagesNoMails() throws MessagingException {
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(new Message[0]);

		assertEquals(false, this.emailLogic.hasNewMessages(false));
		assertEquals(false, this.emailLogic.hasNewMessages(true));
	}

	/**
	 * Test method for {@link EMailLogic#getNewMessageCount(boolean)}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	public void testGetNewMessageCountNormalCase() throws MessagingException, IOException {
		// normal and important mails
		Message[] messages1 = createMockMessages(15, 7);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages1);

		assertEquals(15, this.emailLogic.getNewMessageCount(false));
		assertEquals(7, this.emailLogic.getNewMessageCount(true));

		// only normal mails
		Message[] messages2 = createMockMessages(10, 0);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages2);

		assertEquals(10, this.emailLogic.getNewMessageCount(false));
		assertEquals(0, this.emailLogic.getNewMessageCount(true));

		// only important mails
		Message[] messages3 = createMockMessages(10, 10);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages3);

		assertEquals(10, this.emailLogic.getNewMessageCount(false));
		assertEquals(10, this.emailLogic.getNewMessageCount(true));
	}

	/**
	 * Test method for {@link EMailLogic#getNewMessageCount(boolean)}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testGetNewMessageCountNoMails() throws MessagingException {
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(new Message[0]);

		assertEquals(0, this.emailLogic.getNewMessageCount(false));
		assertEquals(0, this.emailLogic.getNewMessageCount(true));
	}

	/**
	 * Test method for {@link EMailLogic#printMessages(int, boolean)}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	public void testPrintMessagesImportant() throws MessagingException, IOException {
		final int amount = 3;
		Message[] messages = createMockMessages(10, 5);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages);
		String noMails = this.emailLogic.printMessages(0, true);
		String allMails = this.emailLogic.printMessages(-1, true);
		String someMails = this.emailLogic.printMessages(amount, true);
		String tooManyMails = this.emailLogic.printMessages(100, true);

		assertEquals("", noMails);
		assertEquals(allMails, tooManyMails);

		// check allMails
		for (int i = 0; i < messages.length; i++) {
			Message m = messages[i];
			String mailAddress = ((InternetAddress) m.getFrom()[0]).getAddress();
			if (mailAddress.contains("important")) {
				assertThat(allMails, containsString(mailAddress));
				assertThat(allMails, containsString(m.getSubject()));
			} else {
				assertThat(allMails, not(containsString(mailAddress)));
				assertThat(allMails, not(containsString(m.getSubject())));
			}
		}

		// check someMails
		for (int i = 0; i < messages.length; i++) {
			// printMails turns around the order of the mails received from the server
			Message m = messages[messages.length - 1 - i];
			String mailAddress = ((InternetAddress) m.getFrom()[0]).getAddress();
			if (mailAddress.contains("important") && i < amount) {
				assertThat(someMails, containsString(mailAddress));
				assertThat(someMails, containsString(m.getSubject()));
			} else {
				assertThat(someMails, not(containsString(mailAddress)));
				assertThat(someMails, not(containsString(m.getSubject())));
			}
		}
	}

	/**
	 * Test method for {@link EMailLogic#printMessages(int, boolean)}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	public void testPrintMessagesNotImportant() throws MessagingException, IOException {
		final int amount = 3;
		Message[] messages = createMockMessages(10, 0);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages);

		// careful! every call of printMessages will revert the order of the messages array
		String noMails = this.emailLogic.printMessages(0, false);
		String allMails = this.emailLogic.printMessages(-1, false);
		String someMails = this.emailLogic.printMessages(amount, false);
		String tooManyMails = this.emailLogic.printMessages(100, false);

		assertEquals("", noMails);
		assertEquals(allMails, tooManyMails);

		// check allMails
		for (Message m : messages) {
			String mailAddress = ((InternetAddress) m.getFrom()[0]).getAddress();
			assertThat(allMails, containsString(mailAddress));
			assertThat(allMails, containsString(m.getSubject()));
		}

		// check someMails
		for (int i = 0; i < messages.length; i++) {
			// printMails turns around the order of the mails received from the server
			Message m = messages[messages.length - 1 - i];
			String mailAddress = ((InternetAddress) m.getFrom()[0]).getAddress();
			if (i < amount) {
				assertThat(someMails, containsString(mailAddress));
				assertThat(someMails, containsString(m.getSubject()));
			} else {
				assertThat(someMails, not(containsString(mailAddress)));
				assertThat(someMails, not(containsString(m.getSubject())));
			}
		}
	}

	/**
	 * Test method for {@link EMailLogic#getNewMessages()}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	public void testGetNewMessages() throws MessagingException, IOException {
		Message[] messages = createMockMessages(15, 7);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages);
		assertEquals(Arrays.asList(messages), this.emailLogic.getNewMessages());

		Mockito.doThrow(MessagingException.class).when(this.inboxMock).search(ArgumentMatchers.any(FlagTerm.class));
		assertEquals(Collections.emptyList(), this.emailLogic.getNewMessages());
	}

	/**
	 * Test method for {@link EMailLogic#getNewImportantMessages()}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	public void testGetNewImportantMessagesNormalCase() throws MessagingException, IOException {
		Message[] messages = createMockMessages(15, 7);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages);
		List<Message> returnedMessages = this.emailLogic.getNewImportantMessages();
		assertThat(returnedMessages, hasSize(7));
		for (Message message : returnedMessages) {
			String mailAddress = ((InternetAddress) message.getFrom()[0]).getAddress();
			assertThat(mailAddress, containsString("important"));
		}

		// exception
		Mockito.doThrow(MessagingException.class).when(messages[0]).getFrom();
		assertThat(this.emailLogic.getNewImportantMessages(), is(Collections.emptyList()));
	}

	/**
	 * Test method for {@link EMailLogic#getNewImportantMessages()}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	public void testGetNewImportantMessagesNoMessages() throws MessagingException, IOException {
		Message[] messages = createMockMessages(15, 0);
		when(this.inboxMock.search(ArgumentMatchers.any(FlagTerm.class))).thenReturn(messages);
		List<Message> returnedMessages = this.emailLogic.getNewImportantMessages();
		assertThat(returnedMessages.size(), is(0));
		assertThat(returnedMessages, is(Collections.emptyList()));
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
	public void testGetMailsForRESTAll() throws MessagingException, IOException {
		Message[] messages = createMockMessages(10, 5);
		when(this.inboxMock.getMessages(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(messages);

		MessageDTO[] transferMessages = this.emailLogic.getMailsForREST(-1);
		for (int i = 0; i < transferMessages.length; i++) {
			compareMessages(messages[messages.length - 1 - i], transferMessages[i]);
		}
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
	public void testGetMailsForRESTAmount() throws MessagingException, IOException {
		final int amount = 7;
		Message[] messages = createMockMessages(7, 5);
		when(this.inboxMock.getMessages(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(messages);

		MessageDTO[] transferMessages = this.emailLogic.getMailsForREST(amount);
		for (int i = 0; i < transferMessages.length; i++) {
			compareMessages(messages[messages.length - 1 - i], transferMessages[i]);
		}
	}

	private void compareMessages(Message message, MessageDTO transferMessage) throws MessagingException {
		String messageFrom = ((InternetAddress) message.getFrom()[0]).getAddress();

		assertThat(transferMessage.getFrom(), is(messageFrom));
		assertThat(transferMessage.getSubject(), is(message.getSubject()));

		// need to convert from Date to LocalDateTime
		assertThat(transferMessage.getSentDate(),
				is(message.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
		assertThat(transferMessage.isImportant(), is(messageFrom.contains("important")));
		assertThat(transferMessage.isSeen(), is(message.isSet(Flag.SEEN)));
	}

	/**
	 * Tests {@link EMailLogic#getImportantMailAddresses()}
	 * 
	 * @throws IOException
	 *             if something goes wrong
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	protected void testGetImportantMailAddresses() throws MessagingException, IOException {
		createMockMessages(10, 7); // needed so we have a few contacts
		Set<String> importantAddresses = this.emailLogic.getImportantMailAddresses();
		assertThat(importantAddresses.size(), is(7));
		for (String s : importantAddresses) {
			assertThat(s, containsString("important"));
		}
	}

	/**
	 * Tests {@link EMailLogic#isImportantMessage(Message)}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	protected void testIsImportantMessage() throws MessagingException, IOException {
		Message[] normalMessages = createMockMessages(10, 0);
		Message[] importantMessages = createMockMessages(10, 10);

		for (Message message : normalMessages) {
			assertThat(this.emailLogic.isImportantMessage(message), is(false));
		}

		for (Message message : importantMessages) {
			assertThat(this.emailLogic.isImportantMessage(message), is(true));
		}
	}

	/**
	 * Tests {@link EMailLogic#connectToMailServer(EMailCredentials)}
	 */
	@Test
	protected void testConnectToMailServerStandardCredentials() {
		EMailCredentials credentials = new EMailCredentials("testUsername", "testPassword", "testHostAddress");

		when(this.mailSession.startNewMailSession(credentials)).thenReturn(true);
		assertTrue(this.emailLogic.connectToMailServer(credentials));
	}

	/**
	 * Tests {@link EMailLogic#connectToMailServer(EMailCredentials)}
	 */
	@Test
	protected void testConnectToMailServerEmptyCredentials() {
		when(this.mailSession.startNewMailSession(ArgumentMatchers.any(EMailCredentials.class))).thenReturn(true);
		assertTrue(this.emailLogic.connectToMailServer(null));
		Mockito.verify(this.properties).getProperty(EMailLogic.AMY_MAIL_ADDRESS_KEY);
		Mockito.verify(this.properties).getProperty(EMailLogic.AMY_MAIL_PW_KEY);
	}

	/**
	 * Tests {@link EMailLogic#isConnectedToMailServer()}
	 */
	@Test
	protected void testIsConnectedToMailServer() {
		Assertions.assertFalse(this.emailLogic.isConnectedToMailServer());

		when(this.mailSession.isConnected()).thenReturn(true);
		assertTrue(this.emailLogic.isConnectedToMailServer());
	}

	/**
	 * Tests {@link EMailLogic#disconnectFromMailServer()}
	 */
	@Test
	protected void testDisconnectFromMailServer() {
		this.emailLogic.disconnectFromMailServer();

		Mockito.verify(this.mailSession).endSession();
		Mockito.verify(this.framework.storage(), Mockito.atLeastOnce()).delete(ArgumentMatchers.anyString());
	}

	/**
	 * Tests {@link EMailLogic#getCredentials()}
	 */
	@Test
	protected void testGetCredentialsNotNull() {
		final String credentialString = "credential";
		when(this.framework.storage().has(ArgumentMatchers.anyString())).thenReturn(true);
		when(this.framework.storage().get(ArgumentMatchers.anyString())).thenReturn(credentialString);
		EMailCredentials creds = new EMailCredentials(credentialString, credentialString, credentialString);

		assertThat(this.emailLogic.getCredentials().getUsername(), is(creds.getUsername()));
		assertThat(this.emailLogic.getCredentials().getPassword(), is(creds.getPassword()));
		assertThat(this.emailLogic.getCredentials().getImapServer(), is(creds.getImapServer()));
	}

	/**
	 * Tests {@link EMailLogic#getCredentials()}
	 */
	@Test
	protected void testGetCredentialsNull() {
		when(this.framework.storage().has(ArgumentMatchers.anyString())).thenReturn(false);
		Assertions.assertNull(this.emailLogic.getCredentials());
	}

	/**
	 * Tests {@link EMailLogic#getFrom(Message)}
	 *
	 * @throws MessagingException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Test
	protected void testGetFrom() throws MessagingException, IOException {
		final String personalName = "personalName";
		Message[] messages = createMockMessages(2, 0);

		// first one has personal, second one hasn't
		InternetAddress addressWithPersonal = (InternetAddress) messages[0].getFrom()[0];
		when(addressWithPersonal.getPersonal()).thenReturn(personalName);

		assertThat(EMailLogic.getFrom(messages[0]), is(personalName));
		assertThat(EMailLogic.getFrom(messages[1]), containsString("address"));
	}

	private Message[] createMockMessages(int amount, int amountImportant) throws MessagingException {
		Message[] messages = new Message[amount];
		for (int i = 0; i < amount; i++) {
			Message mockMessage = mock(Message.class);
			when(mockMessage.getSubject()).thenReturn("subject" + i);
			when(mockMessage.isMimeType("text/plain")).thenReturn(true);
			InternetAddress address = mock(InternetAddress.class);
			when(mockMessage.getFrom()).thenReturn(new InternetAddress[] { address });
			when(mockMessage.getSentDate()).thenReturn(new Date(System.currentTimeMillis()));
			if (i < amount - amountImportant) {
				// normal message
				String mailAddress = "address" + i;
				when(address.getAddress()).thenReturn(mailAddress);

				// add normal contact
				Contact contact = mock(Contact.class);
				when(contact.isImportant()).thenReturn(false);
				when(contact.getEmail()).thenReturn(mailAddress);
				this.contacts.add(contact);

			} else {
				// important message
				String mailAddress = "important" + i;
				when(address.getAddress()).thenReturn(mailAddress);

				// add important contact
				Contact contact = mock(Contact.class);
				when(contact.isImportant()).thenReturn(true);
				when(contact.getEmail()).thenReturn(mailAddress);
				this.contacts.add(contact);
			}
			messages[i] = mockMessage;
		}
		return messages;
	}
}
