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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.plugin.webpush.model.Subscription;
import io.github.amyassist.amy.plugin.webpush.persistence.SubscriptionEntity;
import io.github.amyassist.amy.plugin.webpush.persistence.SubscriptionStorage;

/**
 * Implementation of the WebPushNameService. This uses Tags of the Database entity as names.
 * 
 * @author Leon Kiefer
 */
@Service
public class WebPushNameServiceImpl implements WebPushNameService {

	@Reference
	private Logger logger;

	@Reference
	private SubscriptionStorage subscriptionStorage;

	@Reference
	private WebPushService webPushService;

	@Override
	public void sendPushNotification(String name, byte[] payload) {
		for (int id : this.getIDs(name)) {
			this.webPushService.sendPushNotification(id, payload);
		}
	}

	@Override
	public int subscribe(Subscription subscription, String name) {
		int subscribe = this.webPushService.subscribe(subscription);
		this.setName(subscribe, name);
		return subscribe;
	}

	@Override
	public String getName(int id) {
		return this.subscriptionStorage.getById(id).getTag();
	}

	@Override
	public List<Integer> getIDs(String name) {
		return this.subscriptionStorage.getEntitiesWithTag(name).stream().map(SubscriptionEntity::getPersistentId)
				.collect(Collectors.toList());
	}

	@Override
	public List<Integer> getAllIDs() {
		return this.subscriptionStorage.getAll().stream().map(SubscriptionEntity::getPersistentId)
				.collect(Collectors.toList());
	}

	@Override
	public void setName(int id, String name) {
		SubscriptionEntity entity = this.subscriptionStorage.getById(id);
		if (entity == null) {
			throw new NoSuchElementException("No Subscription with id: " + id);
		}
		entity.setTag(name);
		this.subscriptionStorage.save(entity);
	}

}
