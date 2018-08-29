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

import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * Speech class for alarm clock
 *
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@SpeechCommand
public class AlarmClockSpeech {

	@Reference
	private AlarmClockLogic logic;

	@Reference
	private Logger logger;

	private static final String ELEMENTNOTFOUND = "Element not found";
	private static final String PARAMSNOTVALID = "Parameters not valid.";

	private static final String ALARMS = "Alarm ";
	private static final String TOMORROW = "tomorrow";

	private static final String TIME_MAP_KEY = "time";
	private static final String NUMBER_MAP_KEY = "number";
	private static final String DAY_MAP_KEY = "day";
	private static final String TYPE_MAP_KEY = "type";

	private static final String REGEX_ALARM = "(alarm|alarms)";

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
			Alarm alarm = this.logic.setAlarm(tomoro, entities.get(TIME_MAP_KEY).getTime().getHour(),
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
	 * Sets a new timer. You can select between hours, minutes and seconds or combinate them
	 * 
	 * @param entities
	 *            contains the data input
	 * @return params for the timer
	 */
	@Intent
	public String setTimer(Map<String, EntityData> entities) {
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		if (entities.get("hour") != null) {
			hours = entities.get("hour").getNumber();
		}
		if (entities.get("minute") != null) {
			minutes = entities.get("minute").getNumber();
		}
		if (entities.get("second") != null) {
			seconds = entities.get("second").getNumber();
		}
		if (hours == 0 && minutes == 0 && seconds == 0) {
			return "No value is set";
		}
		Timer timer = this.logic.setTimer(hours, minutes, seconds);
		return "Timer " + timer.getId() + " set";
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
		if (entities.get(TYPE_MAP_KEY).getString().matches(REGEX_ALARM))
			return this.logic.resetAlarms();
		return this.logic.resetTimers();
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
			if (entities.get(TYPE_MAP_KEY).getString().matches(REGEX_ALARM))
				return this.logic.deleteAlarm(entities.get(NUMBER_MAP_KEY).getNumber());
			return this.logic.deleteTimer(entities.get(NUMBER_MAP_KEY).getNumber());
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
			if (entities.get(TYPE_MAP_KEY).getString().matches(REGEX_ALARM))
				return this.logic.deactivateAlarm(entities.get(NUMBER_MAP_KEY).getNumber());
			this.logic.deleteTimer(entities.get(NUMBER_MAP_KEY).getNumber());
			return "Timer " + entities.get(NUMBER_MAP_KEY).getNumber() + " deactivated";
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
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
			return this.logic.activateAlarm(entities.get(NUMBER_MAP_KEY).getNumber());
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
			if (entities.get(TYPE_MAP_KEY).getString().matches(REGEX_ALARM)) {
				Alarm alarm = this.logic.getAlarm(entities.get(NUMBER_MAP_KEY).getNumber());
				return outputAlarm(alarm);
			}
			Timer timer = this.logic.getTimer(entities.get(NUMBER_MAP_KEY).getNumber());
			return outputTimer(timer);
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
		if (entities.get(TYPE_MAP_KEY).getString().matches(REGEX_ALARM)) {
			List<Alarm> alarms = this.logic.getAllAlarms();
			if (alarms.isEmpty()) {
				return "No alarms found";
			}
			String[] stringAlarms = new String[alarms.size()];
			for (int i = 0; i < alarms.size(); i++) {
				stringAlarms[i] = outputAlarm(alarms.get(i));
			}
			return String.join("\n", stringAlarms);
		}
		List<Timer> timers = this.logic.getAllTimers();
		if (timers.isEmpty()) {
			return "No timers found";
		}
		String[] stringTimers = new String[timers.size()];
		for (int i = 0; i < timers.size(); i++) {
			stringTimers[i] = outputTimer(timers.get(i));
		}
		return String.join("\n", stringTimers);
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
			return outputAlarm(this.logic.editAlarm(entities.get(NUMBER_MAP_KEY).getNumber(), day,
					entities.get(TIME_MAP_KEY).getTime().getHour(), entities.get(TIME_MAP_KEY).getTime().getMinute()));
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * Gets remaining timer delay
	 * 
	 * @param entities
	 *            contains the data input
	 * @return remaining timer delay
	 */
	@Intent
	public String getRemainingTimerDelay(Map<String, EntityData> entities) {
		try {
			return outputTimer(this.logic.getTimer(entities.get(NUMBER_MAP_KEY).getNumber()));
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

	private static String outputTimer(Timer timer) {
		int[] timeDiff = timer.getRemainingTime();
		int timerNumber = timer.getId();
		if (timeDiff[0] < 0 || timeDiff[1] < 0 || timeDiff[2] < 0) {
			return "Timer " + timerNumber + " is ringing right now.";
		}
		if (timeDiff[0] == 0) {
			if (timeDiff[1] == 0) {
				return "Timer " + timerNumber + " will ring in " + timeDiff[2] + " seconds";
			}
			return "Timer " + timerNumber + " will ring in " + timeDiff[1] + " minutes and " + timeDiff[2] + " seconds";
		}
		return "Timer " + timerNumber + " will ring in " + timeDiff[0] + " hours and " + timeDiff[1] + " minutes and "
				+ timeDiff[2] + " seconds";
	}

}
