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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.session.GMailSession;
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

	@Reference
	private TestFramework framework;

	private GMailSession mailSession;

	private Properties configLoader;

	private ContactRegistry contactRegistry;

	private EMailLogic emailLogic;

	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.session = Session.getInstance(System.getProperties(), null);

		this.mailSession = this.framework.mockService(GMailSession.class);
		this.configLoader = this.framework.mockService(Properties.class);
		this.contactRegistry = this.framework.mockService(ContactRegistry.class);
		this.emailLogic = this.framework.setServiceUnderTest(EMailLogic.class);
	}

	/**
	 * Tests one case of method {@link EMailLogic#hasNewMessages(boolean)}
	 */
	@Test
	public void testHasNewMessages() {

	}

	/**
	 * Tests other case of method {@link EMailLogic#hasNewMessages(boolean)}
	 */
	@Test
	public void testHasNewImportantMessages() {

	}

	/**
	 * Test method for {@link EMailLogic#getNewMessageCount()}
	 */
	@Test
	public void testGetNewMessageCount() {

	}

	/**
	 * Test method for {@link EMailLogic#getNewImportantMessageCount()}
	 */
	@Test
	public void testGetNewImportantMessageCount() {

	}

	/**
	 * Test method for {@link EMailLogic#printMessages(boolean)}
	 */
	@Test
	public void testPrintMessages() {

	}

	/**
	 * Test method for {@link EMailLogic#getNewMessages()}
	 */
	@Test
	public void testGetNewMessages() {

	}

	/**
	 * Test method for {@link EMailLogic#getNewImportantMessages()}
	 */
	@Test
	public void testGetNewImportantMessages() {

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

	}

	/**
	 * Tests {@link EMailLogic#getMailsForREST()}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	public void testConvertToMessageDTO() throws MessagingException {

	}

	/**
	 * Tests {@link EMailLogic#getImportantMailAddresses()}
	 */
	@Test
	protected void testGetImportantMailAddresses() {
	}

	/**
	 * Tests {@link EMailLogic#isImportantMessage(Message)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	protected void testIsImportantMessage() throws MessagingException {

	}

	/**
	 * Tests {@link EMailLogic#getFrom(Message)}
	 * 
	 * @throws MessagingException
	 *             if something goes wrong
	 */
	@Test
	protected void testGetFrom() throws MessagingException {

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

	private List<Message> createRandomMessages(int amount) {
		List<Message> messages = new ArrayList<>();
		Random random = new Random();
		for (int i = 0; i < amount; i++) {
			byte[] bytes = new byte[5];
			random.nextBytes(bytes);
			String randomString = new String(bytes);
			messages.add(createMessage(randomString, randomString, randomString, LocalDateTime.now()));
		}
		return messages;
	}
}
