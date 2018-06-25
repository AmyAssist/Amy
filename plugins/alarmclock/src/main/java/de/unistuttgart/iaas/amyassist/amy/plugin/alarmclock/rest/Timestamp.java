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

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A timestamp
 * 
 * @author Christian Bräuner
 */
@XmlRootElement
public class Timestamp {

	/**
	 * the hour of the timestamp
	 */
	public int hour = 0;

	/**
	 * the minute of the timestamp
	 */
	public int minute = 0;

	/**
	 * constructor for a timestamp without set values
	 */
	public Timestamp() {
		// needed for JSON
	}

	public Timestamp(String time) throws IllegalArgumentException {
		try {
			String[] timeSplit = time.split(":");
			this.hour = Integer.parseInt(timeSplit[0]);
			this.minute = Integer.parseInt(timeSplit[1]);
		} catch (Exception e) {
			throw new IllegalArgumentException(time);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String sh = String.valueOf(this.hour);
		String sm = String.valueOf(this.minute);
		if (this.hour < 10) {
			sh = "0" + sh;
		}
		if (this.minute < 10) {
			sm = "0" + sm;
		}
		return sh + ":" + sm;
	}

	@XmlTransient
	public boolean isValid() {
		return ((this.hour >= 0 && this.hour < 24) && (this.minute >= 0 && this.minute < 60));
	}
}
