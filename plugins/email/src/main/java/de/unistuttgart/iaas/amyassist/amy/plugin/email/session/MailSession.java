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

/**
 * This interface defines the basic behavior every session that connects to a mail provider must have
 * 
 * @author Patrick Singer
 */
public abstract class MailSession {

	private Session session;
	private Store store;

	/**
	 * The folder which will be used for getting mails
	 */
	Folder inbox;

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
	void connect(String username, String password, String protocol, String hostAddress) throws MessagingException {
		if (username != null && password != null) {
			// set up session
			this.session = Session.getInstance(System.getProperties(), null);
			this.store = this.session.getStore(protocol);
			this.store.connect(hostAddress, username, password);
		} else {
			throw new IllegalArgumentException("Parameters invalid");
		}
	}

	/**
	 * Open folder, but only for reading
	 * 
	 * @param inboxFolderName
	 *            name of the inbox on the server
	 * 
	 * @return the opened folder, null if store is not connected
	 * @throws MessagingException
	 *             if something went wrong
	 */
	Folder openInboxReadOnly(String inboxFolderName) throws MessagingException {
		if (this.store.isConnected()) {
			Folder folderToOpen = this.store.getFolder(inboxFolderName);
			folderToOpen.open(Folder.READ_ONLY);
			this.inbox = folderToOpen;
			return folderToOpen;
		}
		return null;
	}

	/**
	 * Close all folders
	 * 
	 * @throws MessagingException
	 *             if closing a folder failed
	 */
	public void closeInbox() throws MessagingException {
		this.inbox.close(false);
		this.inbox.getStore().close();
	}
}
