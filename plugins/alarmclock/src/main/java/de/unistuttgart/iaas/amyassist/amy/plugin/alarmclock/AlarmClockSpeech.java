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
	 * @return true if everything went well
	 */
	@Grammar("set alarm (at|for) # oh #")
	public String setAlarm(String[] params) {
		if (Integer.parseInt(params[3]) > 23 || Integer.parseInt(params[5]) > 59)
			return "Not a valid time of day.";

		return this.logic.setAlarm(new String[] { params[3], params[5] });
	}

	@Grammar("set timer on [# hours] [# minutes] [# seconds]")
	public String setTimer(String[] params) {
		if (params.length >= 5) {
			// TODO: implement
		}
		return "";
	}

	@Grammar("reset (alarms|timers)")
	public String resetAlarms(String[] params) {
		if (params[1].equals("alarms"))
			return this.logic.resetAlarms();
		return this.logic.resetTimers();
	}

	@Grammar("delete (alarm|timer) #")
	public String deleteAlarm(String[] params) {
		if (params[1].equals("alarm"))
			return this.logic.deleteAlarm(Integer.parseInt(params[2]));
		return this.logic.deleteTimer(Integer.parseInt(params[2]));
	}

	@Grammar("deactivate (alarm|timer) #")
	public String deactivateAlarm(String[] params) {
		if (params[1].equals("alarm"))
			return this.logic.deactivateAlarm(Integer.parseInt(params[2]));
		return this.logic.deactivateTimer(Integer.parseInt(params[2]));
	}

	@Grammar("activate (alarm|timer) #")
	public String activateAlarm(String[] params) {
		if (params[1].equals("alarm"))
			return this.logic.activateAlarm(Integer.parseInt(params[2]));
		return this.logic.activateTimer(Integer.parseInt(params[2]));
	}

	@Grammar("get (alarm|timer) #")
	public String getAlarm(String[] params) {
		if (params[1].equals("alarm"))
			return this.logic.getAlarm(Integer.parseInt(params[2]));
		return this.logic.getTimer(Integer.parseInt(params[2]));
	}

	@Grammar("get all (alarms|timers)")
	public String getAllAlarms(String[] params) {
		if (params[2].equals("alarms"))
			return String.join("\n", this.logic.getAllAlarms());
		return String.join("\n", this.logic.getAllTimers());
	}

	@Grammar("edit alarm # to # oh #")
	public String editAlarm(String[] params) {
		if (Integer.parseInt(params[4]) > 23 || Integer.parseInt(params[6]) > 59)
			return "Not a valid time of day.";
		return this.logic.editAlarm(Integer.parseInt(params[2]), new String[] { params[4], params[6] });
	}
}
