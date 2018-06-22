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

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.Alarm;

/**
 * A timestamp
 * 
 * @author Christian Bräuner
 */
@XmlRootElement
public class Timestamp {
	
	/**
	 * the hour of the timestamp
	 */
	private int hour = 0;
	
	/**
	 * the minute of the timestamp
	 */
	private int minute = 0;
	
	private String link = "";
	
	/**
	 * constructor for a timestamp without set values
	 */
	public Timestamp() {
		// needed for JSON
	}
	
	/**
	 * creates a timestamp from a string
	 * 
	 * @param time the time in format hh:mm
	 * 
	 * @throws IllegalArgumentException parameter format wrong
	 */
	public Timestamp(String time) throws IllegalArgumentException {
		try {
			String[] timeSplit = time.split(":");
			this.hour = Integer.parseInt(timeSplit[0]);
			this.minute = Integer.parseInt(timeSplit[1]);
		} catch (Exception e) {
			throw new IllegalArgumentException(time);
		}
	}
	
	/**
	 * creates an initiallized timestamp
	 * 
	 * @param hour the hour of the new timestamp
	 * @param minute the minute of the new timestamp
	 */
	public Timestamp(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}
	
	public Timestamp(Alarm alarm) {
		if(alarm == null) {
			throw new IllegalArgumentException();
		}
		Calendar date = alarm.getAlarmDate();
		this.hour = date.get(Calendar.HOUR_OF_DAY);
		this.minute = date.get(Calendar.MINUTE);
	}
	
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String sh = String.valueOf(this.hour);
		String sm = String.valueOf(this.minute);
		if(this.hour < 10) {
			sh = "0" + sh;
		}
		if(this.minute < 10) {
			sm = "0" + sm;
		}
		return sh + ":" + sm;
	}
	
	@XmlTransient
	public boolean isValid() {
		return ((this.hour>=0 && this.hour < 24) && (this.minute >= 0 && this.minute < 60));
	}

	/**
	 * @return the hour
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * @param hour the hour to set
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * @return the minute
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * @param minute the minute to set
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	/**
	 * @see java.lang.object#equals
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Timestamp) {
			Timestamp ts = (Timestamp) obj;
			return this.minute == ts.minute && this.hour == ts.hour;
		}
		return false;
	}
}
