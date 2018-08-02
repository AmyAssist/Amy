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

import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;

/**
 * This class defines a message object which is used for handling mails, it represents a single e-mail
 * 
 * @author Patrick Singer
 */
@XmlRootElement
public class MessageDTO extends Entity {

	private String from;
	private String subject;
	private String content;
	// private Date sentDate;
	private boolean important;

	/**
	 * Constructor needed to parse as JSON
	 */
	public MessageDTO() {
	}

	/**
	 * Constructor for a message
	 * 
	 * @param from
	 *            the mail address of the sender
	 * @param subject
	 *            subject of the messages
	 * @param content
	 *            the content of the message
	 * @param important
	 *            is message from important sender
	 */
	public MessageDTO(String from, String subject, String content, boolean important) {
		this.from = from;
		this.subject = subject;
		this.content = content;
		// this.sentDate = sentDate;
		this.important = important;
	}

	public String getFrom() {
		return this.from;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getContent() {
		return this.content;
	}

	/**
	 * @return if the message is important
	 */
	public boolean isImportant() {
		return this.important;
	}
}
