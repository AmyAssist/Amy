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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

/**
 * A LocalDateTime provider to return LocalDateTimeParameterConverter according to ISO-8601
 * 
 * @author Leon Kiefer, Muhammed Kaya
 */
public class ZonedDateTimeProvider implements ParamConverterProvider {

	/**
	 * @see javax.ws.rs.ext.ParamConverterProvider#getConverter(java.lang.Class, java.lang.reflect.Type,
	 *      java.lang.annotation.Annotation[])
	 */
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if (rawType.equals(ZonedDateTime.class)) {
			return (ParamConverter<T>) new ZonedDateTimeParameterConverter();
		}
		return null;
	}

}
