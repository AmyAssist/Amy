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

package io.github.amyassist.amy.plugin.spotify;

/**
 * Stores user specific content for example the refresh token or current selected device
 * @author Lars Buttgereit
 */
public class User {

	private int id;
	private String refreshToken;
	private String accessToken;
	private long accessTokenExpireTime = Long.MIN_VALUE;
	private String currentDeviceId;
	/**
	 * Get's {@link #id id}
	 * @return  id
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * Set's {@link #id id}
	 * @param id  id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * Get's {@link #refreshToken refreshToken}
	 * @return  refreshToken
	 */
	public String getRefreshToken() {
		return this.refreshToken;
	}
	/**
	 * Set's {@link #refreshToken refreshToken}
	 * @param refreshToken  refreshToken
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	/**
	 * Get's {@link #accessToken accessToken}
	 * @return  accessToken
	 */
	public String getAccessToken() {
		return this.accessToken;
	}
	/**
	 * Set's {@link #accessToken accessToken}
	 * @param accessToken  accessToken
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	/**
	 * Get's {@link #accessTokenExpireTime accessTokenExpireTime}
	 * @return  accessTokenExpireTime
	 */
	public long getAccessTokenExpireTime() {
		return this.accessTokenExpireTime;
	}
	/**
	 * Set's {@link #accessTokenExpireTime accessTokenExpireTime}
	 * @param accessTokenExpireTime  accessTokenExpireTime
	 */
	public void setAccessTokenExpireTime(long accessTokenExpireTime) {
		this.accessTokenExpireTime = accessTokenExpireTime;
	}
	/**
	 * Get's {@link #currentDeviceId currentDeviceId}
	 * @return  currentDeviceId
	 */
	public String getCurrentDeviceId() {
		return this.currentDeviceId;
	}
	/**
	 * Set's {@link #currentDeviceId currentDeviceId}
	 * @param currentDeviceId  currentDeviceId
	 */
	public void setCurrentDeviceId(String currentDeviceId) {
		this.currentDeviceId = currentDeviceId;
	}
	
	
}
