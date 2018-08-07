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

package de.unistuttgart.iaas.amyassist.amy.messagehub;

/**
 * This is a internal used representation of a Message. It contains the topic and the data of a message.
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the type of the message payload
 */
public class Message<T> {
	private final String topic;
	private final T data;

	/**
	 * @param topic
	 *            the topic the message was published
	 * @param data
	 *            the payload of the message
	 */
	public Message(String topic, T data) {
		this.topic = topic;
		this.data = data;
	}

	/**
	 * Get the topic.
	 * 
	 * @return the topic of the message
	 */
	public String getTopic() {
		return this.topic;
	}

	/**
	 * Get the data.
	 * 
	 * @return the payload of the message
	 */
	public T getData() {
		return this.data;
	}
}
