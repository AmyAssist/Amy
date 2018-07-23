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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity for EMail structure
 * 
 * @author Muhammed Kaya
 */
@XmlRootElement
public class MailEntity {

	private String recipient;
	private String subject;
	private String message;

	/**
	 * Constructor
	 */
	public MailEntity() {
		// Needed for JSON
	}
	
	/**
	 * Constructor
	 * 
	 * @param recipient the recipient of the mail
	 * @param subject the subject of the mail
	 * @param message the message of the mail
	 */
	public MailEntity(String recipient, String subject, String message) {
		this.recipient = recipient;
		this.subject = subject;
		this.message = message;
	}

	/**
	 * get the recipient
	 * 
	 * @return recipient of the mail
	 */
	public String getRecipient() {
		return this.recipient;
	}

	/**
	 * set the recipient
	 * 
	 * @param recipient
	 *            recipient of the mail
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * get the subject
	 * 
	 * @return subject of the mail
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * set the subject
	 * 
	 * @param subject
	 *            subject of the mail
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * get the message
	 * 
	 * @return message of the mail
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * set the message
	 * 
	 * @param message
	 *            message of the mail
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
