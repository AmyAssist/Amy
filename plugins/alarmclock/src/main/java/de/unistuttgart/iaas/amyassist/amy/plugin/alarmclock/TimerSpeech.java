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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;

/**
 * Speech class for timer
 *
 * @author Patrick Gebhardt
 */
@SpeechCommand
public class TimerSpeech {

	@Reference
	private TimerLogic timerLogic;

	@Reference
	private Logger logger;

	private static final String ELEMENTNOTFOUND = "Element not found";
	private static final String NUMBER_MAP_KEY = "number";

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
		LocalDateTime timerDate = LocalDateTime.now().plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
		Timer timer = this.timerLogic.setTimer(timerDate);
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
	public String resetTimerObjects(Map<String, EntityData> entities) {
		return this.timerLogic.resetTimers();
	}

	/**
	 * Deletes one specific alarm or timer
	 * 
	 * @param entities
	 *            contains the data input
	 * @return deleteAlarm or deleteTimer
	 */
	@Intent
	public String deleteTimerObject(Map<String, EntityData> entities) {
		try {
			return this.timerLogic.deleteTimer(entities.get(NUMBER_MAP_KEY).getNumber());
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
	public String getTimerObject(Map<String, EntityData> entities) {
		try {
			Timer timer = this.timerLogic.getTimer(entities.get(NUMBER_MAP_KEY).getNumber());
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
	public String getAllTimerObjects(Map<String, EntityData> entities) {
		List<Timer> timers = this.timerLogic.getAllTimers();
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
	 * Gets remaining timer delay
	 * 
	 * @param entities
	 *            contains the data input
	 * @return remaining timer delay
	 */
	@Intent
	public String getRemainingTimerDelay(Map<String, EntityData> entities) {
		try {
			return outputTimer(this.timerLogic.getTimer(entities.get(NUMBER_MAP_KEY).getNumber()));
		} catch (NoSuchElementException e) {
			this.logException(e);
			return ELEMENTNOTFOUND;
		}
	}

	private void logException(Exception e) {
		this.logger.error("Exception Thrown!", e);
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
