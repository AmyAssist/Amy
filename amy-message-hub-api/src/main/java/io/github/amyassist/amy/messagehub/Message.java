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
 * A message which was received by the {@link MessageHub}.
 *
 * @author Tim Neumann
 */
public interface Message {
	/**
	 * @return The Payload of this message
	 */
	String getPayload();

	/**
	 * @return The quality of service(0-2) of this message.
	 * @see <a href="https://en.wikipedia.org/wiki/MQTT#Quality_of_service_(QoS)"> quality of service </a>
	 */
	int getQualityOfService();

	/**
	 * @return Whether this message may be a duplicate message
	 */
	boolean mayBeDuplicate();

	/**
	 * @return Whether the message was retained in the Broker, an may therefore not be "fresh".
	 */
	boolean wasRetained();
}
