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

import java.time.LocalTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.adapter.LocalTimeAdapter;

/**
 * Class that defines timer attributes and behaviour
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer, Leon Kiefer
 */
public class Alarm {

	private int id;
	@XmlJavaTypeAdapter(LocalTimeAdapter.class)
	private LocalTime alarmTime;
	private boolean active;

	public Alarm() {

	}

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

		setTime(hour, minute);

		this.active = active;
	}

	/**
	 * Returns a string representation of this object
	 * 
	 * @see java.lang.Object#toString()
	 * 
	 * @return String representation of this object
	 */
	@Override
	public String toString() {
		return this.id + ":" + this.alarmTime.getHour() + ":" + this.alarmTime.getMinute() + ":" + this.active;
	}

	/**
	 * Construct an alarm object from the String that was made by the convertToString method
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
	 * @param hour
	 *            hour of the alarm
	 * @param minute
	 *            minute of the alarm
	 */
	public final void setTime(int hour, int minute) {
		if (timeValid(hour, minute)) {
			this.alarmTime = LocalTime.of(hour, minute);
		} else {
			throw new IllegalArgumentException();
		}

	}

	/**
	 * Checks if the given hour and minute are valid for an alarm
	 * 
	 * @param hour
	 *            hour of the alarm
	 * @param minute
	 *            minute of the alarm
	 * @return true, if hour and minute are valid, else false
	 */
	public static boolean timeValid(int hour, int minute) {
		return hour <= 23 && hour >= 0 && minute <= 59 && minute >= 0;

	}

	/**
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            alarm id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return alarmDate
	 */
	public LocalTime getAlarmTime() {
		return this.alarmTime;
	}

	/**
	 * @return active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active
	 *            alarm active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
