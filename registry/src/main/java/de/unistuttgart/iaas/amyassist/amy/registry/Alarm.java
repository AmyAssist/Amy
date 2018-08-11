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

package de.unistuttgart.iaas.amyassist.amy.registry;

import java.time.LocalTime;

/**
 * An alarm entity for the alarm registry
 * 
 * @author Patrick Gebhardt
 */
public interface Alarm extends RegistryEntity {

	/**
	 * @return the id of the alarm
	 */
	int getId();

	/**
	 * @param id
	 *            sets the id of the alarm
	 */
	void setId(int id);

	/**
	 * @return the alarmtime
	 */
	LocalTime getAlarmTime();

	/**
	 * @param alarmTime
	 *            sets the time of the alarm
	 */
	void setAlarmTime(LocalTime alarmTime);

	/**
	 * @return whether the alarm is active or not
	 */
	boolean isActive();

	/**
	 * @param active
	 *            sets whether the alarm is active or inactive
	 */
	void setActive(boolean active);
}
