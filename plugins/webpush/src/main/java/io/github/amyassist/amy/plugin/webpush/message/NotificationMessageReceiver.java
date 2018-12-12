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

package io.github.amyassist.amy.plugin.webpush.message;

import java.util.Collections;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.messagehub.annotations.MessageReceiver;
import io.github.amyassist.amy.messagehub.annotations.Subscription;
import io.github.amyassist.amy.messagehub.topic.TopicName;
import io.github.amyassist.amy.messagehub.topics.SystemTopics;
import io.github.amyassist.amy.plugin.webpush.SimpleWebPushService;
import io.github.amyassist.amy.plugin.webpush.WebPushNameService;
import io.github.amyassist.amy.plugin.webpush.model.Notification;
import io.github.amyassist.amy.plugin.webpush.model.NotificationImp;

/**
 * Send Push notification for messages on the user notification topic
 * 
 * @author Leon Kiefer
 */
@MessageReceiver
public class NotificationMessageReceiver {

	@Reference
	private SimpleWebPushService simpleWebPushService;

	@Reference
	private WebPushNameService webPushNameService;

	@Subscription(SystemTopics.USER + "/+/notification")
	public void notifyUser(String message, TopicName topic) {
		String userName = topic.getTopicLevels().get(1).getStringRepresentation();

		Notification notification = new NotificationImp("Amy Message Notification", message,
				"assets/icons/icon-512x512.png", new int[] { 100, 50, 100 }, "", Collections.emptyList());
		if (userName.equals("all")) {
			for (int id : this.webPushNameService.getAllIDs()) {
				this.simpleWebPushService.sendPushNotification(id, notification);
			}
		} else {
			this.simpleWebPushService.sendPushNotification(userName, notification);
		}
	}
}
