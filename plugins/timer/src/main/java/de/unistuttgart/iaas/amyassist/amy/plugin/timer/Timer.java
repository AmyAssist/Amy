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
import de.unistuttgart.iaas.amyassist.amy.utility.rest.adapter.DurationAdapter;
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
	@XmlJavaTypeAdapter(DurationAdapter.class)
	private Duration remainingTime;
	private boolean active;

	/**
	 * Default constructor
	 */
	public Timer() {

	}

	/**
	 * Constructor for a timer object
	 * 
	 * @param id
	 *            id of the timer
	 * @param timerTime
	 *            date and time of the timer
	 * @param remainingTime
	 *            time until the timer rings
	 * @param active
	 *            states whether the timer is active or inactive
	 */
	public Timer(int id, LocalDateTime timerTime, Duration remainingTime, boolean active) {
		if (id < 1)
			throw new IllegalArgumentException();

		this.id = id;
		this.timerTime = timerTime;
		this.remainingTime = remainingTime;
		this.active = active;
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
	 * @return the remaining time in seconds
	 */
	public Duration getRemainingTime() {
		if (this.active) {
			LocalDateTime current = LocalDateTime.now();
			LocalDateTime future = this.getTimerTime();
			return (Duration.ofMillis(current.until(future, ChronoUnit.MILLIS)));
		}
		return this.remainingTime;
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
	 * @return whether the timer is active or not
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active
	 *            the value whether the timer is active or not
	 */
	public void setActive(boolean active) {
		this.active = active;
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
	 * @param remainingTime
	 *            sets the remainingTime of the timer
	 */
	public void setDuration(Duration remainingTime) {
		this.remainingTime = remainingTime;
	}

	/**
	 * @return the duration of the timer
	 */
	public Duration getDuration() {
		return this.remainingTime;
	}

	/**
	 * @param persistentId
	 *            persistentId
	 */
	public void setPersistentId(int persistentId) {
		this.persistentId = persistentId;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.registry.RegistryEntity#getPersistentId()
	 */
	@Override
	public int getPersistentId() {
		return this.persistentId;
	}

}
