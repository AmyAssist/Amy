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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.messagehub.annotations.MessageReceiver;
import de.unistuttgart.iaas.amyassist.amy.messagehub.annotations.Subscription;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.SmarthomeFunctionTopics;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.SystemTopics;

/**
 * MessageReveiver for music and sound messages
 * 
 * @author Leon Kiefer
 */
@MessageReceiver
public class SpotifyMessageReciever {

	private static final String MUSIC_MUTE_TOPIC_FUNCTION = "muteMusic";
	@Reference
	private PlayerLogic player;
	@Reference
	private Logger logger;

	/**
	 * 
	 * @param message
	 *            "true" or "false"
	 */
	@Subscription(SystemTopics.SMARTHOME + "/+/+/" + SmarthomeFunctionTopics.MUTE)
	public void mute(String message) {
		switch (message) {
		case "true":
			this.player.setSuppressed(true);
			break;
		case "false":
			this.player.setSuppressed(false);
			break;
		default:
			this.logger.warn("unknown message {}", message);
			break;
		}
	}

	/**
	 * 
	 * @param message
	 *            "true" or "false"
	 */
	@Subscription(SystemTopics.SMARTHOME + "/+/+/" + MUSIC_MUTE_TOPIC_FUNCTION)
	public void muteMusic(String message) {
		switch (message) {
		case "true":
			this.player.setSuppressed(true);
			break;
		case "false":
			this.player.setSuppressed(false);
			break;
		default:
			this.logger.warn("unknown message {}", message);
			break;
		}
	}
}
