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
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * Speech class for alarm clock
 *
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@Service(AlarmClockSpeech.class)
@SpeechCommand({ "alarm", "alarm clock" })
public class AlarmClockSpeech {

	@Reference
	private AlarmClockLogic logic;

	@Reference
	private Logger logger;

	private static final String ELEMENTNOTFOUND = "Element not found";
	private static final String PARAMSNOTVALID = "Parameters not valid.";

	private static final int MINUTESINHOUR = 60;
	private static final int MINUTESQUARTERHOUR = 15;

	/**
	 * Sets new alarm with this scheme: hh:mm
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return params[3], params[5]
	 */
	@Grammar("(set|create) alarm (at|for|on) (#|quarter|half) (past|to) #")
	public String setAlarm(String[] params) {

		int alarmHour = -1;
		int alarmMinute = -1;
		int minuteAddition = 0;

		if (params[3].equals("quarter"))
			minuteAddition = MINUTESQUARTERHOUR;
		else if (params[3].equals("half"))
			minuteAddition = 2 * MINUTESQUARTERHOUR;
		else
			minuteAddition = Integer.parseInt(params[3]);

		if (params[4].equals("past")) {
			alarmHour = Integer.parseInt(params[5]);
			alarmMinute = minuteAddition;
		} else {
			alarmHour = Integer.parseInt(params[5]) - 1;
			alarmMinute = MINUTESINHOUR - minuteAddition;
		}
		try {
			Alarm alarm = this.logic.setAlarm(alarmHour, alarmMinute);
			Calendar time = alarm.getAlarmDate();
			return "Alarm " + alarm.getId() + " set for " + time.get(Calendar.HOUR_OF_DAY) + ":"
					+ time.get(Calendar.MINUTE);
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
	@Grammar("(set|create) timer (for|on) [# (hour|hours)] [# (minute|minutes)] [# (second|seconds)]")
	public String setTimer(String[] params) {
		try {
			if (params.length == 9) {
				return this.logic
						.setTimer(Integer.parseInt(params[3]), Integer.parseInt(params[5]), Integer.parseInt(params[7]))
						.toString();
			} else if (params.length == 7) {
				if (params[4].contains("hour") && params[6].contains("minute")) {
					return this.logic.setTimer(Integer.parseInt(params[3]), Integer.parseInt(params[5]), 0).toString();
				} else if (params[4].contains("hour") && params[6].contains("second")) {

					return this.logic.setTimer(Integer.parseInt(params[3]), 0, Integer.parseInt(params[5])).toString();
				} else if (params[4].contains("minute") && params[6].contains("second")) {
					return this.logic.setTimer(0, Integer.parseInt(params[3]), Integer.parseInt(params[5])).toString();

				}
			} else if (params.length == 5) {
				if (params[4].contains("hour")) {
					return this.logic.setTimer(Integer.parseInt(params[3]), 0, 0).toString();
				} else if (params[4].contains("minute")) {
					return this.logic.setTimer(0, Integer.parseInt(params[3]), 0).toString();
				} else if (params[4].contains("second")) {
					return this.logic.setTimer(0, 0, Integer.parseInt(params[3])).toString();
				}
			}
		} catch (IllegalArgumentException e) {
			this.logException(e);
			return PARAMSNOTVALID;
		}
		return "Speech Command not valid.";
	}

	/**
	 * Resets all alarms or timers
	 * 
	 * @param params
	 *            words in the grammar annotation
	 * @return resetAlarms or resetTimers
	 */
	@Grammar("reset (alarms|timers)")
	public String resetAlarms(String[] params) {
		if (params[1].equals("alarms"))
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
	@Grammar("delete (alarm|timer) #")
	public String deleteAlarm(String[] params) {
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
	@Grammar("deactivate (alarm|timer) #")
	public String deactivateAlarm(String[] params) {
		try {
			if (params[1].equals("alarm"))
				return this.logic.deactivateAlarm(Integer.parseInt(params[2]));
			return this.logic.deactivateTimer(Integer.parseInt(params[2]));
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
	@Grammar("activate alarm #")
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
	@Grammar("get (alarm|timer) #")
	public String getAlarm(String[] params) {
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
	@Grammar("get all (alarms|timers)")
	public String getAllAlarms(String[] params) {
		if (params[2].equals("alarms")) {
			List<Alarm> alarms = this.logic.getAllAlarms();
			String[] stringAlarms = new String[alarms.size()];
			for (int i = 0; i < alarms.size(); i++) {
				stringAlarms[i] = outputAlarm(alarms.get(i));
			}
			return String.join("\n", stringAlarms);
		}
		List<Timer> timers = this.logic.getAllTimers();
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
	@Grammar("edit alarm # to # oh #")
	public String editAlarm(String[] params) {
		try {
			if (Integer.parseInt(params[4]) > 23 || Integer.parseInt(params[6]) > 59)
				return "Not a valid time of day.";
			return this.logic
					.editAlarm(Integer.parseInt(params[2]), Integer.parseInt(params[4]), Integer.parseInt(params[6]))
					.toString();
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
	@Grammar("when (does|is) timer # (ringing|ring)")
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

	private String outputAlarm(Alarm alarm) {
		Calendar ringDate = alarm.getAlarmDate();
		if (alarm.isActive())
			return "Alarm " + alarm.getId() + " will ring at " + ringDate.get(Calendar.HOUR_OF_DAY) + ":"
					+ ringDate.get(Calendar.MINUTE);

		return "Alarm " + alarm.getId() + " is set for " + ringDate.get(Calendar.HOUR_OF_DAY) + ":"
				+ ringDate.get(Calendar.MINUTE) + " but will not ring";
	}

	private String outputTimer(Timer timer) {
		int[] timeDiff = timer.getRemainingTime();
		if (timeDiff[0] < 0 || timeDiff[1] < 0 || timeDiff[2] < 0) {
			return "Timer will not ring!";
		}
		if (timeDiff[0] == 0) {
			if (timeDiff[1] == 0) {
				return "Timer rings in " + timeDiff[2] + " seconds";
			}
			return "Timer rings in " + timeDiff[1] + " minutes and " + timeDiff[2] + " seconds";
		}
		return "Timer rings in " + timeDiff[0] + " hours and " + timeDiff[1] + " minutes and " + timeDiff[2]
				+ " seconds";
	}
}
