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

	/**
	 * Sets new alarm with this scheme: hh:mm
	 * 
	 * @param params
	 * @return params[3], params[5]
	 */
	@Grammar("set alarm (at|for) # oh #")
	public String setAlarm(String[] params) {
		if (Integer.parseInt(params[3]) > 23 || Integer.parseInt(params[5]) > 59)
			return "Not a valid time of day.";

		return this.logic.setAlarm(new String[] { params[3], params[5] });
	}

	/**
	 * Sets a new timer. You can select between hours, minutes and seconds or
	 * combinate them
	 * 
	 * @param params
	 * @return params for the timer
	 */
	@Grammar("set timer on [# hours] [# minutes] [# seconds]")
	public String setTimer(String[] params) {
		if (params.length == 9) {
			return this.logic.setTimer(Integer.parseInt(params[3]), Integer.parseInt(params[5]),
					Integer.parseInt(params[7]));
		} else if (params.length == 7) {
			if (params[4].equals("hours") && params[6].equals("minutes")) {
				return this.logic.setTimer(Integer.parseInt(params[3]), Integer.parseInt(params[5]), 0);
			} else if (params[4].equals("hours") && params[6].equals("seconds")) {
				return this.logic.setTimer(Integer.parseInt(params[3]), 0, Integer.parseInt(params[5]));
			} else if (params[4].equals("minutes") && params[6].equals("seconds")) {
				return this.logic.setTimer(0, Integer.parseInt(params[3]), Integer.parseInt(params[5]));
			}
		} else if (params.length == 5) {
			if (params[4].equals("hours")) {
				return this.logic.setTimer(Integer.parseInt(params[3]), 0, 0);
			} else if (params[4].equals("minutes")) {
				return this.logic.setTimer(0, Integer.parseInt(params[3]), 0);
			} else if (params[4].equals("seconds")) {
				return this.logic.setTimer(0, 0, Integer.parseInt(params[3]));
			}
		}
		return "";
	}

	/**
	 * Resets all alarms or timers
	 * 
	 * @param params
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
	 * @return deleteAlarm or deleteTimer
	 */
	@Grammar("delete (alarm|timer) #")
	public String deleteAlarm(String[] params) {
		if (params[1].equals("alarm"))
			return this.logic.deleteAlarm(Integer.parseInt(params[2]));
		return this.logic.deleteTimer(Integer.parseInt(params[2]));
	}

	/**
	 * deactivates one specific alarm or timer
	 * 
	 * @param params
	 * @return deactivateAlarm or deactivateTimer
	 */
	@Grammar("deactivate (alarm|timer) #")
	public String deactivateAlarm(String[] params) {
		if (params[1].equals("alarm"))
			return this.logic.deactivateAlarm(Integer.parseInt(params[2]));
		return this.logic.deactivateTimer(Integer.parseInt(params[2]));
	}

	/**
	 * Activates one specific alarm or timer
	 * 
	 * @param params
	 * @return activateAlarm or activateTimer
	 */
	@Grammar("activate (alarm|timer) #")
	public String activateAlarm(String[] params) {
		if (params[1].equals("alarm"))
			return this.logic.activateAlarm(Integer.parseInt(params[2]));
		return this.logic.activateTimer(Integer.parseInt(params[2]));
	}

	/**
	 * gets one specific alarm or timer
	 * 
	 * @param params
	 * @return getAlarm or getTimer
	 */
	@Grammar("get (alarm|timer) #")
	public String getAlarm(String[] params) {
		if (params[1].equals("alarm"))
			return this.logic.getAlarm(Integer.parseInt(params[2]));
		return this.logic.getTimer(Integer.parseInt(params[2]));
	}

	/**
	 * gets all alarms or timers
	 * 
	 * @param params
	 * @return getAllAlarms or getAllTimers
	 */
	@Grammar("get all (alarms|timers)")
	public String getAllAlarms(String[] params) {
		if (params[2].equals("alarms"))
			return String.join("\n", this.logic.getAllAlarms());
		return String.join("\n", this.logic.getAllTimers());
	}

	/**
	 * edits a specific alarm
	 * 
	 * @param params
	 * @return edit Alarm
	 */
	@Grammar("edit alarm # to # oh #")
	public String editAlarm(String[] params) {
		if (Integer.parseInt(params[4]) > 23 || Integer.parseInt(params[6]) > 59)
			return "Not a valid time of day.";
		return this.logic.editAlarm(Integer.parseInt(params[2]), new String[] { params[4], params[6] });
	}
}
