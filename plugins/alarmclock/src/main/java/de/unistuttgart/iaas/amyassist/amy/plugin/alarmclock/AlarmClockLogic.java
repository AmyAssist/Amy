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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;

/**
 * This class implements the logic for all the functions that our alarm clock
 * and timer are capable of
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@Service
public class AlarmClockLogic {

	private final Logger logger = LoggerFactory.getLogger(AlarmClockLogic.class);

	@Reference
	private IAlarmClockStorage acStorage;

	@Reference
	private TaskSchedulerAPI taskScheduler;

	private static final File ALARMSOUND = new File("resources/alarmsound.wav");

	/**
	 * Reads out the given alarm per Text-to-speech. e.g.: "This alarm rings
	 * tomorrow/today at 15:15"
	 * 
	 * @param alarm
	 * 
	 * @return
	 */
	protected static String alarmOutput(Alarm alarm) {
		if (alarm.isActive())
			return "This alarm is set for " + alarm.getAlarmDate().get(Calendar.HOUR_OF_DAY) + ":"
					+ alarm.getAlarmDate().get(Calendar.MINUTE) + " and active.";

		return "This alarm is set for " + alarm.getAlarmDate().get(Calendar.HOUR_OF_DAY) + ":"
				+ alarm.getAlarmDate().get(Calendar.MINUTE) + " and is NOT active.";
	}

	/**
	 * Reads out the given delay per Text-to-speech. e.g.: "This timer rings in
	 * 5 minutes"
	 * 
	 * @param timer
	 * @return
	 */
	protected static String timerOutput(Timer timer) {
		Calendar current = Calendar.getInstance();
		Calendar timerDate = timer.getTimerDate();

		Duration duration = Duration.between(current.toInstant(), timerDate.toInstant());
		int diff = (int) duration.getSeconds();

		int hourDiff = diff / 3600;
		diff %= 3600;

		int minuteDiff = diff / 60;
		diff %= 60;

		int secondDiff = diff;

		if (timer.isActive()) {

			return "This timer will ring in " + hourDiff + " hours, " + minuteDiff + " minutes and " + secondDiff
					+ " seconds.";
		}
		return "This timer is NOT active.";
	}

	/**
	 * Creates a Runnable that plays the alarm sound. License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel
	 * Simion
	 *
	 * @return runnable
	 *
	 */
	private Runnable createAlarmRunnable(int alarmNumber) {
		return () -> {
			if (this.acStorage.hasKey("alarm" + alarmNumber) && this.acStorage.getAlarm(alarmNumber).isActive()) {
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
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel
	 * Simion
	 *
	 * @param timerNumber
	 *            the number of the timer in storage
	 * @return runnable
	 */
	private Runnable createTimerRunnable(int timerNumber) {
		return () -> {
			if (this.acStorage.hasKey("timer" + timerNumber) && this.acStorage.getTimer(timerNumber).isActive()) {
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
	 *            String array with two integers. First entry is hour second is
	 *            minute
	 *
	 * @return
	 */
	protected String setAlarm(String[] alarmTime) {
		int counter = this.acStorage.incrementAlarmCounter();
		Alarm alarm = new Alarm(counter, Integer.parseInt(alarmTime[0]), Integer.parseInt(alarmTime[1]), true);
		this.acStorage.storeAlarm(alarm);
		Runnable alarmRunnable = createAlarmRunnable(counter);
		this.taskScheduler.schedule(alarmRunnable, alarm.getAlarmDate().getTime());
		return "Alarm " + counter + " set for " + alarmTime[0] + ":" + alarmTime[1];
	}

	/**
	 * Sets new timer and schedules it
	 *
	 * @param hours
	 * @param minutes
	 * @param seconds
	 *
	 * @return true if everything went well
	 */
	protected String setTimer(int hours, int minutes, int seconds) {

		if (hours + minutes + seconds > 0) {
			int counter = this.acStorage.incrementTimerCounter();
			Timer timer = new Timer(counter, hours, minutes, seconds, true);
			this.acStorage.storeTimer(timer);
			Runnable timerRunnable = createTimerRunnable(counter);
			this.taskScheduler.schedule(timerRunnable, timer.getTimerDate().getTime());
			return "Timer " + counter + " set on " + hours + " hours, " + minutes + " minutes and " + seconds
					+ " seconds.";
		}
		return "No valid delay";
	}

	/**
	 * Delete all alarms and reset alarmCounter
	 *
	 * @return
	 */
	protected String resetAlarms() {
		int amount = this.acStorage.getAlarmCounter();
		if (amount == 0) {
			return "No alarms found";
		}
		this.acStorage.putAlarmCounter(0);
		int counter = 0;
		for (int i = 1; i <= amount; i++) {
			String key = "alarm" + i;
			if (this.acStorage.hasKey(key)) {
				counter++;
				this.acStorage.deleteKey(key);
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
		int amount = this.acStorage.getTimerCounter();
		if (amount == 0) {
			return "No timers found";
		}
		this.acStorage.putTimerCounter(0);
		int counter = 0;
		for (int i = 1; i <= amount; i++) {
			String key = "timer" + i;
			if (this.acStorage.hasKey(key)) {
				counter++;
				this.acStorage.deleteKey(key);
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
		if (this.acStorage.hasKey("alarm" + alarmNumber)) {
			this.acStorage.deleteKey("alarm" + alarmNumber);
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
		if (this.acStorage.hasKey("alarm" + timerNumber)) {
			this.acStorage.deleteKey("alarm" + timerNumber);
			return "Alarm " + timerNumber + " deleted";
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
		if (this.acStorage.hasKey("alarm" + alarmNumber)) {
			Alarm alarm = this.acStorage.getAlarm(alarmNumber);
			try {
				if (alarm.isActive()) {
					alarm.setActive(false);
					this.acStorage.storeAlarm(alarm);
				} else {
					return "Alarm " + alarmNumber + " is already inactive";
				}
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
		if (this.acStorage.hasKey("timer" + timerNumber)) {
			Timer timer = this.acStorage.getTimer(timerNumber);
			try {
				if (timer.isActive()) {
					timer.setActive(false);
					this.acStorage.storeTimer(timer);
				} else {
					return "Timer " + timerNumber + " is already inactive";
				}
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
		if (this.acStorage.hasKey("alarm" + alarmNumber)) {
			Alarm alarm = this.acStorage.getAlarm(alarmNumber);
			try {
				if (!alarm.isActive()) {
					this.acStorage.storeAlarm(alarm);
				} else {
					return "Alarm " + alarmNumber + " is already active";
				}
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
		if (this.acStorage.hasKey("timer" + timerNumber)) {
			Timer timer = this.acStorage.getTimer(timerNumber);
			try {
				if (!timer.isActive())
					this.acStorage.storeTimer(timer);
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
		if (this.acStorage.hasKey("alarm" + alarmNumber)) {
			return alarmOutput(this.acStorage.getAlarm(alarmNumber));
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
		if (this.acStorage.hasKey("alarm" + alarmNumber)) {
			return this.acStorage.getAlarm(alarmNumber).convertToString();
		}
		return "Alarm not found";
	}

	/**
	 * @param timerNumber
	 *            number of the timer in storage
	 * @return
	 */
	protected String getTimer(int timerNumber) {
		if (this.acStorage.hasKey("timer" + timerNumber)) {
			return timerOutput(this.acStorage.getTimer(timerNumber));
		}
		return "Timer not found";
	}

	/**
	 * Get all alarms
	 *
	 * @return Array of all alarms
	 */
	protected String[] getAllAlarms() {
		List<String> allAlarms = new ArrayList<>();
		int alarms = this.acStorage.getAlarmCounter();
		for (int i = 1; i <= alarms; i++) {
			if (this.acStorage.hasKey("alarm" + i)) {
				allAlarms.add(this.acStorage.getAlarm(i).convertToString());
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
		List<String> allTimers = new ArrayList<>();
		int timers = this.acStorage.getTimerCounter();
		for (int i = 1; i <= timers; i++) {
			if (this.acStorage.hasKey("timer" + i)) {
				allTimers.add(this.acStorage.getTimer(i).convertToString());
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
		if (this.acStorage.hasKey("alarm" + alarmNumber)) {
			deleteAlarm(alarmNumber);
			setAlarm(alarmTime);
			return "Alarm " + alarmNumber + " changed to " + alarmTime[0] + ":" + alarmTime[1];
		}
		return "Alarm not found";
	}

	/*
	 * @PostConstruct public void init() { if
	 * (!this.acStorage.hasKey("ALARMCOUNTER")) this.acStorage.put(ALARMCOUNTER,
	 * "0"); if (!this.acStorage.has(TIMERCOUNTER))
	 * this.acStorage.put(TIMERCOUNTER, "0"); }
	 */

}
