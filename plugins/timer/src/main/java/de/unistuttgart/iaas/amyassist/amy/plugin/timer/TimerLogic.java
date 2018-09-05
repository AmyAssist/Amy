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

package de.unistuttgart.iaas.amyassist.amy.plugin.timer;

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

/**
 * This class implements the logic for all the functions that our timer is capable of
 * 
 * @author Patrick Gebhardt
 */
@Service(TimerLogic.class)
public class TimerLogic implements RunnableService {

	@Reference
	private TimerBeepService alarmbeep;

	@Reference
	private TimerRegistry timerStorage;

	@Reference
	private TaskScheduler taskScheduler;

	@Reference
	private Environment environment;

	private String timerS = "Timer ";

	/**
	 * Creates a Runnable that plays the alarm sound. License: Attribution 3.0
	 * http://creativecommons.org/licenses/by-sa/3.0/deed.de Recorded by Daniel Simion
	 * 
	 * @param timerNumber
	 *            number of the timer
	 * 
	 * @return runnable
	 * 
	 */
	private Runnable createTimerRunnable(int timerNumber) {
		return () -> {
			Timer t = getTimer(timerNumber);
			if (this.environment.getCurrentLocalDateTime().truncatedTo(ChronoUnit.MINUTES).isEqual(t.getTimerTime())) {
				this.alarmbeep.beep(t);
			}
		};

	}

	/**
	 * Sets new timer and schedules it
	 * 
	 * @param timerTime
	 *            date and time of the timer
	 * @return the new timer
	 */
	protected Timer setTimer(LocalDateTime timerTime) {
		int id = searchTimerId();
		Timer timer = new Timer(id, timerTime);
		this.timerStorage.save(timer);
		Runnable timerRunnable = createTimerRunnable(id);
		ZonedDateTime with = this.environment.getCurrentDateTime().with(timer.getTimerTime());
		if (with.isBefore(this.environment.getCurrentDateTime())) {
			with = with.plusDays(1);
		}
		this.taskScheduler.schedule(timerRunnable, with.toInstant());
		return timer;
	}

	private int searchTimerId() {
		int id = 1;
		List<Timer> timerList = this.timerStorage.getAll();
		timerList.sort((timer1, timer2) -> timer1.getId() - timer2.getId());
		for (Timer t : timerList) {
			if (t.getId() == id) {
				id++;
			} else {
				return id;
			}
		}
		return id;
	}

	/**
	 * Delete all timers
	 * 
	 * @return counter counts the existing alarms
	 */
	protected String resetTimers() {
		int counter = 0;
		for (Timer t : this.timerStorage.getAll()) {
			counter++;
			this.alarmbeep.stopBeep(t);
			this.timerStorage.deleteById(t.getPersistentId());
		}
		if (counter == 0) {
			return "No timers found";
		}
		return counter + " timers deleted";
	}

	/**
	 * Delete one alarm
	 * 
	 * @param timerNumber
	 *            timerNumber in the storage
	 * @return alarmNumber
	 */
	protected String deleteTimer(int timerNumber) {
		Timer t = getTimer(timerNumber);
		this.alarmbeep.stopBeep(t);
		this.timerStorage.deleteById(t.getPersistentId());
		return this.timerS + t.getId() + " deleted";
	}

	/**
	 * Read out one timer
	 * 
	 * @param timerNumber
	 *            number of the timer in the storage
	 * 
	 * @return timer
	 */
	protected Timer getTimer(int timerNumber) {
		for (Timer t : this.timerStorage.getAll()) {
			if (t.getId() == timerNumber) {
				return t;
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * Get all timers
	 * 
	 * @return List of all timers
	 */
	protected List<Timer> getAllTimers() {
		List<Timer> timerList = this.timerStorage.getAll();
		timerList.sort((timer1, timer2) -> timer1.getId() - timer2.getId());
		return timerList;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.service.RunnableService#start()
	 */
	@Override
	public void start() {
		List<Timer> timerList = getAllTimers();
		for (Timer a : timerList) {
			Runnable timerRunnable = createTimerRunnable(a.getId());
			ZonedDateTime with = this.environment.getCurrentDateTime().with(a.getTimerTime());
			this.taskScheduler.schedule(timerRunnable, with.toInstant());
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
