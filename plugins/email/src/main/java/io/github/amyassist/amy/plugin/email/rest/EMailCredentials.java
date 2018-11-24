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

package io.github.amyassist.amy.plugin.email.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Merges the email credentials into one object which is better for transferring the data
 * 
 * @author Patrick Singer
 */
@XmlRootElement
public class EMailCredentials {
	private String username;
	private String password;
	private String imapServer;

	/**
	 * Constructor
	 * 
	 * @param username
	 *            the mail address
	 * @param password
	 *            the mail password
	 * @param imapServer
	 *            the imap server
	 */
	public EMailCredentials(String username, String password, String imapServer) {
		this.username = username;
		this.password = password;
		this.imapServer = imapServer;
	}

	/**
	 * Empty constructor
	 */
	public EMailCredentials() {
		// Needed for json
	}

	/**
	 * Gets {@link #username username}
	 * 
	 * @return username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Gets {@link #password password}
	 * 
	 * @return password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Gets {@link #imapServer imapServer}
	 * 
	 * @return imapServer
	 */
	public String getImapServer() {
		return this.imapServer;
	}

}
