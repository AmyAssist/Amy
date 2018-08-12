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
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.EMailResource;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MessageDTO;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

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
	 * Test method for {@link EMailResource#getAllMails()}
	 */
	@Test
	public void testGetAllMails() {
		final int amountOfMails = 15;
		List<MessageDTO> mails = createMessages(amountOfMails);
		when(this.logic.getMailsForREST(-1)).thenReturn(mails);

		try (Response response = this.target.path("getMails").request().get()) {
			assertEquals(200, response.getStatus());
			MessageDTO[] messages = response.readEntity(MessageDTO[].class);
			assertEquals(amountOfMails, messages.length);
			for (int i = 0; i < messages.length; i++) {
				assertEquals(mails.get(i), messages[i]);
			}
		}
	}

	private List<MessageDTO> createMessages(int amount) {
		List<MessageDTO> messages = new ArrayList<>();
		Random random = new Random();
		for (int i = 0; i < amount; i++) {
			byte[] bytes = new byte[5];
			random.nextBytes(bytes);
			String randomString = new String(bytes);
			messages.add(new MessageDTO(randomString, randomString, randomString, LocalDateTime.now(),
					random.nextBoolean()));
		}
		return messages;
	}
}
