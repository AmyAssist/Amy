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

import java.util.NoSuchElementException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.plugin.webpush.WebPushNameService;
import de.unistuttgart.iaas.amyassist.amy.plugin.webpush.WebPushService;
import de.unistuttgart.iaas.amyassist.amy.plugin.webpush.model.SubscriptionImpl;

/**
 * Resource class to setup Subscriptions.
 * 
 * @author Leon Kiefer
 */
@Path("webpush/subscription")
public class WebPushSubscriptionRescource {
	@Reference
	private WebPushService webPushService;

	@Reference
	private WebPushNameService webPushNameService;

	/**
	 * 
	 * @return Base64 URL encoded public VAPID key
	 */
	@GET
	@Path("key")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPublicVAPIDKey() {
		return this.webPushService.getPublicVAPIDKey();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public int subscribe(@QueryParam("clientName") String name, Subscription subscription) {
		return this.webPushNameService.subscribe(new SubscriptionImpl(subscription.endpoint.toString(),
				subscription.keys.auth, subscription.keys.p256dh), name);
	}

	@DELETE
	@Path("{id}")
	public void unsubscribe(@PathParam("id") int id) {
		try {
			this.webPushService.unsubscribe(id);
		} catch (NoSuchElementException e) {
			throw new WebApplicationException(e, Status.NOT_FOUND);
		}
	}

	@POST
	@Path("{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	public void setName(@PathParam("id") int id, String name) {
		try {
			this.webPushNameService.setName(id, name);
		} catch (NoSuchElementException e) {
			throw new WebApplicationException(e, Status.NOT_FOUND);
		}
	}
}
