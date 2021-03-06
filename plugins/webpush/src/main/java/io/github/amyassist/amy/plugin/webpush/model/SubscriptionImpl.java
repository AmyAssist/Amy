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

package io.github.amyassist.amy.plugin.webpush.model;

/**
 * Implementation of Subscription.
 * 
 * @author Leon Kiefer
 */
public class SubscriptionImpl implements Subscription {

	private String endpoint;
	private String auth;
	private String key;

	public SubscriptionImpl(String endpoint, String auth, String key) {
		this.endpoint = endpoint;
		this.auth = auth;
		this.key = key;
	}

	@Override
	public void setAuth(String auth) {
		this.auth = auth;
	}

	@Override
	public String getAuth() {
		return this.auth;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getEndpoint() {
		return this.endpoint;
	}

	@Override
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
}
