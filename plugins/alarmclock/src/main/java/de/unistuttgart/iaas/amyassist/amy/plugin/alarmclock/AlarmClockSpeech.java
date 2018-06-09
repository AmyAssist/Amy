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
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * TODO: Description
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
	@Grammar("set alarm at # oh #")
	public String setAlarm(String[] params) {
		if (Integer.parseInt(params[3]) > 23 || Integer.parseInt(params[5]) > 59)
			return "Not a valid time of day.";

		return this.logic.setAlarm(new String[] { params[3], params[5] });
	}

	@Grammar("set timer on # minutes")
	public String setTimer(String[] params) {
		return this.logic.setAlarm(Integer.parseInt(params[3]));
	}

	@Grammar("delete alarm #")
	public String deleteAlarm(String[] params) {
		return this.logic.deleteAlarm(Integer.parseInt(params[2]));
	}

	@Grammar("deactivate alarm #")
	public String deactivateAlarm(String[] params) {
		return this.logic.deactivateAlarm("alarm" + params[2]);
	}

	@Grammar("reset alarms")
	public String resetAlarms(String[] params) {
		return this.logic.resetAlarms();
	}

	@Grammar("reset timers")
	public String resetTimers(String[] params) {
		return this.logic.resetTimers();
	}

	@Grammar("get alarm #")
	public String getAlarm(String[] params) {
		return this.logic.getAlarm(Integer.parseInt(params[2]));
	}

	@Grammar("get timer #")
	public String getTimer(String[] params) {
		return this.logic.getTimer(Integer.parseInt(params[2]));
	}

	@Grammar("get all")
	public String getAllAlarms(String[] params) {
		return String.join("\n", this.logic.getAllAlarms());
	}

	@Grammar("edit alarm # to # oh #")
	public String editAlarm(String[] params) {
		return this.logic.editAlarm(Integer.parseInt(params[2]), new String[] { params[4], params[6] });
	}
}
