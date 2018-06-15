/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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

package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * The Logic for the system time
 * 
 * @author Florian Bauer, Patrick Gebhardt
 */
@Service
public class SystemTimeLogic {

	/**
	 * 
	 * @return object of type Date
	 */
	public Date getTimeStamp() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 
	 * @return current day of month as String (dd), e.g. 01
	 */
	public String getDay() {
		DateFormat dayFormat = new SimpleDateFormat("dd");
		return dayFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current month of year as String (MM), e.g. 06
	 */
	public String getMonth() {
		DateFormat monthFormat = new SimpleDateFormat("MM");
		return monthFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current year as String (yyyy), e.g. 2018
	 */
	public String getYear() {
		DateFormat yearFormat = new SimpleDateFormat("yyyy");
		return yearFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current date as String (dd MM yy), e.g. 01 06 18
	 */
	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd MM yy");
		return dateFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current hour as String (HH), e.g. 12
	 */
	public String getHour() {
		DateFormat hourFormat = new SimpleDateFormat("HH");
		return hourFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current minute as String (mm), e.g. 45
	 */
	public String getMinute() {
		DateFormat minuteFormat = new SimpleDateFormat("mm");
		return minuteFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current second as String (ss), e.g. 20
	 */
	public String getSecond() {
		DateFormat secondFormat = new SimpleDateFormat("ss");
		return secondFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current time as String (HH mm ss), e.g. 12 45 20
	 */
	public String getTime() {
		DateFormat timeFormat = new SimpleDateFormat("HH mm ss");
		return timeFormat.format(this.getTimeStamp()).toString();
	}
}
