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

package io.github.amyassist.amy.plugin.email.session;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.PreDestroy;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.plugin.email.rest.EMailCredentials;

/**
 * Session class for the google mail provider
 * 
 * @author Patrick Singer
 */
@Service
public class MailSession {

	@Reference
	private Logger logger;

	private Store store;

	private boolean connected = false;

	private static final String PROTOCOL = "imaps";

	private static final String INBOX_NAME = "INBOX";

	/**
	 * Get the inbox from the currently connected mail server
	 * 
	 * @return inbox folder
	 * @throws MessagingException
	 *             if folder couldn't be found or couldn't be opened
	 * 
	 * @throws IllegalStateException
	 *             if session is not connected
	 */
	public Folder getInbox() throws MessagingException {
		if (!this.connected) {
			throw new IllegalStateException("Session is not connected");
		}
		Folder folderToOpen = this.store.getFolder(INBOX_NAME);
		folderToOpen.open(Folder.READ_ONLY);
		return folderToOpen;
	}

	/**
	 * Set up connection to an email service with given parameters
	 * 
	 * @param credentials
	 *            the mail credentials
	 * 
	 * @return if connecting was successful
	 */
	public boolean startNewMailSession(EMailCredentials credentials) {
		endSession();

		// set up new session
		Session session = Session.getInstance(System.getProperties(), null);
		try {
			this.store = session.getStore(PROTOCOL);
			this.store.connect(credentials.getImapServer(), credentials.getUsername(), credentials.getPassword());
			this.connected = true;
			return true;
		} catch (MessagingException e) {
			this.logger.error("Couldn't connect to service", e);
		}
		this.connected = false;
		return false;
	}

	/**
	 * Check if this mail session is connected to any mail server
	 * 
	 * @return true, if connected to mail server, else false
	 */
	public boolean isConnected() {
		return this.connected;
	}

	/**
	 * End the currently running mail session and disconnect from server
	 */
	@PreDestroy
	public void endSession() {
		if (this.store != null) {
			try {
				this.store.close();
				this.store = null;
			} catch (MessagingException e) {
				this.logger.error("Couldn't close previously opened mail store", e);
			}
		}
		this.connected = false;
	}
}
