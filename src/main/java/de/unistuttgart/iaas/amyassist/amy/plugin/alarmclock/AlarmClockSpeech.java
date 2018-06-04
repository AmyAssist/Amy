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
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Init;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * TODO: Description
 *
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@Service(AlarmClockSpeech.class)
@SpeechCommand({ "Alarm", "Alarm clock" })
public class AlarmClockSpeech {

	@Reference
	private AlarmClockLogic logic;

	/**
	 * Sets new alarm with this scheme: hh:mm
	 *
	 * @return true if everything went well
	 */
	@Grammar("set alarm")
	private boolean setAlarm() {
		return this.logic.setAlarm("10:00");
	}

	@Grammar("set timer")
	private boolean setTimer() {
		return this.logic.setAlarm(60000);
	}

	@Grammar("delete")
	private boolean deleteAlarm() {
		return this.logic.deleteAlarm("1");
	}

	@Grammar("deactivate")
	private boolean deactivateAlarm() {
		return this.logic.deactivateAlarm("alarm1");
	}

	@Grammar("reset alarms")
	private boolean resetAlarms() {
		return this.logic.resetAlarms();
	}

	@Grammar("reset timers")
	private boolean resetTimers() {
		return this.logic.resetTimers();
	}

	@Grammar("get")
	private String getAlarm() {
		return this.logic.getAlarm();
	}

	@Grammar("get all")
	private String[] getAllAlarms() {
		return this.logic.getAllAlarms();
	}

	@Grammar("edit")
	private boolean editAlarm() {
		return this.logic.editAlarm("1", "10");
	}

	/**
	 * Initialization method
	 *
	 * @param core
	 *            The core
	 */
	@Init
	public void init(ICore core) {
		this.logic = new AlarmClockLogic();
		this.logic.init(core);
	}

}
