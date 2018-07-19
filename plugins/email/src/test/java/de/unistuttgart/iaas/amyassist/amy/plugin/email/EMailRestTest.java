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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of email
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
class EMailRestTest {

	@Reference
	private TestFramework testFramework;

	private EMailLogic logic;

	private WebTarget target;

	/**
	 * setUp
	 */
	@BeforeEach
	public void setUp() {
		this.target = this.testFramework.setRESTResource(EMailResource.class);
		this.logic = this.testFramework.mockService(EMailLogic.class);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.EMailResource#getNewMessageCount()}.
	 */
	@Test
	void testGetNewMessageCount() {
		Mockito.when(this.logic.getNewMessageCount()).thenReturn(5);
		Response response = this.target.path("new/count").request().get();
		int count = response.readEntity(Integer.class);
		assertEquals(count, 5);
		assertThat(response.getStatus(), is(200));

		Mockito.when(this.logic.getNewMessageCount()).thenReturn(-1);
		response = this.target.path("new/count").request().get();
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Initialize before accessing the inbox."));
		assertThat(response.getStatus(), is(401));
		Mockito.verify(this.logic, Mockito.times(2)).getNewMessageCount();
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.EMailResource#printPlainTextMessages(String, int)}.
	 */
	@Test
	void testPrintPlainTextMessages() {
		Mockito.when(this.logic.printPlainTextMessages(3)).thenReturn("Hello");
		Mockito.when(this.logic.printPlainTextMessages(4)).thenReturn("");
		Mockito.when(this.logic.printPlainTextMessages(5)).thenReturn("Bye");
		Mockito.when(this.logic.printImportantMessages(5)).thenReturn("Important");

		Response response = this.target.path("plains").queryParam("amount", "3").request().post(null);
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Hello"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).printPlainTextMessages(3);

		response = this.target.path("plains").queryParam("amount", "4").request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Could not fetch messages from inbox."));
		assertThat(response.getStatus(), is(409));
		Mockito.verify(this.logic).printPlainTextMessages(4);

		response = this.target.path("plains").request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Bye"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).printPlainTextMessages(5);

		response = this.target.path("plains/fails").request().post(null);
		assertThat(response.getStatus(), is(404));

		response = this.target.path("plains/important").request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Important"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).printImportantMessages(5);
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.EMailResource#hasUnreadMessages()}.
	 */
	@Test
	void testHasUnreadMessages() {
		Mockito.when(this.logic.hasUnreadMessages()).thenReturn(true);
		Response response = this.target.path("unread").request().get();
		Boolean actual = response.readEntity(Boolean.class);
		assertThat(actual, is(true));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).hasUnreadMessages();
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.EMailResource#sendMail(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testSendMail() {
		MailEntity mail = new MailEntity();
		mail.setRecipient("example@mail.com");
		mail.setSubject("Mail From Amy");
		mail.setMessage("Hello!");
		Entity<MailEntity> entity = Entity.entity(mail, MediaType.APPLICATION_JSON);
		
		Mockito.when(this.logic.sendMail(mail.getRecipient(), mail.getSubject(), mail.getMessage())).thenReturn("Message is sent!");
		Response response = this.target.path("new").request().post(entity);
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Message is sent!"));
		assertThat(response.getStatus(), is(200));
		Mockito.verify(this.logic).sendMail(mail.getRecipient(), mail.getSubject(), mail.getMessage());

		response = this.target.path("new").request().post(null);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Fill the missing fields."));
		assertThat(response.getStatus(), is(409));
		
		mail.setRecipient("");
		mail.setSubject("");
		mail.setMessage("");
		Mockito.when(this.logic.sendMail(mail.getRecipient(), mail.getSubject(), mail.getMessage())).thenReturn("Message could not be sent");
		response = this.target.path("new").request().post(entity);
		actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("Message could not be sent."));
		assertThat(response.getStatus(), is(409));
		Mockito.verify(this.logic).sendMail("", "", "");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.EMailResource#getImportantMailAddresses()}.
	 */
	@Test
	void testGetImportantMailAddresses() {
		List<String> emptyList = new ArrayList<>();
		List<String> addressesList = new ArrayList<>();
		addressesList.add("Amy");
		addressesList.add("Horst");

		Mockito.when(this.logic.getImportantMailAddresses()).thenReturn(addressesList);
		Response response = this.target.path("addresses").request().get();
		List<String> actual = response.readEntity(List.class);
		assertThat(actual, is(addressesList));
		assertThat(response.getStatus(), is(200));

		Mockito.when(this.logic.getImportantMailAddresses()).thenReturn(emptyList);
		response = this.target.path("addresses").request().get();
		String actualMsg = response.readEntity(String.class);
		assertThat(actualMsg, is("No important mail addresses were found."));
		assertThat(response.getStatus(), is(409));
		Mockito.verify(this.logic, Mockito.times(2)).getImportantMailAddresses();
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.EMailResource#init()}.
	 */
	@Test
	void testInit() {
		Response response = this.target.path("init").request().post(null);
		assertThat(response.getStatus(), is(204));
		Mockito.verify(this.logic).init();
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.EMailResource#closeInbox()}.
	 */
	@Test
	void testCloseInbox() {
		Response response = this.target.path("close").request().post(null);
		assertThat(response.getStatus(), is(204));
		Mockito.verify(this.logic).closeInbox();
	}

}
