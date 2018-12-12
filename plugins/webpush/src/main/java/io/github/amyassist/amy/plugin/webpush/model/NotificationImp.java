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

import java.util.List;

/**
 * Implementation of the Notification model.
 * 
 * @author Leon Kiefer
 */
public class NotificationImp implements Notification {
	private final String title;
	private final String body;
	private final String icon;
	private final int[] vibrate;
	private final String data;
	private final List<Action> actions;

	/**
	 * @param title
	 *            the title of the notification
	 * @param body
	 *            text body of the notification
	 * @param icon
	 *            the url of the icon to display
	 * @param vibrate
	 * @param data
	 *            custom data
	 * @param actions
	 *            possible user actions for this notification
	 */
	public NotificationImp(String title, String body, String icon, int[] vibrate, String data, List<Action> actions) {
		this.title = title;
		this.body = body;
		this.icon = icon;
		this.vibrate = vibrate;
		this.data = data;
		this.actions = actions;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getBody() {
		return this.body;
	}

	@Override
	public String getIcon() {
		return this.icon;
	}

	@Override
	public int[] getVibrate() {
		return this.vibrate;
	}

	@Override
	public String getData() {
		return this.data;
	}

	@Override
	public List<Action> getActions() {
		return this.actions;
	}

	public static class ActionImpl implements Action {
		private final String action;
		private final String title;

		public ActionImpl(String action, String title) {
			this.action = action;
			this.title = title;
		}

		@Override
		public String getAction() {
			return this.action;
		}

		@Override
		public String getTitle() {
			return this.title;
		}

	}

}
