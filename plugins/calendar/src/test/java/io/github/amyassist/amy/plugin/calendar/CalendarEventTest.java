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

package io.github.amyassist.amy.plugin.calendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for CalendarEvent
 *
 * @author Muhammed Kaya
 */
class CalendarEventTest {

	private CalendarEvent event1;
	private CalendarEvent event2;

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#CalendarEvent()}.
	 */
	@Test
	@BeforeEach
	void testCalendarEventConstructors() {
		this.event1 = new CalendarEvent();
		this.event1 = new CalendarEvent(LocalDateTime.parse("2015-05-28T09:00:00"),
				LocalDateTime.parse("2015-05-28T11:00:00"), "summary", "home", "homework", "type", 0, "", false);
		this.event2 = new CalendarEvent("1", LocalDateTime.parse("2015-05-28T08:00:00"),
				LocalDateTime.parse("2015-05-28T10:00:00"), "summary", "work", "meeting", false);
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getId()}.
	 */
	@Test
	void testGetId() {
		assertThat(this.event2.getId(), equalTo("1"));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getStart()}.
	 */
	@Test
	void testGetStart() {
		assertThat(this.event2.getStart(), equalTo(LocalDateTime.parse("2015-05-28T08:00:00")));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getEnd()}.
	 */
	@Test
	void testGetEnd() {
		assertThat(this.event2.getEnd(), equalTo(LocalDateTime.parse("2015-05-28T10:00:00")));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getSummary()}.
	 */
	@Test
	void testGetSummary() {
		assertThat(this.event1.getSummary(), equalTo("summary"));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getLocation()}.
	 */
	@Test
	void testGetLocation() {
		assertThat(this.event1.getLocation(), equalTo("home"));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getDescription()}.
	 */
	@Test
	void testGetDescription() {
		assertThat(this.event1.getDescription(), equalTo("homework"));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#isAllDay()}.
	 */
	@Test
	void testIsAllDay() {
		assertThat(this.event1.isAllDay(), is(false));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getRecurrence()}.
	 */
	@Test
	void testGetRecurrence() {
		assertThat(this.event1.getRecurrence(), equalTo(""));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getReminderType()}.
	 */
	@Test
	void testGetReminderType() {
		assertThat(this.event1.getReminderType(), equalTo("type"));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#getReminderTime()}.
	 */
	@Test
	void testGetReminderTime() {
		assertThat(this.event1.getReminderTime(), is(0));
	}

	/**
	 * Test method for
	 * {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		assertThat(this.event1.equals(new CalendarEvent(LocalDateTime.parse("2015-05-28T09:00:00"),
				LocalDateTime.parse("2015-05-28T11:00:00"), "summary", "home", "homework", "type", 0, "", false)),
				is(true));
		assertThat(this.event1.equals(this.event1), is(true));
		assertThat(this.event1.equals(null), is(false));
		assertThat(this.event1.equals(""), is(false));
	}

	/**
	 * Test method for {@link io.github.amyassist.amy.plugin.calendar.CalendarEvent#hashCode()}.
	 */
	@Test
	void testHashCode() {
		assertThat(this.event1.hashCode(),
				is(Objects.hash(this.event1.getId(), this.event1.getStart(), this.event1.getEnd(),
						this.event1.getSummary(), this.event1.getLocation(), this.event1.getDescription(),
						this.event1.getReminderType(), this.event1.getReminderTime(), this.event1.getRecurrence(),
						this.event1.isAllDay())));
	}

}
