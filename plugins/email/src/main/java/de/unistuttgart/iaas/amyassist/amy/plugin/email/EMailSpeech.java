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

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;

/**
 * Class that defines the speech commands for the email functionality and calls the logic methods
 * 
 * @author Patrick Singer, Felix Burk
 */
@SpeechCommand
public class EMailSpeech {

	private static final String NOMAILS = "No new messages";
	private static final String NOIMPMAILS = "No important new messages";

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
	@Grammar("[do i have] new [important] messages")
	public String newMessages(String... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i].equals("important")) {
				if (this.logic.hasNewMessages(true)) {
					return "You have new important messages.";
				}
				return NOIMPMAILS;
			}
		}
		if (this.logic.hasNewMessages(false)) {
			return "You have new messages.";
		}
		return NOMAILS;
	}

	/**
	 * Gets number of UNREAD mails
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return number of unseen emails
	 */
	@Grammar("how many [new] [important] (emails|mails) [do i have]")
	public String numberOfNewMails(String... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i].equals("important")) {
				int count = this.logic.getNewMessageCount(true);
				if (count > 0) {
					return count + " new important messages";
				}
				return NOIMPMAILS;
			}
		}
		int count = this.logic.getNewMessageCount(false);
		if (count > 0) {
			return count + " new mails.";
		}
		return NOMAILS;
	}

	/**
	 * Reads the x most recent mails
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return x most recent mails
	 */
	@Grammar("read (#|all) [important] (emails|mails)")
	public String readRecentMails(String... params) {
		boolean important = params[2].equals("important");
		if (!params[1].equals("all")) {
			if (important) {
				return this.logic.printMessages(-1, true);
			}
			return this.logic.printMessages(-1, false);
		}
		int amount = Integer.parseInt(params[1]);
		if (important) {
			return this.logic.printMessages(amount, true);
		}
		return this.logic.printMessages(amount, false);
	}
}
