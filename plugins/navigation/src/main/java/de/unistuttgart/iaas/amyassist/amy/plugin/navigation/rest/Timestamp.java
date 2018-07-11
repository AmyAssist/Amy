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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.adapter.ZonedDateTimeAdapter;

/**
 * Timestamp class serves as entity for time and date
 * 
 * @author Muhammed Kaya
 */
@XmlRootElement
public class Timestamp extends Entity {
	
	@XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
	private ZonedDateTime dateTime;

	/**
	 * Get's {@link #dateTime dateTime}
	 * @return  dateTime
	 */
	public ZonedDateTime getDateTime() {
		return this.dateTime;
	}

	/**
	 * Set's {@link #dateTime dateTime}
	 * @param dateTime  dateTime
	 */
	public void setDateTime(ZonedDateTime dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * the year of the new timestamp
	 */
	private int year;
	
	/**
	 * the month of the new timestamp
	 */
	private int month;
	
	/**
	 * the day of the new timestamp
	 */
	private int day;
	
	/**
	 * the hour of the new timestamp
	 */
	private int hour;
	
	/**
	 * the minute of the new timestamp
	 */
	private int minute;
	
	/**
	 * the second of the new timestamp
	 */
	private int second;

	/**
	 * constructor
	 */
	public Timestamp() {
		// Needed for JSON
	}

	/**
	 * @param year
	 *            the year of the new timestamp
	 * @param month
	 *            the month of the new timestamp
	 * @param day
	 *            the day of the new timestamp
	 * @param hour
	 *            the hour of the new timestamp
	 * @param minute
	 *            the minute of the new timestamp
	 * @param second
	 *            the second of the new timestamp
	 */
	public Timestamp(int year, int month, int day, int hour, int minute, int second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	/**
	 * @return year
	 */
	public int getYear() {
		return this.year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return month
	 */
	public int getMonth() {
		return this.month;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @return day
	 */
	public int getDay() {
		return this.day;
	}

	/**
	 * @param day
	 *            the day to set
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * @return hour
	 */
	public int getHour() {
		return this.hour;
	}

	/**
	 * @param hour
	 *            the hour to set
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * @return minute
	 */
	public int getMinute() {
		return this.minute;
	}

	/**
	 * @param minute
	 *            the minute to set
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}

	/**
	 * @return second
	 */
	public int getSecond() {
		return this.second;
	}

	/**
	 * @param second
	 *            the second to set
	 */
	public void setSecond(int second) {
		this.second = second;
	}
	
	/**
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Timestamp) {
			Timestamp timestamp = (Timestamp) obj;
			return this.year == timestamp.year && this.month == timestamp.month && this.day == timestamp.day
					&& this.hour == timestamp.hour && this.minute == timestamp.minute
					&& this.second == timestamp.second;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
