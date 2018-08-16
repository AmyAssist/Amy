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
import java.util.Arrays;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventReminder;
import de.unistuttgart.iaas.amyassist.amy.utility.rest.adapter.LocalDateTimeAdapter;

/**
 * This creates an event object
 *
 * @author Florian Bauer, Muhammed Kaya
 */
@XmlRootElement
public class CalendarEvent {
	private String id;
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime start;
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime end;
	private String summary;
	private String location;
	private String description;
	private String reminderType;
	private int reminderTime;
	private String recurrence;
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
	 *
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
	 * @param reminderType
	 *            set reminderType
	 * @param reminderTime
	 *            set reminderTime in minutes
	 * @param recurrence
	 *            set recurrence
	 * @param allDay
	 *            set allDay
	 */
	public CalendarEvent(LocalDateTime start, LocalDateTime end, String summary, String location, String description,
			String reminderType, int reminderTime, String recurrence, boolean allDay) {
		this.start = start;
		this.end = end;
		this.summary = summary;
		this.location = location;
		this.description = description;
		this.reminderType = reminderType;
		this.reminderTime = reminderTime;
		this.recurrence = recurrence;
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
	 * Get's {@link #start start}
	 *
	 * @return start
	 */
	public LocalDateTime getStart() {
		return this.start;
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
	 * Get's {@link #summary summary}
	 *
	 * @return summary
	 */
	public String getSummary() {
		return this.summary;
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
	 * Get's {@link #description description}
	 *
	 * @return description
	 */
	public String getDescription() {
		return this.description;
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
	 *
	 * @return the recurrence options
	 */
	public String getRecurrence() {
		return this.recurrence;
	}

	/**
	 *
	 * @return the reminder type
	 */
	public String getReminderType() {
		return this.reminderType;
	}

	/**
	 *
	 * @return the reminder time in minutes
	 */
	public int getReminderTime() {
		return this.reminderTime;
	}

	/**
	 *
	 * @return the reminders of the Event as Event.Reminders from google api
	 */
	public Event.Reminders getReminders(){
		EventReminder[] reminderOverrides = new EventReminder[]{
				new EventReminder()
						.setMethod(this.reminderType)
						.setMinutes(this.reminderTime),
		};

		return new Event.Reminders()
				.setUseDefault(false)
				.setOverrides(Arrays.asList(reminderOverrides));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CalendarEvent that = (CalendarEvent) o;
		return (this.id == null || this.id.equals(that.id)) && this.start.equals(that.start)
				&& this.end.equals(that.end) && this.summary.equals(that.summary) && this.location.equals(that.location)
				&& this.description.equals(that.description) && this.reminderType.equals(that.reminderType)
				&& this.reminderTime == that.reminderTime && this.recurrence.equals(that.recurrence)
				&& this.allDay == that.allDay;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.start, this.end, this.summary, this.location, this.description,
				this.reminderType, this.reminderTime, this.recurrence, this.allDay);
	}

}
