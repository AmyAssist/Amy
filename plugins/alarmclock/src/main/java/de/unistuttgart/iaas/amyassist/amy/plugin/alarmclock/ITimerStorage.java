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

import java.util.NoSuchElementException;

/**
 * Interface that defines the methods the alarmclock logic needs to store the alarms and timers
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 *
 */
public interface ITimerStorage {

	/**
	 * Stores a timer in the storage
	 * 
	 * @param timer
	 *            the timer to be stored
	 */
	public void storeTimer(Timer timer);

	/**
	 * Returns the current value of the timer counter in the storage
	 * 
	 * @return timer counter
	 */
	public int getTimerCounter();

	/**
	 * Sets the timer counter in the storage
	 * 
	 * @param number
	 *            new number for the timer counter
	 */
	public void putTimerCounter(int number);

	/**
	 * Increments and returns the incremented timer counter
	 * 
	 * @return incremented timer counter
	 */
	public int incrementTimerCounter();

	/**
	 * Checks if storage has a timer with given id
	 * 
	 * @param id
	 *            timer id
	 * @return true, if timer with given id exists
	 */
	public boolean hasTimer(int id);

	/**
	 * Deletes timer with given id
	 * 
	 * @param id
	 *            timer id
	 * @throws NoSuchElementException
	 *             if timer with given id is non existent
	 */
	public void deleteTimer(int id);

	/**
	 * Returns the timer with the given id from the storage
	 * 
	 * @param id
	 *            id of the counter
	 * @return timer with the given id
	 * @throws NoSuchElementException
	 *             if timer with given id is non existent
	 */
	public Timer getTimer(int id);
}
