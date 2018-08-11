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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;

/**
 * An alarm entity for the alarm registry
 * 
 * @author Patrick Gebhardt
 */
@Entity
@PersistenceUnit(unitName = "AlarmRegistry")
public class AlarmImpl implements Alarm {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false, nullable = false)
	private int persistentId;
	private int id;
	private LocalTime alarmTime;
	private boolean active;

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public LocalTime getAlarmTime() {
		return this.alarmTime;
	}

	@Override
	public void setAlarmTime(LocalTime alarmTime) {
		this.alarmTime = alarmTime;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public int getPersistentId() {
		return this.persistentId;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.active ? 1231 : 1237);
		result = prime * result + ((this.alarmTime == null) ? 0 : this.alarmTime.hashCode());
		result = prime * result + this.id;
		result = prime * result + this.persistentId;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlarmImpl other = (AlarmImpl) obj;
		if (this.active != other.active)
			return false;
		if (this.alarmTime == null) {
			if (other.alarmTime != null)
				return false;
		} else if (!this.alarmTime.equals(other.alarmTime)) {
			return false;
		}
		if (this.id != other.id)
			return false;
		if (this.persistentId != other.persistentId)
			return false;
		return true;
	}

}
