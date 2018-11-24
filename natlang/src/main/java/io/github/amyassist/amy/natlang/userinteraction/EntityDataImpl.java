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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import io.github.amyassist.amy.core.natlang.EntityData;
import io.github.amyassist.amy.natlang.languagespecifics.DateTimeUtility;

/**
 * Stores the data from one entity. Only one attribute is set at the same time
 * 
 * @author Lars Buttgereit
 */
public class EntityDataImpl implements EntityData {

	private String string;
	private DateTimeUtility timeUtility;

	/**
	 * constructor
	 * 
	 * @param string
	 *            content
	 * @param timeUtility
	 *            time converter
	 */
	public EntityDataImpl(String string, DateTimeUtility timeUtility) {
		this.string = string;
		this.timeUtility = timeUtility;
	}

	@Override
	public int getNumber() {
		try {
			return Integer.parseInt(this.string);
		} catch (NumberFormatException e) {

			return Integer.MIN_VALUE;
		}
	}

	@Override
	public String getString() {
		return this.string;
	}

	@Override
	public LocalTime getTime() {
		return this.timeUtility.parseTime(this.string);
	}

	@Override
	public LocalDate getDate() {
		return this.timeUtility.parseDate(this.string);
	}

	@Override
	public LocalDateTime getDateTime() {
		return this.timeUtility.parseDateTime(this.string);
	}

}
