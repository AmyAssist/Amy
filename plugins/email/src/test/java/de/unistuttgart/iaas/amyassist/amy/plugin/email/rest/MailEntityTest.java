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

package de.unistuttgart.iaas.amyassist.amy.plugin.email.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for MailEntity
 * 
 * @author Muhammed Kaya
 */
class MailEntityTest {

	private MailEntity mail;

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity#MailEntity(String, String, String)}.
	 */
	@Test
	@BeforeEach
	void testMailEntity() {
		this.mail = new MailEntity();
		this.mail = new MailEntity("example@mail.com", "Mail From Amy", "Hello!");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity#getRecipient()}.
	 */
	@Test
	void testGetRecipient() {
		assertThat(this.mail.getRecipient(), is("example@mail.com"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity#setRecipient(java.lang.String)}.
	 */
	@Test
	void testSetRecipient() {
		this.mail.setRecipient("unknown@mail.com");
		assertThat(this.mail.getRecipient(), is("unknown@mail.com"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity#getSubject()}.
	 */
	@Test
	void testGetSubject() {
		assertThat(this.mail.getSubject(), is("Mail From Amy"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity#setSubject(java.lang.String)}.
	 */
	@Test
	void testSetSubject() {
		this.mail.setSubject("Mail From Unknown");
		assertThat(this.mail.getSubject(), is("Mail From Unknown"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity#getMessage()}.
	 */
	@Test
	void testGetMessage() {
		assertThat(this.mail.getMessage(), is("Hello!"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.MailEntity#setMessage(java.lang.String)}.
	 */
	@Test
	void testSetMessage() {
		this.mail.setMessage("Bye!");
		assertThat(this.mail.getMessage(), is("Bye!"));

	}

}
