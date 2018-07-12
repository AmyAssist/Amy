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

package de.unistuttgart.iaas.amyassist.amy.utility.rest.adapter;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *  An adapter for ZonedDateTime objects to parse and get Strings according to ISO-8601
 * 
 * @author Leon Kiefer, Muhammed Kaya
 */
public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(ZonedDateTime v) throws Exception {
		return v.toString();
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public ZonedDateTime unmarshal(String v) throws Exception {
		return ZonedDateTime.parse(v);
	}

}