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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;

/**
 * This class implements the logic for all the functions that our alarm clock and timer are capable of
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@Service
public class AlarmClockLogic {

	@Reference
	private AlarmBeepService alarmbeep;

	@Reference
	private IAlarmClockStorage acStorage;

	@Reference
	private TaskScheduler taskScheduler;

	/**
	 * Creates a Runnable that plays the alarm sound. License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel Simion
	 * 
	 * @param alarmNumber
	 *            alarm id of the corresponding alarm object
	 * 
	 * @return runnable
	 * 
	 */
	private Runnable createAlarmRunnable(int alarmNumber) {
		return () -> {
			if (this.acStorage.hasAlarm(alarmNumber) && this.acStorage.getAlarm(alarmNumber).isActive()) {
				Alarm alarm = this.acStorage.getAlarm(alarmNumber);
				this.alarmbeep.beep(alarm);
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
			if (this.acStorage.hasTimer(timerNumber) && this.acStorage.getTimer(timerNumber).isActive()) {
				Timer timer = this.acStorage.getTimer(timerNumber);
				this.alarmbeep.beep(timer);
			}
		};

	}

	/**
	 * Set new alarm and schedule it
	 * 
	 * @param hour
	 *            hour of the alarm
	 * @param minute
	 *            minute of the alarm
	 * 
	 * @return counter, alarmTime[0], alarmTime[1]
	 */
	protected Alarm setAlarm(int hour, int minute) {
		if (Alarm.timeValid(hour, minute)) {
			int counter = this.acStorage.incrementAlarmCounter();
			Alarm alarm = new Alarm(counter, hour, minute, true);
			this.acStorage.storeAlarm(alarm);
			Runnable alarmRunnable = createAlarmRunnable(counter);
			this.taskScheduler.schedule(alarmRunnable, alarm.getAlarmDate().toInstant());
			return alarm;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Sets new timer and schedules it
	 * 
	 * @param hours
	 *            number of hours of the timer
	 * @param minutes
	 *            number of minutes of the timer
	 * @param seconds
	 *            number of seconds of the timer
	 * 
	 * @return counter, hours, minutes, seconds
	 */
	protected Timer setTimer(int hours, int minutes, int seconds) {

		if (Timer.delayValid(hours, minutes, seconds)) {
			int counter = this.acStorage.incrementTimerCounter();
			Timer timer = new Timer(counter, hours, minutes, seconds, true);
			this.acStorage.storeTimer(timer);
			Runnable timerRunnable = createTimerRunnable(counter);
			this.taskScheduler.schedule(timerRunnable, timer.getTimerDate().toInstant());
			return timer;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Delete all alarms and reset alarmCounter
	 * 
	 * @return counter counts the existing alarms
	 */
	protected String resetAlarms() {
		int amount = this.acStorage.getAlarmCounter();
		if (amount == 0) {
			return "No alarms found";
		}
		this.acStorage.putAlarmCounter(0);
		int counter = 0;
		for (int i = 1; i <= amount; i++) {
			if (this.acStorage.hasAlarm(i)) {
				counter++;
				deactivateAlarm(i);
				this.acStorage.deleteAlarm(i);
			}
		}
		return counter + " alarms deleted";
	}

	/**
	 * Delete all timers and reset timerCounter
	 * 
	 * @return counter counts the existing timers
	 */
	protected String resetTimers() {
		int amount = this.acStorage.getTimerCounter();
		if (amount == 0) {
			return "No timers found";
		}
		this.acStorage.putTimerCounter(0);
		int counter = 0;
		for (int i = 1; i <= amount; i++) {
			if (this.acStorage.hasTimer(i)) {
				counter++;
				deactivateTimer(i);
				this.acStorage.deleteTimer(i);
			}
		}
		return counter + " timers deleted";
	}

	/**
	 * Delete one alarm
	 * 
	 * @param alarmNumber
	 *            alarmNumber in the storage
	 * @return alarmNumber
	 */
	protected String deleteAlarm(int alarmNumber) {
		if (this.acStorage.hasAlarm(alarmNumber)) {
			deactivateAlarm(alarmNumber);
			this.acStorage.deleteAlarm(alarmNumber);
			return "Alarm " + alarmNumber + " deleted";
		}
		throw new NoSuchElementException();
	}

	/**
	 * 
	 * @param timerNumber
	 *            timerNumber in the storage
	 * @return timerNumber
	 */
	protected String deleteTimer(int timerNumber) {
		if (this.acStorage.hasTimer(timerNumber)) {
			deactivateTimer(timerNumber);
			this.acStorage.deleteTimer(timerNumber);
			return "Timer " + timerNumber + " deleted";
		}
		throw new NoSuchElementException();
	}

	/**
	 * Deactivates specific alarm so it will not go off
	 * 
	 * @param alarmNumber
	 *            number of the alarm
	 * 
	 * @return alarmNumber
	 */
	protected String deactivateAlarm(int alarmNumber) {
		if (this.acStorage.hasAlarm(alarmNumber)) {
			Alarm alarm = this.acStorage.getAlarm(alarmNumber);
			if (alarm.isActive()) {
				this.alarmbeep.stopBeep(alarm);
				alarm.setActive(false);
				this.acStorage.storeAlarm(alarm);
				return "Alarm " + alarmNumber + " deactivated";
			}
			return "Alarm " + alarmNumber + " is already inactive";
		}
		throw new NoSuchElementException();
	}

	/**
	 * Deactivated specific timer so it will not go off
	 * 
	 * @param timerNumber
	 *            number of the timer
	 * @return timerNumber
	 */
	protected String deactivateTimer(int timerNumber) {
		if (this.acStorage.hasTimer(timerNumber)) {
			Timer timer = this.acStorage.getTimer(timerNumber);
			if (timer.isActive()) {
				this.alarmbeep.stopBeep(timer);
				timer.setActive(false);
				this.acStorage.storeTimer(timer);
				return "Timer " + timerNumber + " deactivated";
			}
			return "Timer " + timerNumber + " is already inactive";
		}
		throw new NoSuchElementException();
	}

	/**
	 * Activates an existing alarm, so it will ring
	 * 
	 * @param alarmNumber
	 *            number of the alarm
	 * @return alarmNumber
	 */
	protected String activateAlarm(int alarmNumber) {
		if (this.acStorage.hasAlarm(alarmNumber)) {
			Alarm alarm = this.acStorage.getAlarm(alarmNumber);
			if (!alarm.isActive()) {
				alarm.setActive(true);
				this.acStorage.storeAlarm(alarm);
				return "Alarm " + alarmNumber + " activated";
			}
			return "Alarm " + alarmNumber + " is already active";
		}
		throw new NoSuchElementException();
	}

	/**
	 * Activates an existing timer, so it will ring.
	 * 
	 * @param timerNumber
	 *            number of the timer
	 * @return timerNumber
	 */
	protected String activateTimer(int timerNumber) {
		if (this.acStorage.hasTimer(timerNumber)) {
			Timer timer = this.acStorage.getTimer(timerNumber);
			if (!timer.isActive()) {
				timer.setActive(true);
				this.acStorage.storeTimer(timer);
				return "Timer " + timerNumber + " activated";
			}
			return "Timer " + timerNumber + " is already active";
		}
		throw new NoSuchElementException();
	}

	/**
	 * Read out one alarm
	 * 
	 * @param alarmNumber
	 *            number of the alarm in the storage
	 * 
	 * @return alarmNumber
	 */
	protected Alarm getAlarm(int alarmNumber) {
		if (this.acStorage.hasAlarm(alarmNumber)) {
			return this.acStorage.getAlarm(alarmNumber);
		}
		throw new NoSuchElementException();
	}

	/**
	 * @param timerNumber
	 *            number of the timer in storage
	 * @return timerNumber
	 */
	protected Timer getTimer(int timerNumber) {
		if (this.acStorage.hasTimer(timerNumber)) {
			return this.acStorage.getTimer(timerNumber);
		}
		throw new NoSuchElementException();
	}

	/**
	 * Get all alarms
	 * 
	 * @return List of all alarms
	 */
	protected List<Alarm> getAllAlarms() {
		List<Alarm> allAlarms = new ArrayList<>();
		int alarms = this.acStorage.getAlarmCounter();
		for (int i = 1; i <= alarms; i++) {
			if (this.acStorage.hasAlarm(i)) {
				allAlarms.add(this.acStorage.getAlarm(i));
			}
		}
		return allAlarms;
	}

	/**
	 * Get all timers
	 * 
	 * @return List of all timers
	 */
	protected List<Timer> getAllTimers() {
		List<Timer> allTimers = new ArrayList<>();
		int timers = this.acStorage.getTimerCounter();
		for (int i = 1; i <= timers; i++) {
			if (this.acStorage.hasTimer(i)) {
				allTimers.add(this.acStorage.getTimer(i));
			}
		}
		return allTimers;
	}

	/**
	 * Edit a specific Alarm
	 * 
	 * @param alarmNumber
	 *            name of the alarm
	 * @param hour
	 *            new hour of the alarm
	 * @param minute
	 *            new minute of the alarm
	 * @return alarmNumber + alarmTime new Time of the edited Alarm
	 */
	protected Alarm editAlarm(int alarmNumber, int hour, int minute) {
		if (this.acStorage.hasAlarm(alarmNumber) && Alarm.timeValid(hour, minute)) {
			Alarm alarm = this.acStorage.getAlarm(alarmNumber);
			alarm.setTime(hour, minute);
			this.acStorage.storeAlarm(alarm);
			return alarm;
		}
		throw new NoSuchElementException();
	}

	/**
	 * Returns the remaining timer delay in hours, minutes and seconds
	 * 
	 * @param timerNumber
	 *            id of the timer
	 * @return remaining timer delay
	 */
	protected int[] getRemainingTimerDelay(int timerNumber) {
		return this.acStorage.getTimer(timerNumber).getRemainingTime();
	}
}
