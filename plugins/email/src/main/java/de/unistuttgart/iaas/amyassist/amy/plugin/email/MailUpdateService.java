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

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import de.unistuttgart.iaas.amyassist.amy.plugin.email.session.MailSession;

/**
 * This class checks every few minutes if the user has received a new email
 * 
 * @author Patrick Singer
 */
@Service(MailUpdateService.class)
public class MailUpdateService implements RunnableService {

	@Reference
	private MailSession mailSession;

	@Reference
	private MessageHub messageHub;

	@Reference
	private TaskScheduler scheduler;

	@Reference
	private Logger logger;

	private int lastMessageCount;

	private static final int MINUTE_INTERVAL = 1;

	private void checkForNewMails() {
		if (this.mailSession.isConnected()) {
			try (final Folder inbox = this.mailSession.getInbox()) {
				final int currentMessageCount = inbox.getMessageCount();
				if (currentMessageCount > this.lastMessageCount) {
					final String mailAddress = inbox.getStore().getURLName().getUsername();
					this.messageHub.publish("user/" + mailAddress + "/mail", "You've got new mail", 0, false);
				}
				this.lastMessageCount = currentMessageCount;
			} catch (MessagingException e) {
				this.logger.error("Checking for new mails failed", e);
			}

			this.scheduler.schedule(this::checkForNewMails, Instant.now().plus(MINUTE_INTERVAL, ChronoUnit.MINUTES));
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		checkForNewMails();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		// remove checkForNewMails call from TaskScheduler
	}
}
