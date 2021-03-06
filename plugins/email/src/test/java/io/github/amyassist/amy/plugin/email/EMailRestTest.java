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

import static io.github.amyassist.amy.test.matcher.rest.ResponseMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Random;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.plugin.email.rest.EMailCredentials;
import io.github.amyassist.amy.plugin.email.rest.EMailResource;
import io.github.amyassist.amy.plugin.email.rest.MessageDTO;
import io.github.amyassist.amy.test.FrameworkExtension;
import io.github.amyassist.amy.test.TestFramework;

/**
 * Test for the rest resource of email
 * 
 * @author Muhammed Kaya, Patrick Singer
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
	 * Tests {@link EMailResource#isConnected()}
	 */
	@Test
	public void testIsConnected() {
		when(this.logic.isConnectedToMailServer()).thenReturn(true);
		try (Response response = this.target.path("isConnected").request().get()) {
			assertThat(response.getStatus(), is(200));
			boolean successful = response.readEntity(Boolean.class);
			assertThat(successful, is(true));
		}
	}

	/**
	 * Test method for {@link EMailResource#connect(EMailCredentials)}
	 */
	@Test
	public void testConnect() {
		when(this.logic.connectToMailServer(null)).thenReturn(true);
		Entity<EMailCredentials> entity = Entity.entity(null, MediaType.APPLICATION_JSON);
		try (Response response = this.target.path("connect").request().post(entity)) {
			assertThat(response.getStatus(), is(200));
			boolean successful = response.readEntity(Boolean.class);
			assertThat(successful, is(true));
		}
	}

	/**
	 * Test method for {@link EMailResource#getMails(int)}
	 */
	@Test
	public void testGetMailsAll() {
		final int amountOfMails = -1;
		MessageDTO[] mails = createMessages(20);
		Mockito.when(this.logic.getMailsForREST(amountOfMails)).thenReturn(mails);

		try (Response response = this.target.path("getMails/" + amountOfMails).request().get()) {
			assertThat(response, status(200));
			MessageDTO[] messages = response.readEntity(MessageDTO[].class);
			assertThat(messages.length, is(20));
			for (int i = 0; i < messages.length; i++) {
				// test equality of objects
				MessageDTO message1 = mails[i];
				MessageDTO message2 = messages[i];

				assertThat(message1.getFrom(), equalTo(message2.getFrom()));
				assertThat(message1.getSubject(), equalTo(message2.getSubject()));
				assertThat(message1.getSentDate(), equalTo(message2.getSentDate()));
				assertThat(message1.isImportant(), equalTo(message2.isImportant()));
			}
			Mockito.verify(this.logic).getMailsForREST(amountOfMails);
		}
	}

	/**
	 * Test method for {@link EMailResource#getMails(int)}
	 */
	@Test
	public void testGetMailsAmount() {
		final int amountOfMails = 20;
		MessageDTO[] mails = createMessages(amountOfMails);
		Mockito.when(this.logic.getMailsForREST(amountOfMails)).thenReturn(mails);

		try (Response response = this.target.path("getMails/" + amountOfMails).request().get()) {
			assertThat(response, status(200));
			MessageDTO[] messages = response.readEntity(MessageDTO[].class);
			assertThat(messages.length, is(amountOfMails));
			for (int i = 0; i < messages.length; i++) {
				// test equality of objects
				MessageDTO message1 = mails[i];
				MessageDTO message2 = messages[i];

				assertThat(message1.getFrom(), equalTo(message2.getFrom()));
				assertThat(message1.getSubject(), equalTo(message2.getSubject()));
				assertThat(message1.getSentDate(), equalTo(message2.getSentDate()));
				assertThat(message1.isImportant(), equalTo(message2.isImportant()));
			}
			Mockito.verify(this.logic).getMailsForREST(amountOfMails);
		}
	}

	/**
	 * Tests {@link EMailResource#disconnect()}
	 */
	@Test
	public void testDisconnect() {
		try (Response response = this.target.path("disconnect").request().post(null)) {
			assertThat(response.getStatus(), is(204));
		}
	}

	private MessageDTO[] createMessages(int amount) {
		MessageDTO[] messages = new MessageDTO[amount];
		Random random = new Random();
		for (int i = 0; i < amount; i++) {
			byte[] bytes = new byte[5];
			random.nextBytes(bytes);
			String randomString = new String(bytes);
			messages[i] = new MessageDTO(randomString, randomString, LocalDateTime.now(), random.nextBoolean(),
					random.nextBoolean());
		}
		return messages;
	}
}
