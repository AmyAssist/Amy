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

package io.github.amyassist.amy.messagehub;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Implementation of {@link Message}.
 *
 * @author Tim Neumann
 */
public class MessageImpl implements Message {
	private final String data;
	private final int qos;
	private final boolean duplicate;
	private final boolean retained;

	/**
	 * Creates a new message from the given mqtt message
	 *
	 * @param msg
	 *            the mqtt message
	 */
	protected MessageImpl(MqttMessage msg) {
		this.data = new String(msg.getPayload(), StandardCharsets.UTF_8);
		this.qos = msg.getQos();
		this.duplicate = msg.isDuplicate();
		this.retained = msg.isRetained();
	}

	/**
	 * @see io.github.amyassist.amy.messagehub.Message#getPayload()
	 */
	@Override
	public String getPayload() {
		return this.data;
	}

	/**
	 * @see io.github.amyassist.amy.messagehub.Message#getQualityOfService()
	 */
	@Override
	public int getQualityOfService() {
		return this.qos;
	}

	/**
	 * @see io.github.amyassist.amy.messagehub.Message#mayBeDuplicate()
	 */
	@Override
	public boolean mayBeDuplicate() {
		return this.duplicate;
	}

	/**
	 * @see io.github.amyassist.amy.messagehub.Message#wasRetained()
	 */
	@Override
	public boolean wasRetained() {
		return this.retained;
	}
}
