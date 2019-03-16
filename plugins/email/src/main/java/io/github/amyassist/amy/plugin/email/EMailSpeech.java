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

package io.github.amyassist.amy.plugin.email;

import java.util.Map;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.natlang.EntityData;
import io.github.amyassist.amy.core.natlang.Intent;
import io.github.amyassist.amy.core.natlang.SpeechCommand;

/**
 * Class that defines the speech commands for the email functionality and calls the logic methods
 * 
 * @author Patrick Singer, Felix Burk
 */
@Service
@SpeechCommand
public class EMailSpeech {

	private static final String NO_MAILS = "No new messages";
	private static final String NO_IMP_MAILS = "No important new messages";
	private static final String IMPORTANT = "important";

	@Reference
	private EMailLogic logic;

	@Reference
	private Logger logger;

	/**
	 * Connects to the Amy Mail
	 * 
	 * @return connected
	 */
	@Intent
	public String connectToAmyMail(Map<String, EntityData> entities) {
		if (this.logic.connectToMailServer(null)) {
			return "Connected to mail server";
		}
		return "Couldn't connect to mail server";
	}

	/**
	 * Disconnect from currently connected mail server
	 * 
	 * @return disconnected
	 */
	@Intent
	public String disconnect(Map<String, EntityData> entities) {
		this.logic.disconnectFromMailServer();
		return "disconnected";
	}

	/**
	 * Checks if there are new messages
	 * 
	 * @param entities
	 *            contain if important or not
	 * @return yes or no
	 */
	@Intent()
	public String newMessages(Map<String, EntityData> entities) {
		try {
			if (entities.get(IMPORTANT) != null && entities.get(IMPORTANT).getString().contains(IMPORTANT)) {
				if (this.logic.hasNewMessages(true)) {
					return "You have new important messages.";
				}
				return NO_IMP_MAILS;
			}
			if (this.logic.hasNewMessages(false)) {
				return "You have new messages.";
			}
		} catch (IllegalStateException ise) {
			return ise.getMessage();
		}
		return NO_MAILS;
	}

	/**
	 * Gets number of UNREAD mails
	 * 
	 * @param entities
	 *            contain if important or not
	 * @return number of unseen emails
	 */
	@Intent()
	public String numberOfNewMails(Map<String, EntityData> entities) {
		try {
			if (entities.get(IMPORTANT) != null && entities.get(IMPORTANT).getString().contains(IMPORTANT)) {
				int count = this.logic.getNewMessageCount(true);
				if (count > 0) {
					return count + " new important messages";
				}
				return NO_IMP_MAILS;
			}
			int count = this.logic.getNewMessageCount(false);
			if (count > 0) {
				return count + " new mails.";
			}
			return NO_MAILS;
		} catch (IllegalStateException ise) {
			return ise.getMessage();
		}
	}

	/**
	 * Reads the x most recent mails
	 * 
	 * @param entities
	 *            contain if important or not and how many mails should be read
	 * @return x most recent mails
	 */
	@Intent()
	public String readRecentMails(Map<String, EntityData> entities) {
		boolean important = false;
		if (entities.get(IMPORTANT) != null) {
			important = entities.get(IMPORTANT).getString().contains(IMPORTANT);
		}
		try {
			if (entities.get("all") != null && entities.get("all").getString() != null) {
				return this.logic.printMessages(-1, important);
			}
			if (entities.get("number") != null) {
				int amount = entities.get("number").getNumber();
				return this.logic.printMessages(amount, important);
			}
		} catch (IllegalStateException ise) {
			return ise.getMessage();
		}
		return "";
	}
}
