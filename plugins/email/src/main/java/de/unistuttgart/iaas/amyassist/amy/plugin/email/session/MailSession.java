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

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PreDestroy;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.rest.EMailCredentials;

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

	private static final String PROTOCOL = "imaps";

	private static final String INBOX_NAME = "INBOX";

	/**
	 * Get the inbox from the currently connected mail server
	 * 
	 * @return inbox folder
	 * @throws MessagingException
	 *             if folder couldn't be found or couldn't be opened
	 */
	public Folder getInbox() throws MessagingException {
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
			return true;
		} catch (MessagingException e) {
			this.logger.error("Couldn't connect to service", e);
		}
		return false;
	}

	@PreDestroy
	private void endSession() {
		if (this.store != null) {
			try {
				this.store.close();
				this.store = null;
			} catch (MessagingException e) {
				this.logger.error("Couldn't close previously opened mail store", e);
			}
		}
	}
}
