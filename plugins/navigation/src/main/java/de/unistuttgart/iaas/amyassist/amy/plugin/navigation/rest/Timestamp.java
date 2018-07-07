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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Timestamp class serves as entity for time and date
 * 
 * @author Muhammed Kaya
 */
@XmlRootElement
public class Timestamp {

	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	public Timestamp() {
		
	}
	
	/**
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
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
	 * Get's {@link #year year}
	 * @return  year
	 */
	public int getYear() {
		return this.year;
	}

	/**
	 * Set's {@link #year year}
	 * @param year  year
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Get's {@link #month month}
	 * @return  month
	 */
	public int getMonth() {
		return this.month;
	}

	/**
	 * Set's {@link #month month}
	 * @param month  month
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * Get's {@link #day day}
	 * @return  day
	 */
	public int getDay() {
		return this.day;
	}

	/**
	 * Set's {@link #day day}
	 * @param day  day
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * Get's {@link #hour hour}
	 * @return  hour
	 */
	public int getHour() {
		return this.hour;
	}

	/**
	 * Set's {@link #hour hour}
	 * @param hour  hour
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * Get's {@link #minute minute}
	 * @return  minute
	 */
	public int getMinute() {
		return this.minute;
	}

	/**
	 * Set's {@link #minute minute}
	 * @param minute  minute
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}

	/**
	 * Get's {@link #second second}
	 * @return  second
	 */
	public int getSecond() {
		return this.second;
	}

	/**
	 * Set's {@link #second second}
	 * @param second  second
	 */
	public void setSecond(int second) {
		this.second = second;
	}
	
}
