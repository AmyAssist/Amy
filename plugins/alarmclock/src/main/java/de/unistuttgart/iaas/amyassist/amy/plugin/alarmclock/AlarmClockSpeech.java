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
	private static final String SETALARMGRAMMAR = "(set|create) alarm (at|for|on)" + "( ( # x # )" + "| ( # oh # )"
			+ "| ( (#|quarter|half) (past|to) # )" + "| ( # oh clock ) )" + "[am|pm]" + "[today|tomorrow]";

	private static final int MINUTESINHOUR = 60;
	private static final int MINUTESQUARTERHOUR = 15;
	private static String alarmS = "Alarm ";
	private static String tomorrow = "tomorrow";

	/**
	 * Creates new alarm
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * 
	 * @return info if creation was successful
	 */
	@Intent
	public String setAlarm(Map<String, EntityData> entities) {
		try {
			int tomoro = -1;
			if (entities.get("day") != null && entities.get("day").getString().equals(tomorrow)) {
				tomoro = 1;
			}
			Alarm alarm = this.logic.setAlarm(tomoro, entities.get("time").getTime().getHour(),
					entities.get("time").getTime().getMinute());
			LocalDateTime time = alarm.getAlarmTime();
			String day;
			if (LocalDateTime.now().getDayOfMonth() == time.getDayOfMonth()) {
				day = "today";
			} else {
				day = tomorrow;
			}
			return alarmS + alarm.getId() + " set for " + time.getHour() + ":" + time.getMinute() + " " + day;
		} catch (IllegalArgumentException e) {
			this.logException(e);
			return PARAMSNOTVALID;
		}
	}

	/**
	 * Sets a new timer. You can select between hours, minutes and seconds or combinate them
	 * 
	 * @param params
	 *            words in the grammar annotation
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
	 * @param params
	 *            words in the grammar annotation
	 * @return resetAlarms or resetTimers
	 */
	//@Grammar("reset [all] (alarms|timers)")
	public String resetAlarmClockObjects(String[] params) {
		if (params[1].equals("alarms") || (params.length == 3 && params[2].equals("alarms")))
			return this.logic.resetAlarms();
		return this.logic.resetTimers();
	}

	/**
	 * Deletes one specific alarm or timer
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return deleteAlarm or deleteTimer
	 */
//	@Grammar("delete (alarm|timer) #")
	public String deleteAlarmClockObject(String[] params) {
		try {
			if (params[1].equals("alarm"))
				return this.logic.deleteAlarm(Integer.parseInt(params[2]));
			return this.logic.deleteTimer(Integer.parseInt(params[2]));
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * deactivates one specific alarm or timer
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return deactivateAlarm or deactivateTimer
	 */
//	@Grammar("deactivate (alarm|timer) #")
	public String deactivateAlarmClockObject(String[] params) {
		try {
			if (params[1].equals("alarm"))
				return this.logic.deactivateAlarm(Integer.parseInt(params[2]));
			this.logic.deleteTimer(Integer.parseInt(params[2]));
			return "Timer " + params[2] + " deactivated";
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * Activates one specific alarm or timer
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return activateAlarm or activateTimer
	 */

//	@Grammar("activate alarm #")
	public String activateAlarm(String[] params) {
		try {
			return this.logic.activateAlarm(Integer.parseInt(params[2]));
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * gets one specific alarm or timer
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return getAlarm or getTimer
	 */

//	@Grammar("get (alarm|timer) #")
	public String getAlarmClockObject(String[] params) {
		try {
			if (params[1].equals("alarm")) {
				Alarm alarm = this.logic.getAlarm(Integer.parseInt(params[2]));
				return outputAlarm(alarm);
			}
			Timer timer = this.logic.getTimer(Integer.parseInt(params[2]));
			return outputTimer(timer);
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * gets all alarms or timers
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return getAllAlarms or getAllTimers
	 */

//	@Grammar("get all (alarms|timers)")
	public String getAllAlarmClockObjects(String[] params) {
		if (params[2].equals("alarms")) {
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
	 * @param params
	 *            words in the grammar annotation
	 * @return edit Alarm
	 */

//	@Grammar("edit alarm # to # oh # [today|tomorrow]")
	public String editAlarm(String[] params) {
		try {
			if (Integer.parseInt(params[4]) > 23 || Integer.parseInt(params[6]) > 59) {
				return "Not a valid time of day.";
			}
			int[] alarmTime = new int[] { -1, -1, -1 };
			alarmTime[0] = Integer.parseInt(params[4]);
			alarmTime[1] = Integer.parseInt(params[6]);
			for (String s : params) {
				if (s.equals(tomorrow)) {
					alarmTime[2] = 1;
				}
			}
			return outputAlarm(
					this.logic.editAlarm(Integer.parseInt(params[2]), alarmTime[2], alarmTime[0], alarmTime[1]));
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	/**
	 * Gets remaining timer delay
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return remaining timer delay
	 */

//	@Grammar("when (does|is) timer # (ringing|ring)")
	public String getRemainingTimerDelay(String[] params) {
		try {
			return outputTimer(this.logic.getTimer(Integer.parseInt(params[3])));
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	private void logException(Exception e) {
		this.logger.error("Exception Thrown!", e);
	}

	/**
	 * Extracts the alarm time of the speech command given to the setAlarm function Parsing is based on following
	 * grammar: {@link #SETALARMGRAMMAR}
	 * 
	 * @param params
	 *            words in the grammar annotation {@link #setAlarm(String[] params)}
	 * @return array with hour and minute of the alarm
	 */

	private static int[] extractAlarmTime(String[] params) {
		int[] alarmTime = new int[] { -1, -1, -1 };
		for (String s : params) {
			if (s.equals(tomorrow)) {
				alarmTime[2] = 1;
			}
		}
		if (params[4].equals("x")) {
			// google notation
			alarmTime[0] = Integer.parseInt(params[3]);
			alarmTime[1] = Integer.parseInt(params[5]);
		} else if (params[4].equals("oh")) {
			if (params[5].equals("clock")) {
				// whole hour notation
				alarmTime[0] = Integer.parseInt(params[3]);
				alarmTime[1] = 0;
			} else {
				// "oh" notation
				alarmTime[0] = Integer.parseInt(params[3]);
				alarmTime[1] = Integer.parseInt(params[5]);
			}
		} else {
			// past/to notation
			int minuteAddition = 0;

			if (params[3].equals("quarter"))
				minuteAddition = MINUTESQUARTERHOUR;
			else if (params[3].equals("half"))
				minuteAddition = 2 * MINUTESQUARTERHOUR;
			else
				minuteAddition = Integer.parseInt(params[3]);

			if (params[4].equals("past")) {
				alarmTime[0] = Integer.parseInt(params[5]);
				alarmTime[1] = minuteAddition;
			} else {
				alarmTime[0] = Integer.parseInt(params[5]) - 1;
				alarmTime[1] = MINUTESINHOUR - minuteAddition;
			}
		}
		// pm hour addition
		if (params.length == 7 && params[6].equals("pm")) {
			alarmTime[0] = alarmTime[0] + 12;
			if (alarmTime[0] == 24) {
				alarmTime[0] = 0;
			}
		}
		return alarmTime;
	}

	private static String outputAlarm(Alarm alarm) {
		LocalDateTime ringTime = alarm.getAlarmTime();
		String day;
		if (LocalDateTime.now().getDayOfMonth() == ringTime.getDayOfMonth()) {
			day = "today";
		} else {
			day = tomorrow;
		}
		if (alarm.isActive())
			return alarmS + alarm.getId() + " will ring at " + ringTime.getHour() + ":" + ringTime.getMinute() + " "
					+ day;

		return alarmS + alarm.getId() + " is set for " + ringTime.getHour() + ":" + ringTime.getMinute() + " " + day
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
