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

package de.unistuttgart.iaas.amyassist.amy.httpserver.adapter;

import java.time.ZonedDateTime;

import javax.ws.rs.ext.ParamConverter;

/**
 * A ZonedDateTime parameter converter to parse and get Strings according to ISO-8601
 * 
 * @author Leon Kiefer, Muhammed Kaya
 */
public class ZonedDateTimeParameterConverter implements ParamConverter<ZonedDateTime> {

	/**
	 * @see javax.ws.rs.ext.ParamConverter#fromString(java.lang.String)
	 */
	@Override
	public ZonedDateTime fromString(String value) {
		return ZonedDateTime.parse(value);
	}

	/**
	 * @see javax.ws.rs.ext.ParamConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(ZonedDateTime value) {
		return value.toString();
	}

}
