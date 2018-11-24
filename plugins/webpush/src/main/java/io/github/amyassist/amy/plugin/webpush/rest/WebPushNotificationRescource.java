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

package de.unistuttgart.iaas.amyassist.amy.plugin.webpush.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.webpush.WebPushNameService;
import de.unistuttgart.iaas.amyassist.amy.plugin.webpush.WebPushService;

/**
 * Resource class for sending Push Notifications.
 * 
 * @author Leon Kiefer
 */
@Path("webpush/notification")
public class WebPushNotificationRescource {
	@Reference
	private WebPushService webPushService;

	@Reference
	private WebPushNameService webPushNameService;

	/**
	 * 
	 * @param id
	 *            the id of the subscription
	 * @param json
	 *            the payload of the notification as json encoded object
	 */
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendPushNotification(@PathParam("id") int id, byte[] json) {
		this.webPushService.sendPushNotification(id, json);
	}

	/**
	 * 
	 * @param name
	 *            the name of the subscription
	 * @param json
	 *            the payload of the notification as json encoded object
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendPushNotification(@QueryParam("clientName") String name, byte[] json) {
		this.webPushNameService.sendPushNotification(name, json);
	}
}
