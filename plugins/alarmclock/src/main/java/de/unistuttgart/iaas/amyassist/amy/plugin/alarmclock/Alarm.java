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

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.util.Calendar;

/**
 * Class that defines timer attributes and behaviour
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
public class Alarm {

	private int id;
	private Calendar alarmDate;
	private boolean active;

	/**
	 * Constructor for the alarm. Every alarm is initially set active
	 * 
	 * @param id
	 *            the alarmcounter
	 * @param hour
	 *            hour of the alarm
	 * @param minute
	 *            minute of the alarm
	 * @param active
	 *            alarm active
	 */
	public Alarm(int id, int hour, int minute, boolean active) {
		if (id < 0)
			throw new IllegalArgumentException();

		this.id = id;

		setTime(new int[] { hour, minute });

		this.active = active;
	}

	/**
	 * Returns a string representation of this object
	 * 
	 * @see java.lang.Object#toString()
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return this.id + ":" + this.alarmDate.get(Calendar.HOUR_OF_DAY) + ":" + this.alarmDate.get(Calendar.MINUTE)
				+ ":" + this.active;
	}

	/**
	 * Construct an alarm object from the String that was made by the
	 * convertToString method
	 * 
	 * @param input
	 *            the String made by convertToString method
	 * @return the corresponding alarm object
	 */
	public static Alarm reconstructObject(String input) {
		String[] params = input.split(":");
		if (params.length == 4)
			return new Alarm(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]),
					Boolean.parseBoolean(params[3]));
		throw new IllegalArgumentException();
	}

	/**
	 * Give the alarm a new time
	 * 
	 * @param newTime
	 *            new alarm time containing hour and minute
	 */
	public void setTime(int[] newTime) {
		if (newTime.length == 2) {
			Calendar date = Calendar.getInstance();
			date.set(Calendar.HOUR_OF_DAY, newTime[0]);
			date.set(Calendar.MINUTE, newTime[1]);
			date.set(Calendar.SECOND, 0);
			if (date.before(Calendar.getInstance()))
				date.add(Calendar.DATE, 1);

			this.alarmDate = date;
		} else {
			throw new IllegalArgumentException();
		}

	}

	/**
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return alarmDate
	 */
	public Calendar getAlarmDate() {
		return this.alarmDate;
	}

	/**
	 * @return active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}