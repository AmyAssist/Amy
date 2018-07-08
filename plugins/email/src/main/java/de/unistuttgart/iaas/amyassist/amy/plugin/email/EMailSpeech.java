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

import javax.mail.MessagingException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * Class that defines the speech commands for the email functionality and calls the logic methods
 * 
 * @author Patrick Singer
 */
@Service(EMailSpeech.class)
@SpeechCommand({ "email", "mail" })
public class EMailSpeech {

	@Reference
	private EMailLogic logic;

	@Reference
	private Logger logger;

	/**
	 * Checks if there are new messages
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return yes or no
	 */
	@Grammar("new messages")
	public String newMessages(String... params) {
		try {
			if (this.logic.hasUnreadMessages()) {
				return "You have new messages.";
			}
			return "You have no new messages.";
		} catch (MessagingException me) {
			this.logger.error("Something wrong with inbox", me);
			return "nope";
		}
	}

	/**
	 * Gets number of UNSEEN (don't confuse with UNREAD) mails
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return number of unseen emails
	 */
	@Grammar("how many [new] (emails|mails) [do i have]")
	public String numberOfNewMails(String... params) {
		int s = this.logic.getNewMessageCount();
		if(s != -1) {
			return this.logic.getNewMessageCount() + " new mails.";
		}
		return "something went wrong - i am deeply sorry";
	}

	/**
	 * Reads the x most recent mails
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return x most recent mails
	 */
	@Grammar("read # most recent (emails|mails)")
	public String readRecentMails(String... params) {
		String message;
		message = this.logic.printPlainTextMessages(Integer.parseInt(params[1]));
		if(message.equals("")) {
			return "no messages received - poor you";
		}
		return message;
	}

	/**
	 * Sends an example email
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return confirmation
	 */
	@Grammar("send example mail")
	public String sendExampleMail(String... params) {
		try {
			return this.logic.sendMail("st142778@stud.uni-stuttgart.de", "Mail From Amy", "Hello!");
		} catch (MessagingException e) {
			this.logger.error("Sending mail failed!", e);
			return "nope";
		}
	}
}
