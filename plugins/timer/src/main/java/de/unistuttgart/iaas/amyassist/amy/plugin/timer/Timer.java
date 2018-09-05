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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unistuttgart.iaas.amyassist.amy.registry.RegistryEntity;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.adapter.LocalDateTimeAdapter;

/**
 * Class that defines timer attributes and behaviour
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 *
 */
@Entity
@PersistenceUnit(unitName = "TimerRegistry")
public class Timer extends de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity implements RegistryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false, nullable = false)
	private int persistentId;
	private int id;
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime timerTime;

	public Timer() {

	}

	/**
	 * Constructor for a timer object
	 * 
	 * @param id
	 *            id of the timer
	 * @param timerTime
	 *            date and time of the timer
	 */
	public Timer(int id, LocalDateTime timerTime) {
		if (id < 1)
			throw new IllegalArgumentException();
		this.id = id;
		this.timerTime = timerTime;
	}

	/**
	 * Returns a string representation of this object
	 * 
	 * @see java.lang.Object#toString()
	 * 
	 * @return String representation of this timer object
	 */
	@Override
	public String toString() {
		return this.id + ":" + this.timerTime.getHour() + ":" + this.timerTime.getMinute() + ":"
				+ this.timerTime.getSecond();
	}

	/**
	 * Returns this timers delay until it goes off
	 * 
	 * @param timer
	 *            timer of which the remaining time should be calculated
	 * @return the remaining time in seconds
	 */
	public Duration getRemainingTime(Timer timer) {
		LocalDateTime current = LocalDateTime.now();
		LocalDateTime future = timer.getTimerTime();
		return Duration.ofMillis(current.until(future, ChronoUnit.MILLIS));
	}

	/**
	 * Checks if the given hours, minutes and seconds are valid for a timer
	 * 
	 * @param hours
	 *            hour delay of timer
	 * @param minutes
	 *            minute delay of timer
	 * @param seconds
	 *            second delay of timer
	 * @return true, if delay is valid, else false
	 */
	public static boolean delayValid(int hours, int minutes, int seconds) {
		return hours >= 0 && minutes >= 0 && seconds >= 0 && hours + minutes + seconds > 0;
	}

	/**
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            timer id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return timerDate
	 */
	public LocalDateTime getTimerTime() {
		return this.timerTime;
	}

	/**
	 * @param timerTime
	 *            date and time the timer rings
	 */
	public void setTimerTime(LocalDateTime timerTime) {
		this.timerTime = timerTime;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.registry.RegistryEntity#getPersistentId()
	 */
	@Override
	public int getPersistentId() {
		return this.persistentId;
	}

}
