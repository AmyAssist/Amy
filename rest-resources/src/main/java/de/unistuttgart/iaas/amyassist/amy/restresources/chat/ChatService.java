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

package de.unistuttgart.iaas.amyassist.amy.restresources.chat;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * Helper service for the chat inside the webapp
 * @author Felix Burk
 */
@Service()
public class ChatService {
	
	/**
	 * maps uuids from users to a LinkedList<String> containing answers from amy
	 */
	public ConcurrentHashMap<UUID, LinkedList<String>> userQueueMap = new ConcurrentHashMap<>();
	
	/**
	 * retrieve queue from user with uuid
	 * @param uuid of user
	 * @return the queue of answers from amy
	 */
	protected LinkedList<String> getQueue(String uuid) {
		UUID key = UUID.fromString(uuid);
		if(this.userQueueMap.containsKey(key)) {
			return this.userQueueMap.get(key);
		}
		return null;
		
	}
	
	/**
	 * adds a new user
	 * @param uuid string representation of uuid
	 */
	protected void addUser(String uuid) {
		UUID key = UUID.fromString(uuid);
		this.userQueueMap.put(key, new LinkedList<String>());
	}


}
