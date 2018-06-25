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

/**
 * Interface that defines the methods the alarmclock logic needs to store the
 * alarms and timers
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 *
 */
public interface IAlarmClockStorage {

	/**
	 * Stores an alarm in the storage
	 * 
	 * @param alarm
	 *            the alarm to be stored
	 */
	public void storeAlarm(Alarm alarm);

	/**
	 * Stores a timer in the storage
	 * 
	 * @param timer
	 *            the timer to be stored
	 */
	public void storeTimer(Timer timer);

	/**
	 * Returns the current value of the alarm counter in the storage
	 * 
	 * @return
	 */
	public int getAlarmCounter();

	/**
	 * Returns the current value of the timer counter in the storage
	 * 
	 * @return
	 */
	public int getTimerCounter();

	/**
	 * Sets the alarm counter in the storage
	 * 
	 * @param number
	 *            new number for the alarm counter
	 */
	public void putAlarmCounter(int number);

	/**
	 * Sets the timer counter in the storage
	 * 
	 * @param number
	 *            new number for the timer counter
	 */
	public void putTimerCounter(int number);

	/**
	 * Increments and returns the incremented alarm counter
	 * 
	 * @return
	 */
	public int incrementAlarmCounter();

	/**
	 * Increments and returns the incremented timer counter
	 * 
	 * @return
	 */
	public int incrementTimerCounter();

	/**
	 * Checks if the storage has the given key stored
	 * 
	 * @param key
	 *            key that should be checked
	 * @return
	 */
	public boolean hasKey(String key);

	/**
	 * Deletes the given key from the storage. Only use this method, if it's
	 * guaranteed that the key is actually stored
	 * 
	 * @param key
	 *            key that should be deleted
	 */
	public void deleteKey(String key);

	/**
	 * Returns the alarm with the given id from the storage
	 * 
	 * @param id
	 *            id of the alarm
	 * @return
	 */
	public Alarm getAlarm(int id);

	/**
	 * Returns the timer with the given id from the storage
	 * 
	 * @param id
	 *            id of the counter
	 * @return
	 */
	public Timer getTimer(int id);
}