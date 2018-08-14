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

package de.unistuttgart.iaas.amyassist.amy.plugin.email.session;

import javax.annotation.Nonnull;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * This interface defines the basic behavior every session that connects to a mail provider must have
 * 
 * @author Patrick Singer
 */
public abstract class MailSession {

	/**
	 * The mail store
	 */
	protected Store store;

	/**
	 * Get inbox of this class. If the inbox is non-existent, or not opened, it will be created and opened
	 * 
	 * @return opened inbox
	 * @throws MessagingException
	 *             if the folder couldn't be opened
	 */
	public abstract Folder getInbox() throws MessagingException;

	/**
	 * Set up connection to an email service
	 * 
	 * @param username
	 *            email address
	 * @param password
	 *            password
	 * @param protocol
	 *            email protocol, e.g. imap, pop3
	 * @param hostAddress
	 *            address of the mail provider, e.g. imap.gmail.com
	 * @throws MessagingException
	 *             if connecting fails
	 */
	void connect(@Nonnull String username, @Nonnull String password, @Nonnull String protocol,
			@Nonnull String hostAddress) throws MessagingException {

		Session session = Session.getInstance(System.getProperties(), null);
		this.store = session.getStore(protocol);
		this.store.connect(hostAddress, username, password);

	}

	/**
	 * Close all folders
	 * 
	 * @throws MessagingException
	 *             if closing a folder failed
	 */
	protected void endSession() throws MessagingException {
		this.store.close();
	}
}
