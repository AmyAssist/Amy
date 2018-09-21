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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskScheduler;
import de.unistuttgart.iaas.amyassist.amy.messagehub.MessageHub;
import de.unistuttgart.iaas.amyassist.amy.messagehub.topics.Topics;

/**
 * This class implements the logic for all the functions that our alarm clock is capable of
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 */
@Service(AlarmClockLogic.class)
public class AlarmClockLogic implements RunnableService {

	@Reference
	private AlarmBeepService alarmbeep;

	@Reference
	private AlarmRegistry alarmStorage;

	@Reference
	private TaskScheduler taskScheduler;

	@Reference
	private Environment environment;

	private LocalDateTime alarmTime;

	private String alarmS = "Alarm ";

	private MessageHub messageHub;

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
			Alarm a = getAlarm(alarmNumber);
			if (a.isActive() && this.environment.getCurrentLocalDateTime().truncatedTo(ChronoUnit.MINUTES)
					.isEqual(a.getAlarmTime())) {
				String topic = Topics.smarthomeAll("alarm");
				this.messageHub.publish(topic, this.alarmS + alarmNumber + "is ringing");
				this.alarmbeep.beep(a);
			}
		};

	}

	/**
	 * Set new alarm and schedule it
	 * 
	 * @param tomorrow
	 *            the day of the alarm
	 * @param hour
	 *            hour of the alarm
	 * @param minute
	 *            minute of the alarm
	 * 
	 * @return alarm
	 */
	protected Alarm setAlarm(int tomorrow, int hour, int minute) {
		if (Alarm.timeValid(hour, minute)) {
			tomorrowCheck(tomorrow, hour, minute);
			int id = searchAlarmId();
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

	private int searchAlarmId() {
		int id = 1;
		List<Alarm> alarmList = this.alarmStorage.getAll();
		alarmList.sort((alarm1, alarm2) -> alarm1.getId() - alarm2.getId());
		for (Alarm a : alarmList) {
			if (a.getId() == id) {
				id++;
			} else {
				return id;
			}
		}
		return id;
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
	 * Delete one alarm
	 * 
	 * @param alarmNumber
	 *            alarmNumber in the storage
	 * @return alarmNumber
	 */
	protected String deleteAlarm(int alarmNumber) {
		Alarm a = getAlarm(alarmNumber);
		deactivateAlarm(a.getId());
		this.alarmStorage.deleteById(a.getPersistentId());
		return this.alarmS + a.getId() + " deleted";
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
		Alarm a = getAlarm(alarmNumber);
		if (a.isActive()) {
			this.alarmbeep.stopBeep(a);
			a.setActive(false);
			this.alarmStorage.save(a);
			return "Alarm " + a.getId() + " deactivated";
		}
		return this.alarmS + alarmNumber + " is already inactive";
	}

	/**
	 * Activates an existing alarm, so it will ring
	 * 
	 * @param alarmNumber
	 *            number of the alarm
	 * @return alarmNumber
	 */
	protected String activateAlarm(int alarmNumber) {
		Alarm a = getAlarm(alarmNumber);
		if (!a.isActive()) {
			a.setActive(true);
			this.alarmStorage.save(a);
			return this.alarmS + a.getId() + " activated";
		}

		return this.alarmS + alarmNumber + " is already active";
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
	 * Get all alarms
	 * 
	 * @return List of all alarms
	 */
	protected List<Alarm> getAllAlarms() {
		List<Alarm> alarmList = this.alarmStorage.getAll();
		alarmList.sort((alarm1, alarm2) -> alarm1.getId() - alarm2.getId());
		return alarmList;
	}

	/**
	 * Edit a specific Alarm
	 * 
	 * @param alarmNumber
	 *            name of the alarm
	 * @param tomorrow
	 *            if the value for tomorrow is 1, tha alarm is set for tomorrow else the alarm is set for today
	 * @param hour
	 *            new hour of the alarm
	 * @param minute
	 *            new minute of the alarm
	 * @return alarm a
	 */
	protected Alarm editAlarm(int alarmNumber, int tomorrow, int hour, int minute) {
		if (Alarm.timeValid(hour, minute)) {
			tomorrowCheck(tomorrow, hour, minute);
			Alarm a = getAlarm(alarmNumber);
			a.setAlarmTime(this.alarmTime);
			this.alarmStorage.save(a);
			Runnable alarmRunnable = createAlarmRunnable(alarmNumber);
			ZonedDateTime with = this.environment.getCurrentDateTime().with(a.getAlarmTime());
			if (with.isBefore(this.environment.getCurrentDateTime())) {
				with = with.plusDays(1);
			}
			this.taskScheduler.schedule(alarmRunnable, with.toInstant());
			return a;
		}
		throw new NoSuchElementException();
	}

	/**
	 * @param tomorrow
	 *            number of the day
	 * @param hour
	 *            hour of the day
	 * @param minute
	 *            minute of the hour
	 * @return Date and Time of the alarm
	 */
	private LocalDateTime tomorrowCheck(int tomorrow, int hour, int minute) {
		if (tomorrow == 1) {
			LocalDateTime date = LocalDateTime.now().plusDays(1);
			this.alarmTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour, minute);
			return this.alarmTime;
		}
		this.alarmTime = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(),
				LocalDateTime.now().getDayOfMonth(), hour, minute);
		return this.alarmTime;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		List<Alarm> alarmList = getAllAlarms();
		for (Alarm a : alarmList) {
			Runnable alarmRunnable = createAlarmRunnable(a.getId());
			ZonedDateTime with = this.environment.getCurrentDateTime().with(a.getAlarmTime());
			this.taskScheduler.schedule(alarmRunnable, with.toInstant());
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#stop()
	 */
	@Override
	public void stop() {
		// Nothing happens here.
	}
}
