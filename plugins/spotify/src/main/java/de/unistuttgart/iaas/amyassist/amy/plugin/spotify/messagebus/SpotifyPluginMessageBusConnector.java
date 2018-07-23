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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.messagebus;

import de.unistuttgart.iaas.amyassist.amy.messagebus.AbstractBusMessageExecutor;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.logic.PlayerLogic;

/**
 * Class that Manages the publish of the Broker
 * 
 * @author Kai Menzel
 */
public class SpotifyPluginMessageBusConnector extends AbstractBusMessageExecutor {

	/**
	 * 
	 * @param l
	 *            PlayerLogic
	 */
	public SpotifyPluginMessageBusConnector(PlayerLogic l) {
		this.player = l;
	}

	private PlayerLogic player;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.messagebus.AbstractBusMessageExecutor#executeCommand(java.lang.String)
	 */
	@Override
	protected void executeCommand(String message) {
		switch (message) {
		case "Volume_Down":
			this.player.setSRListening(true);
			break;
		case "Volume_Normal":
			this.player.setSRListening(false);
			break;
		default:
			break;
		}

	}

}
