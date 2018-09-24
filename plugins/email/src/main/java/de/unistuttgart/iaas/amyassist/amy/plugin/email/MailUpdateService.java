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

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;

/**
 * This class checks every few minutes if the user has received a new email
 * 
 * @author Patrick Singer
 */
@Service(MailUpdateService.class)
public class MailUpdateService implements RunnableService {

	@Reference
	private EMailLogic mailLogic;

	@Reference
	private MessageHub messageHub;

	@Reference
	private TaskScheduler scheduler;

	@Reference
	private Logger logger;

	private ScheduledFuture<?> nextScheduledCall;

	private int lastMessageCount;

	private static final int MINUTE_INTERVAL = 1;

	private void checkForNewMails() {
		if (this.mailLogic.isConnectedToMailServer()) {
			final List<Message> messages = this.mailLogic.getNewMessages();
			final int currentMessageCount = messages.size();
			if (currentMessageCount > this.lastMessageCount) {
				final Message newest = messages.get(0);
				try {
					final String senderInfo = EMailLogic.getFrom(newest);
					final String subjectInfo = newest.getSubject();
					this.messageHub.publish("user/all/notification",
							"You've got new mail from " + senderInfo + ".\nSubject: " + subjectInfo);
				} catch (MessagingException me) {
					this.logger.error("Getting info from message failed");
				}
			}
			this.lastMessageCount = currentMessageCount;
		}
		scheduleNextUpdate();
	}

	private void scheduleNextUpdate() {
		this.nextScheduledCall = this.scheduler.schedule(this::checkForNewMails, MINUTE_INTERVAL, TimeUnit.MINUTES);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		this.lastMessageCount = this.mailLogic.getNewMessageCount(false);
		scheduleNextUpdate();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		if (this.nextScheduledCall != null) {
			this.nextScheduledCall.cancel(true);
		}
	}
}
