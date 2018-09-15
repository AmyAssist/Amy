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

package de.unistuttgart.iaas.amyassist.amy.socket;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.DialogHandler;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import org.slf4j.Logger;

import java.io.IOException;

import static de.unistuttgart.iaas.amyassist.amy.socket.ChatConfig.PROPERTY_PORT;

/**
 * the class running the server socket for the chat 
 * 
 * @author Christian Bräuner
 */
@Service(ChatServer.class)
public class ChatServer implements RunnableService {

	@Reference
	ChatConfig config;
	
	@Reference
	private Logger logger;
	
	@Reference
	private DialogHandler handler;
	
	private ChatWebSocket socket;
	
	
	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {

		int port = Integer.parseInt(config.get(PROPERTY_PORT));
		this.socket = new ChatWebSocket(port, this.handler);
		this.socket.start();

	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		try {
			this.socket.stop();
		} catch (IOException e) {
			this.logger.error("Can't close chatserver", e);
		} catch (InterruptedException e) {
			this.logger.error("Interrupt while stoping", e);
			Thread.currentThread().interrupt();
		}
		this.logger.info("ChatServer shutdown");
	}

}
