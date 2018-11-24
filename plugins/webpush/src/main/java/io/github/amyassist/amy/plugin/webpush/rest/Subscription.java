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

import java.net.URI;

/**
 * The PushSubscription from the javascript Push API https://developer.mozilla.org/en-US/docs/Web/API/PushSubscription.
 * This is a DTO class.
 * 
 * @author Leon Kiefer
 */
public class Subscription {
	public URI endpoint;
	public String expirationTime;
	public Keys keys;

	public class Keys {
		/**
		 * Base64 URL encoded String
		 */
		public String p256dh;
		/**
		 * Base64 URL encoded String
		 */
		public String auth;
	}
}
