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

package io.github.amyassist.amy.messagehub.internal;

import java.util.Map;

import io.github.amyassist.amy.messagehub.annotations.MessageReceiver;
import io.github.amyassist.amy.messagehub.annotations.Subscription;
import io.github.amyassist.amy.messagehub.topic.TopicName;

/**
 * Message receiver with invalid methods
 * 
 * @author Leon Kiefer
 */
@MessageReceiver
public class TestInvalidMessageReceiver {
	@Subscription("topic")
	private void sub(String message) {

	}

	@Subscription("/top")
	public static void sub(String message, TopicName topic) {

	}

	@Subscription("ex")
	public void ex(String message, TopicName topic) throws Exception {
		throw new Exception();
	}

	@Subscription("/top")
	public String returnString(String message, TopicName topic) {
		return message;
	}

	@Subscription("event")
	public void event() {
	}

	@Subscription("speech")
	public void speech(Map<String, String> values) {
	}

	@Subscription("speech")
	public void twoValues(Map<String, String> values, String s) {
	}

	@Subscription("global")
	public void twoValues(String message, String topic) {
	}
}
