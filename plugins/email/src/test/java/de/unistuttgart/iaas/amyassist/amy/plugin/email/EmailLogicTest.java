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

import java.util.Properties;

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jvnet.mock_javamail.Mailbox;
import org.jvnet.mock_javamail.MockFolder;
import org.jvnet.mock_javamail.MockStore;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;


import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for the email logic class 
 * 
 * @author Felix Burk
 */
@ExtendWith(FrameworkExtension.class)
public class EmailLogicTest {
		
	@Mock
	private Properties configLoader;
	
	@Reference
	private  TestFramework framework;
	
	private static EMailLogic emailLogic;
	
	/**
	 * Initializes the class variables before each test
	 */
	@BeforeEach
	public void setup() {
		this.configLoader = this.framework.mockService(Properties.class);
		emailLogic = this.framework.setServiceUnderTest(EMailLogic.class);
		
		final Session session = Session.getInstance(System.getProperties());
		Mailbox mMailbox;
		MockFolder folder;
		MimeMessage msg = new MimeMessage(session);

		try {
			MockStore store = new MockStore(session, new URLName("amy.speechassist@gmail.com"));
			mMailbox = new Mailbox(new InternetAddress("amy.speechassist@gmail.com"));
			folder = new MockFolder(store, mMailbox);
			
			msg.addRecipients(Message.RecipientType.TO, "amy.speechassist@gmail.com");
			msg.setSubject("Test");
			msg.setFlag(Flag.RECENT, true);
			msg.setFlag(Flag.SEEN, false);
			msg.setText("some text");
			Transport.send(msg);
			Transport.send(msg);
			folder.appendMessages(new Message[] {msg, msg});
			emailLogic.inbox = folder;
			
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
		
	}
	
	/**
	 * tests if new messages are received
	 */
	@Test
	public void testHasNewMessages() {
		boolean b = false;
		b = emailLogic.hasUnreadMessages();
	
		assertThat(new Boolean(b), equalTo(new Boolean(true)));
	}
	
	/**
	 * tests get plain text messages
	 */
	@Test
	public void testgetMessages() {
		assertThat(emailLogic.printPlainTextMessages(1), is(not(nullValue())));
	}
	
	/**
	 * tests the number of unread messages
	 */
	@Test
	public void testMessageCound() {
		assertThat(new Integer(emailLogic.getNewMessageCount()), equalTo(new Integer(2)));
	}
	
	/**
	 * tests sending a message,
	 * problem: we can't really check if a message is sent because we are not logged
	 * in to the amy google account
	 * 
	 * thats why this is not an optimal test
	 */
	@Test
	public void testSendMessage() {
		assertThat(emailLogic.sendMail("amy.speechassist@gmail.com", "test", "testBody"), equalTo("Message sent!"));
	}
	
	/**
	 * cleans up the mailbox
	 */
	@AfterAll
	public static void cleanup() {
		Mailbox.clearAll();
	}

}
