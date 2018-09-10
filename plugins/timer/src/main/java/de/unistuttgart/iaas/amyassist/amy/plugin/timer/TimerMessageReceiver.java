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

package de.unistuttgart.iaas.amyassist.amy.plugin.timer;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.messagehub.annotations.MessageReceiver;
import de.unistuttgart.iaas.amyassist.amy.messagehub.annotations.Subscription;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.SmarthomeFunctionTopics;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.SystemTopics;

/**
 * This class controls the alarm sound file, which is used for the alarm clock
 * 
 * @author Patrick Gebhardt, Tim Neumann
 */
@MessageReceiver
public class TimerMessageReceiver {

	@Reference
	private TimerBeepService beepService;

	@Reference
	private Logger logger;

	/**
	 * @param message
	 *            message for the system
	 */
	@Subscription(SystemTopics.SMARTHOME + "/+/+/" + SmarthomeFunctionTopics.MUTE)
	public void beepMessage(String message) {
		switch (message) {
		case "true":
			this.beepService.stopBeeping();
			break;
		case "false":
			// do nothing
			break;
		default:
			this.logger.warn("unkown message {}", message);
			break;
		}
	}
}
