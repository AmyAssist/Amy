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

package de.unistuttgart.iaas.amyassist.amy.plugin.calendar;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This creates an event object
 *
 * @author Florian Bauer, Muhammed Kaya
 */
@XmlRootElement
public class CalendarEvent {
	private String id;
	private LocalDateTime start;
	private LocalDateTime end;
	private String summary;
	private String location;
	private String description;
	private boolean allDay;

	/**
	 * Constructor
	 */
	public CalendarEvent() {
		// Needed for JSON
	}

	/**
	 * @param id
	 *            set id
	 * @param start
	 *            set start
	 * @param end
	 *            set end
	 * @param summary
	 *            set summary
	 * @param location
	 *            set location
	 * @param description
	 *            set description
	 * @param allDay
	 *            set if it is all day
	 */
	public CalendarEvent(String id, LocalDateTime start, LocalDateTime end, String summary, String location,
			String description, boolean allDay) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.summary = summary;
		this.location = location;
		this.description = description;
		this.allDay = allDay;
	}

	/**
	 * Get's {@link #id id}
	 *
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Set's {@link #id id}
	 *
	 * @param id
	 *            id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get's {@link #start start}
	 *
	 * @return start
	 */
	public LocalDateTime getStart() {
		return this.start;
	}

	/**
	 * Set's {@link #start start}
	 * 
	 * @param start
	 *            start
	 */
	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	/**
	 * Get's {@link #end end}
	 * 
	 * @return end
	 */
	public LocalDateTime getEnd() {
		return this.end;
	}

	/**
	 * Set's {@link #end end}
	 * 
	 * @param end
	 *            end
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	/**
	 * Get's {@link #summary summary}
	 * 
	 * @return summary
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * Set's {@link #summary summary}
	 * 
	 * @param summary
	 *            summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Get's {@link #location location}
	 * 
	 * @return location
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * Set's {@link #location location}
	 * 
	 * @param location
	 *            location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Get's {@link #description description}
	 * 
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set's {@link #description description}
	 * 
	 * @param description
	 *            description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get's {@link #allDay allDay}
	 * 
	 * @return allDay
	 */
	public boolean isAllDay() {
		return this.allDay;
	}

	/**
	 * Set's {@link #allDay allDay}
	 * 
	 * @param allDay
	 *            allDay
	 */
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CalendarEvent that = (CalendarEvent) o;
		return this.id.equals(that.id) && this.start.equals(that.start) && this.end.equals(that.end)
				&& this.summary.equals(that.summary) && this.description.equals(that.description)
				&& this.location.equals(that.location) && this.allDay == that.allDay;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.start, this.end, this.summary, this.description, this.location, this.allDay);
	}

}
