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

package io.github.amyassist.amy.restresources.chat;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.natlang.Response;

/**
 * Helper service for the chat inside the webapp
 * 
 * @author Felix Burk
 */
@Service()
public class ChatService {

	/**
	 * maps uuids from users to a LinkedList<String> containing answers from amy
	 */
	private ConcurrentMap<UUID, LinkedList<Response>> userQueueMap = new ConcurrentHashMap<>();

	/**
	 * retrieve queue from user with uuid
	 * 
	 * @param uuid
	 *            of user
	 * @return the queue of answers from amy
	 * @throws NoSuchElementException
	 *             if there is no queue for the given uuid
	 */
	public Queue<Response> getQueue(UUID uuid) {
		if (this.userQueueMap.containsKey(uuid)) {
			return this.userQueueMap.get(uuid);
		}
		throw new NoSuchElementException("There is no queue with id " + uuid);
	}

	/**
	 * adds a new user
	 * 
	 * @param uuid
	 *            string representation of uuid
	 */
	public void addUser(UUID uuid) {
		this.userQueueMap.put(uuid, new LinkedList<Response>());
	}

}
