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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;

/**
 * This class implements the logic for all the functions that our alarm clock and timer are capable of
 *
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@Service
public class AlarmClockLogic {

	private final Logger logger = LoggerFactory.getLogger(AlarmClockLogic.class);

	@Reference
	private IStorage storage;

	@Reference
	private TaskSchedulerAPI taskScheduler;

	protected static final String ALARMCOUNTER = "alarmCounter";

	protected static final String TIMERCOUNTER = "timerCounter";

	private static final File ALARMSOUND = new File("resources/alarmsound.wav");

	/**
	 * Reads out the given alarm per Text-to-speech. e.g.: "This alarm rings tomorrow/today at 15:15"
	 * 
	 * @param alarm
	 * 
	 * @return
	 */
	protected static String alarmOutput(String alarm) {
		String[] alarmParams = alarm.split(";");
		if (alarmParams[2].equals("true"))
			return "This alarm is set for " + alarmParams[0] + ":" + alarmParams[1] + " and active.";

		return "This alarm is set for " + alarmParams[0] + ":" + alarmParams[1] + " and NOT active.";
	}

	/**
	 * Reads out the given delay per Text-to-speech. e.g.: "This timer rings in 5 minutes"
	 * 
	 * @param timer
	 * @return
	 */
	protected static String timerOutput(String timer) {
		String[] timerParams = timer.split(";");
		if (timerParams[1].equals("true"))
			return "This timer was set on " + timerParams[0] + " minutes and is active.";

		return "This timer was set on " + timerParams[0] + " minutes and is NOT active.";
	}

	/**
	 * Creates a Runnable that plays the alarm sound. License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel Simion
	 * 
	 * @return runnable
	 *
	 */
	private Runnable createAlarmRunnable(int alarmNumber) {
		return () -> {
			if (this.storage.has("alarm" + alarmNumber)
					&& this.storage.get("alarm" + alarmNumber).split(";")[2].equals("true")) {
				try {
					Clip clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(ALARMSOUND));
					clip.start();
					Calendar instance = Calendar.getInstance();
					instance.add(Calendar.MILLISECOND, (int) (clip.getMicrosecondLength() / 1000));
					this.taskScheduler.schedule(this.createAlarmRunnable(alarmNumber), instance.getTime());
				} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
					this.logger.error("Cant play alarm sound", e);
				}
			}
		};

	}

	/**
	 * Creates a Runnable that plays the alarm sound. License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel Simion
	 * 
	 * @param timerNumber
	 *            the number of the timer in storage
	 * @return runnable
	 */
	private Runnable createTimerRunnable(int timerNumber) {
		return () -> {
			if (this.storage.has("timer" + timerNumber)
					&& this.storage.get("timer" + timerNumber).split(";")[1].equals("true")) {
				try {
					Clip clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(ALARMSOUND));
					clip.start();
					Calendar instance = Calendar.getInstance();
					instance.add(Calendar.MILLISECOND, (int) (clip.getMicrosecondLength() / 1000));
					this.taskScheduler.schedule(this.createTimerRunnable(timerNumber), instance.getTime());
				} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
					this.logger.error("Cant play alarm sound", e);
				}
			}
		};

	}

	/**
	 * Set new alarm and schedule it
	 *
	 * @param alarmTime
	 *            String array with two integers. First entry is hour second is minute
	 *
	 * @return
	 */
	protected String setAlarm(String[] alarmTime) {
		int counter = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		counter++;
		this.storage.put(ALARMCOUNTER, Integer.toString(counter));
		this.storage.put("alarm" + counter, alarmTime[0] + ";" + alarmTime[1] + ";" + "true");

		Runnable alarmRunnable = createAlarmRunnable(counter);
		Calendar current = Calendar.getInstance();
		Calendar alarmCalendar = Calendar.getInstance();
		alarmCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime[0]));
		alarmCalendar.set(Calendar.MINUTE, Integer.parseInt(alarmTime[1]));
		alarmCalendar.set(Calendar.SECOND, 0);
		if (alarmCalendar.before(current)) {
			alarmCalendar.add(Calendar.DATE, 1);
		}

		this.taskScheduler.schedule(alarmRunnable, alarmCalendar.getTime());
		return "Alarm " + counter + " set for " + alarmTime[0] + ":" + alarmTime[1];
	}

	/**
	 * Sets new timer and schedules it
	 * 
	 * @param delay
	 *            delay until alarm in minutes
	 * @return true if everything went well
	 */
	protected String setAlarm(int delay) {
		if (delay > 0) {
			int counter = Integer.parseInt(this.storage.get(TIMERCOUNTER));
			counter++;
			this.storage.put(TIMERCOUNTER, Integer.toString(counter));
			this.storage.put("timer" + counter, delay + ";" + "true");
			Runnable timerRunnable = createTimerRunnable(counter);
			Calendar alarmTime = Calendar.getInstance();
			alarmTime.add(Calendar.MINUTE, delay);
			this.taskScheduler.schedule(timerRunnable, alarmTime.getTime());
			return "Timer " + counter + " set on " + delay + " minutes";
		}
		return "No valid delay";
	}

	/**
	 * Delete all alarms and reset alarmCounter
	 *
	 * @return
	 */
	protected String resetAlarms() {
		int amount = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		if (amount == 0) {
			return "No alarms found";
		}
		this.storage.put(ALARMCOUNTER, "0");
		int counter = 0;
		for (int i = 1; i <= amount; i++) {
			String key = "alarm" + i;
			if (this.storage.has(key)) {
				counter++;
				this.storage.delete(key);
			}
		}
		return counter + " alarms deleted";
	}

	/**
	 * Delete all timers and reset timerCounter
	 * 
	 * @return
	 */
	protected String resetTimers() {
		int amount = Integer.parseInt(this.storage.get(TIMERCOUNTER));
		if (amount == 0) {
			return "No timers found";
		}
		this.storage.put(TIMERCOUNTER, "0");
		int counter = 0;
		for (int i = 1; i <= amount; i++) {
			String key = "timer" + i;
			if (this.storage.has(key)) {
				counter++;
				this.storage.delete(key);
			}
		}
		return counter + " timers deleted";
	}

	/**
	 * Delete one alarm
	 * 
	 * @param alarmNumber
	 *            alarmNumber in the storage
	 * @return
	 */
	protected String deleteAlarm(int alarmNumber) {
		if (this.storage.has("alarm" + alarmNumber)) {
			this.storage.delete("alarm" + alarmNumber);
			return "Alarm " + alarmNumber + " deleted";
		}
		return "Alarm " + alarmNumber + " not found";
	}

	/**
	 * 
	 * @param timerNumber
	 *            timerNumber in the storage
	 * @return
	 */
	protected String deleteTimer(int timerNumber) {
		if (this.storage.has("timer" + timerNumber)) {
			this.storage.delete("timer" + timerNumber);
			return "Timer " + timerNumber + " deleted";
		}
		return "Timer " + timerNumber + " not found";
	}

	/**
	 * Deactivates specific alarm so it will not go off
	 * 
	 * @param alarmNumber
	 *            number of the alarm
	 * 
	 * @return
	 */
	protected String deactivateAlarm(int alarmNumber) {
		if (this.storage.has("alarm" + alarmNumber)) {
			String alarm = this.storage.get("alarm" + alarmNumber);
			String[] params = alarm.split(";");
			try {
				if (params[2].equals("true"))
					this.storage.put("alarm" + alarmNumber, params[0] + ";" + params[1] + ";" + "false");
				else
					return "Alarm " + alarmNumber + " is already inactive";
			} catch (ArrayIndexOutOfBoundsException e) {
				this.logger.error("Something went wrong!", e);
				return "Something went wrong";
			}
			return "Alarm " + alarmNumber + " deactivated";
		}
		return "Alarm " + alarmNumber + " not found";
	}

	/**
	 * Deactivated specific timer so it will not go off
	 * 
	 * @param timerNumber
	 *            number of the timer
	 * @return
	 */
	protected String deactivateTimer(int timerNumber) {
		if (this.storage.has("timer" + timerNumber)) {
			String alarm = this.storage.get("timer" + timerNumber);
			String[] params = alarm.split(";");
			try {
				if (params[1].equals("true"))
					this.storage.put("timer" + timerNumber, params[0] + ";" + "false");
				else
					return "Timer " + timerNumber + " is already inactive";
			} catch (ArrayIndexOutOfBoundsException e) {
				this.logger.error("Something went wrong!", e);
				return "Something went wrong";
			}
			return "Timer " + timerNumber + " deactivated";
		}
		return "Timer " + timerNumber + " not found";
	}

	/**
	 * Activates an existing alarm, so it will ring
	 * 
	 * @param alarmNumber
	 *            number of the alarm
	 * @return
	 */
	protected String activateAlarm(int alarmNumber) {
		if (this.storage.has("alarm" + alarmNumber)) {
			String alarm = this.storage.get("alarm" + alarmNumber);
			String[] params = alarm.split(";");
			try {
				if (params[2].equals("false"))
					this.storage.put("alarm" + alarmNumber, params[0] + ";" + params[1] + ";" + "true");
				else
					return "Alarm " + alarmNumber + " is already active";
			} catch (ArrayIndexOutOfBoundsException e) {
				this.logger.error("Something went wrong!", e);
				return "Something went wrong";
			}
			return "Alarm " + alarmNumber + " activated";
		}
		return "Alarm " + alarmNumber + " not found";

	}

	/**
	 * Activates an existing timer, so it will ring.
	 * 
	 * @param timerNumber
	 *            number of the timer
	 * @return
	 */
	protected String activateTimer(int timerNumber) {
		if (this.storage.has("timer" + timerNumber)) {
			String alarm = this.storage.get("timer" + timerNumber);
			String[] params = alarm.split(";");
			try {
				if (params[1].equals("false"))
					this.storage.put("timer" + timerNumber, params[0] + ";" + "true");
				else
					return "Timer " + timerNumber + " is already active";
			} catch (ArrayIndexOutOfBoundsException e) {
				this.logger.error("Something went wrong!", e);
				return "Something went wrong";
			}
			return "Timer " + timerNumber + " activated";
		}
		return "Timer " + timerNumber + " not found";
	}

	/**
	 * Read out one alarm
	 * 
	 * @param alarmNumber
	 *            number of the alarm in the storage
	 *
	 * @return
	 */
	protected String getAlarm(int alarmNumber) {
		if (this.storage.has("alarm" + alarmNumber)) {
			return alarmOutput(this.storage.get("alarm" + alarmNumber));
		}
		return "Alarm not found";
	}

	/**
	 * get one alram without speech output
	 * 
	 * @param alarmNumber
	 *            number of the alarm in the storage
	 * @return
	 */
	protected String getAlarmNoOutput(int alarmNumber) {
		if (this.storage.has("alarm" + alarmNumber)) {
			return this.storage.get("alarm" + alarmNumber);
		}
		return "Alarm not found";
	}

	/**
	 * @param timerNumber
	 *            number of the timer in storage
	 * @return
	 */
	protected String getTimer(int timerNumber) {
		if (this.storage.has("timer" + timerNumber)) {
			return timerOutput(this.storage.get("timer" + timerNumber));
		}
		return "Timer not found";
	}

	/**
	 * Get all alarms
	 *
	 * @return Array of all alarms
	 */
	protected String[] getAllAlarms() {
		ArrayList<String> allAlarms = new ArrayList<>();
		int alarms = Integer.parseInt(this.storage.get(AlarmClockLogic.ALARMCOUNTER));
		for (int i = 1; i <= alarms; i++) {
			if (this.storage.has("alarm" + i)) {
				allAlarms.add(i + ";" + this.storage.get("alarm" + i));
			}
		}
		return allAlarms.toArray(new String[allAlarms.size()]);
	}

	/**
	 * Get all timers
	 * 
	 * @return Array of all timers
	 */
	protected String[] getAllTimers() {
		ArrayList<String> allTimers = new ArrayList<>();
		int timers = Integer.parseInt(this.storage.get(AlarmClockLogic.TIMERCOUNTER));
		for (int i = 1; i <= timers; i++) {
			if (this.storage.has("timer" + i)) {
				allTimers.add(i + ": " + this.storage.get("timer" + i));
			}
		}
		return allTimers.toArray(new String[allTimers.size()]);
	}

	/**
	 * Edit a specific Alarm
	 *
	 * @param alarmNumber
	 *            name of the alarm
	 * @param alarmTime
	 *            new alarm time
	 * @return
	 */
	protected String editAlarm(int alarmNumber, String[] alarmTime) {
		if (this.storage.has("alarm" + alarmNumber)) {
			deleteAlarm(alarmNumber);
			setAlarm(alarmTime);
			return "Alarm " + alarmNumber + " changed to " + alarmTime[0] + ":" + alarmTime[1];
		}
		return "Alarm not found";
	}

	/**
	 *
	 * Initialization method for logic class.
	 */
	@PostConstruct
	public void init() {
		if (!this.storage.has(ALARMCOUNTER))
			this.storage.put(ALARMCOUNTER, "0");
		if (!this.storage.has(TIMERCOUNTER))
			this.storage.put(TIMERCOUNTER, "0");
	}

}
