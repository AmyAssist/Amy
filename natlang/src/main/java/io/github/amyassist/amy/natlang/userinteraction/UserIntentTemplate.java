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

package io.github.amyassist.amy.natlang.userinteraction;

import java.lang.reflect.Method;

import io.github.amyassist.amy.natlang.aim.XMLAIMIntent;

/**
 * Helper Template class
 * 
 * @author Felix Burk
 */
public class UserIntentTemplate {
	
	private final Method method;
	private final XMLAIMIntent xml;
	
	/**
	 * constructor
	 * @param method to use
	 * @param xml to use
	 */
	public UserIntentTemplate(Method method, XMLAIMIntent xml) {
		this.method = method;
		this.xml = xml;
	}

	/**
	 * Get's {@link #method method}
	 * @return  method
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * Get's {@link #xml xml}
	 * @return  xml
	 */
	public XMLAIMIntent getXml() {
		return this.xml;
	}
	
	
}
