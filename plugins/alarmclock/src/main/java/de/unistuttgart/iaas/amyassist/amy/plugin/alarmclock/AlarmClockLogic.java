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

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
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
	private AlarmRegistry alarmStorage;

	@Reference
	private IAlarmClockStorage timerStorage;

	@Reference
	private TaskScheduler taskScheduler;

	@Reference
	private Environment environment;

	private LocalTime alarmTime;

	/**
	 * Creates a Runnable that plays the alarm sound. License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel Simion
	 * 
	 * @param alarm
	 * 
	 * @param alarmNumber
	 *            alarm id of the corresponding alarm object
	 * 
	 * @return runnable
	 * 
	 */
	private Runnable createAlarmRunnable(int alarmNumber) {
		return () -> {
			for (Alarm a : this.alarmStorage.getAll()) {
				if (a.getId() == alarmNumber && a.isActive()) {
					this.alarmbeep.beep(a);
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
			if (this.timerStorage.hasTimer(timerNumber) && this.timerStorage.getTimer(timerNumber).isActive()) {
				Timer timer = this.timerStorage.getTimer(timerNumber);
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
			int id = this.alarmStorage.getAll().size() + 1;
			this.alarmTime = LocalTime.of(hour, minute);
			Alarm alarm = new Alarm(id, this.alarmTime, true);
			this.alarmStorage.save(alarm);
			Runnable alarmRunnable = createAlarmRunnable(id);
			ZonedDateTime with = this.environment.getCurrentDateTime().with(alarm.getAlarmTime());
			if (with.isBefore(this.environment.getCurrentDateTime())) {
				with = with.plusDays(1);
			}
			this.taskScheduler.schedule(alarmRunnable, with.toInstant());
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
			int counter = this.timerStorage.incrementTimerCounter();
			Timer timer = new Timer(counter, hours, minutes, seconds, true);
			this.timerStorage.storeTimer(timer);
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
		int counter = 0;
		for (Alarm a : this.alarmStorage.getAll()) {
			counter++;
			deactivateAlarm(a.getId());
			this.alarmStorage.deleteById(a.getPersistentId());
		}
		if (counter == 0) {
			return "No alarms found";
		}
		return counter + " alarms deleted";
	}

	/**
	 * Delete all timers and reset timerCounter
	 * 
	 * @return counter counts the existing timers
	 */
	protected String resetTimers() {
		int amount = this.timerStorage.getTimerCounter();
		this.timerStorage.putTimerCounter(0);
		int counter = 0;
		for (int i = 1; i <= amount; i++) {
			if (this.timerStorage.hasTimer(i)) {
				counter++;
				deactivateTimer(i);
				this.timerStorage.deleteTimer(i);
			}
		}
		if (counter == 0) {
			return "No timers found";
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

		for (Alarm a : this.alarmStorage.getAll()) {
			if (a.getId() == alarmNumber) {
				deactivateAlarm(a.getId());
				this.alarmStorage.deleteById(a.getPersistentId());
				return "Alarm " + a.getId() + " deleted";
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * Deactivates and deletes the timer with the given timer id
	 * 
	 * @param timerNumber
	 *            timerNumber in the storage
	 * @return timerNumber
	 */
	protected String deleteTimer(int timerNumber) {
		if (this.timerStorage.hasTimer(timerNumber)) {
			deactivateTimer(timerNumber);
			this.timerStorage.deleteTimer(timerNumber);
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

		for (Alarm a : this.alarmStorage.getAll()) {
			if (a.getId() == alarmNumber && a.isActive()) {
				this.alarmbeep.stopBeep(a);
				a.setActive(false);
				return "Alarm " + a.getId() + " deactivated";
			}
		}
		return "Alarm " + alarmNumber + " is already inactive";
	}

	/**
	 * Deactivated specific timer so it will not go off
	 * 
	 * @param timerNumber
	 *            number of the timer
	 * @return timerNumber
	 */
	protected String deactivateTimer(int timerNumber) {
		if (this.timerStorage.hasTimer(timerNumber)) {
			Timer timer = this.timerStorage.getTimer(timerNumber);
			if (timer.isActive()) {
				this.alarmbeep.stopBeep(timer);
				timer.setActive(false);
				this.timerStorage.storeTimer(timer);
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
		for (Alarm a : this.alarmStorage.getAll()) {
			if (a.getId() == alarmNumber && !a.isActive()) {
				a.setActive(true);
				return "Alarm " + a.getId() + " activated";
			}
		}
		return "Alarm " + alarmNumber + " is already active";
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
		for (Alarm a : this.alarmStorage.getAll()) {
			if (a.getId() == alarmNumber) {
				return a;
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * @param timerNumber
	 *            number of the timer in storage
	 * @return timerNumber
	 */
	protected Timer getTimer(int timerNumber) {
		if (this.timerStorage.hasTimer(timerNumber)) {
			return this.timerStorage.getTimer(timerNumber);
		}
		throw new NoSuchElementException();
	}

	/**
	 * Get all alarms
	 * 
	 * @return List of all alarms
	 */
	protected List<Alarm> getAllAlarms() {
		List<Alarm> alarmList = new ArrayList<>();
		for (Alarm a : this.alarmStorage.getAll())
			alarmList.add(a);
		return alarmList;
	}

	/**
	 * Get all timers
	 * 
	 * @return List of all timers
	 */
	protected List<Timer> getAllTimers() {
		List<Timer> allTimers = new ArrayList<>();
		int amount = this.timerStorage.getTimerCounter();
		for (int i = 1; i <= amount; i++) {
			if (this.timerStorage.hasTimer(i)) {
				allTimers.add(this.timerStorage.getTimer(i));
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
		for (Alarm a : this.alarmStorage.getAll()) {
			if (a.getId() == alarmNumber && Alarm.timeValid(hour, minute)) {

				a.setAlarmTime(LocalTime.of(hour, minute));
				Runnable alarmRunnable = createAlarmRunnable(alarmNumber);
				ZonedDateTime with = this.environment.getCurrentDateTime().with(a.getAlarmTime());
				if (with.isBefore(this.environment.getCurrentDateTime())) {
					with = with.plusDays(1);
				}
				this.taskScheduler.schedule(alarmRunnable, with.toInstant());
				return a;
			}
		}
		throw new NoSuchElementException();
	}
}
