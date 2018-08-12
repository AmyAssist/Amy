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

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PreDestroy;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Session class for the google mail provider
 * 
 * @author Patrick Singer
 */
@Service
public class GMailSession extends MailSession {

	@Reference
	private Properties configLoader;

	@Reference
	private Logger logger;

	private static final String EMAIL_USR_KEY = "email_usr";

	private static final String EMAIL_PW_KEY = "email_pw";

	private static final String PROTOCOL = "imaps";

	private static final String HOST_ADDRESS = "imap.gmail.com";

	@Override
	public Folder getInbox() throws MessagingException {
		if (this.inbox != null) {
			return this.inbox;
		}
		openInboxReadOnly("inbox");
		return this.inbox;
	}

	@PostConstruct
	private void init() {
		String username = this.configLoader.getProperty(EMAIL_USR_KEY);
		String password = this.configLoader.getProperty(EMAIL_PW_KEY);
		try {
			connect(username, password, PROTOCOL, HOST_ADDRESS);
		} catch (MessagingException e) {
			this.logger.error("Connecting to service failed", e);
		}
	}

	@PreDestroy
	private void endSession() {
		try {
			closeInbox();
		} catch (MessagingException e) {
			this.logger.error("Inbox couldn't be closed properly", e);
		}
	}
}
