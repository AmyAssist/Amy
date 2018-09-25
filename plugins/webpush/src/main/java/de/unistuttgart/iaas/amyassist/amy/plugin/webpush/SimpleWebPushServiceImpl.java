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

package de.unistuttgart.iaas.amyassist.amy.plugin.webpush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.plugin.webpush.model.Notification;

/**
 * Implementation of the simple facade.
 * 
 * @author Leon Kiefer
 */
@Service
public class SimpleWebPushServiceImpl implements SimpleWebPushService {
	@Reference
	private WebPushService webPushService;

	@Reference
	private WebPushNameService webPushNameService;

	@Override
	public void sendPushNotification(int id, Notification notification) {
		this.webPushService.sendPushNotification(id, this.marshallNotification(notification));
	}

	@Override
	public void sendPushNotification(String name, Notification notification) {
		this.webPushNameService.sendPushNotification(name, this.marshallNotification(notification));
	}

	private byte[] marshallNotification(Notification notification) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsBytes(new Payload(notification));
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private class Payload {
		public Notification notification;

		public Payload(Notification notification) {
			this.notification = notification;
		}
	}
}
