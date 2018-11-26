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

package io.github.amyassist.amy.plugin.alarmclock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.natlang.EntityData;
import io.github.amyassist.amy.core.natlang.Intent;
import io.github.amyassist.amy.core.natlang.SpeechCommand;

/**
 * Speech class for alarm clock
 *
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@SpeechCommand
public class AlarmClockSpeech {

	@Reference
	private AlarmClockLogic alarmLogic;

	@Reference
	private Logger logger;

	private static final String ELEMENTNOTFOUND = "Element not found";
	private static final String PARAMSNOTVALID = "Parameters not valid.";

	private static final String ALARMS = "Alarm ";
	private static final String TOMORROW = "tomorrow";

	private static final String TIME_MAP_KEY = "time";
	private static final String NUMBER_MAP_KEY = "number";
	private static final String DAY_MAP_KEY = "day";

	/**
	 * Creates new alarm
	 * 
	 * @param entities
	 *            contains the data input
	 * 
	 * @return info if creation was successful
	 */
	@Intent
	public String setAlarm(Map<String, EntityData> entities) {
		try {
			int tomoro = -1;
			if (entities.get(DAY_MAP_KEY) != null && entities.get(DAY_MAP_KEY).getString().equals(TOMORROW)) {
				tomoro = 1;
			}
			Alarm alarm = this.alarmLogic.setAlarm(tomoro, entities.get(TIME_MAP_KEY).getTime().getHour(),
					entities.get(TIME_MAP_KEY).getTime().getMinute());
			LocalDateTime time = alarm.getAlarmTime();
			String day;
			if (LocalDateTime.now().getDayOfMonth() == time.getDayOfMonth()) {
				day = "today";
			} else {
				day = TOMORROW;
			}
			return ALARMS + alarm.getId() + " set for " + time.getHour() + ":" + time.getMinute() + " " + day;
		} catch (IllegalArgumentException e) {
			this.logException(e);
			return PARAMSNOTVALID;
		}
	}

	/**
	 * Resets all alarms or timers
	 * 
	 * @param entities
	 *            contains the data input
	 * @return resetAlarms or resetTimers
	 */
	@Intent
	public String resetAlarmClockObjects(Map<String, EntityData> entities) {
		return this.alarmLogic.resetAlarms();
	}

	/**
	 * Deletes one specific alarm or timer
	 * 
	 * @param entities
	 *            contains the data input
	 * @return deleteAlarm or deleteTimer
	 */
	@Intent
	public String deleteAlarmClockObject(Map<String, EntityData> entities) {
		try {
			return this.alarmLogic.deleteAlarm(entities.get(NUMBER_MAP_KEY).getNumber());
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * deactivates one specific alarm or timer
	 * 
	 * @param entities
	 *            contains the data input
	 * @return deactivateAlarm or deactivateTimer
	 */
	@Intent
	public String deactivateAlarmClockObject(Map<String, EntityData> entities) {
		try {
			return this.alarmLogic.deactivateAlarm(entities.get(NUMBER_MAP_KEY).getNumber());
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * Stops the currently ringing alarm
	 * 
	 * @param entities
	 *            contains the data input
	 * @return if successful
	 */
	@Intent
	public String stopRinging(Map<String, EntityData> entities) {
		return this.alarmLogic.stopRinging();
	}

	/**
	 * Activates one specific alarm or timer
	 * 
	 * @param entities
	 *            contains the data input
	 * @return activateAlarm or activateTimer
	 */
	@Intent
	public String activateAlarm(Map<String, EntityData> entities) {
		try {
			return this.alarmLogic.activateAlarm(entities.get(NUMBER_MAP_KEY).getNumber());
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * gets one specific alarm or timer
	 * 
	 * @param entities
	 *            contains the data input
	 * @return getAlarm or getTimer
	 */
	@Intent
	public String getAlarmClockObject(Map<String, EntityData> entities) {
		try {
			Alarm alarm = this.alarmLogic.getAlarm(entities.get(NUMBER_MAP_KEY).getNumber());
			return outputAlarm(alarm);

		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * gets all alarms or timers
	 * 
	 * @param entities
	 *            contains the data input
	 * @return getAllAlarms or getAllTimers
	 */
	@Intent
	public String getAllAlarmClockObjects(Map<String, EntityData> entities) {
		List<Alarm> alarms = this.alarmLogic.getAllAlarms();
		if (alarms.isEmpty()) {
			return "No alarms found";
		}
		String[] stringAlarms = new String[alarms.size()];
		for (int i = 0; i < alarms.size(); i++) {
			stringAlarms[i] = outputAlarm(alarms.get(i));
		}
		return String.join("\n", stringAlarms);

	}

	/**
	 * edits a specific alarm
	 * 
	 * @param entities
	 *            contains the data input
	 * @return edit Alarm
	 */
	@Intent
	public String editAlarm(Map<String, EntityData> entities) {
		int day = -1;
		if (entities.get(DAY_MAP_KEY) != null && entities.get(DAY_MAP_KEY).getString().equals(TOMORROW)) {
			day = 1;
		}
		try {
			return outputAlarm(this.alarmLogic.editAlarm(entities.get(NUMBER_MAP_KEY).getNumber(), day,
					entities.get(TIME_MAP_KEY).getTime().getHour(), entities.get(TIME_MAP_KEY).getTime().getMinute()));
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	private void logException(Exception e) {
		this.logger.error("Exception Thrown!", e);
	}

	private static String outputAlarm(Alarm alarm) {
		LocalDateTime ringTime = alarm.getAlarmTime();
		String day;
		if (LocalDateTime.now().getDayOfMonth() == ringTime.getDayOfMonth()) {
			day = "today";
		} else {
			day = TOMORROW;
		}
		if (alarm.isActive())
			return ALARMS + alarm.getId() + " will ring at " + ringTime.getHour() + ":" + ringTime.getMinute() + " "
					+ day;

		return ALARMS + alarm.getId() + " is set for " + ringTime.getHour() + ":" + ringTime.getMinute() + " " + day
				+ " but will not ring";
	}

}
