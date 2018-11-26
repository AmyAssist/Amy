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

package io.github.amyassist.amy.plugin.webpush;

import io.github.amyassist.amy.plugin.webpush.model.Notification;

/**
 * Simple abstracted WebPushService interface.
 * 
 * @author Leon Kiefer
 */
public interface SimpleWebPushService {

	/**
	 * Send a Push notification to the single client with the given unique id.
	 * 
	 * @param id
	 *            the id of the client subscription
	 * @param notification
	 *            the notification to send
	 */
	void sendPushNotification(int id, Notification notification);

	/**
	 * Send a Push notification to the clients with the given name. Multiple clients can have the same name.
	 * 
	 * @param name
	 *            the name of the clients subscription
	 * @param notification
	 *            the notification to send
	 */
	void sendPushNotification(String name, Notification notification);

}
