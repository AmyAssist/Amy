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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * This class is for the Calendar Authetification and Logic, parts of the Code are from
 * https://developers.google.com/calendar/quickstart/java
 * 
 * @author Patrick Gebhardt, Florian Bauer
 */
@Service
public class CalendarLogic {
	private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these scopes, delete your previously
	 * saved credentials/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

	@Reference
	private Properties configuration;
	@Reference
	private Environment environment;
	@Reference
	private Logger logger;

	private Calendar service;

	private List<String> eventList = new ArrayList<>();

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @param xHTTPTRANSPORT
	 *            The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException
	 *             If there is no client_secret.
	 */
	private Credential getCredentials(final NetHttpTransport xHTTPTRANSPORT) throws IOException {
		// Load client secrets.
		InputStream test = new ByteArrayInputStream(this.configuration.getProperty("JSON").getBytes());
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(test));
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(xHTTPTRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(this.environment.getWorkingDirectory()
								.resolve("temp/calendarauth").toAbsolutePath().toFile()))
						.setAccessType("offline").build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	/**
	 * This method is to authorize the Google account with the calendar
	 */
	@PostConstruct
	public void authorize() {
		try {
			// Build a new authorized API client service.
			final NetHttpTransport yHTTPTRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			this.service = new Calendar.Builder(yHTTPTRANSPORT, JSON_FACTORY, getCredentials(yHTTPTRANSPORT))
					.setApplicationName(APPLICATION_NAME).build();
		} catch (IOException | GeneralSecurityException e) {
			this.logger.error("Sorry, an error occured during the authorization", e);
		}
	}

	/**
	 * This method lists the next events from the calendar
	 * 
	 * @param number
	 *            number of events the user wants to get
	 * @return event summary
	 */
	public String getEvents(String number) {
		try {
			this.eventList.clear();
			DateTime now = new DateTime(System.currentTimeMillis());
			Events events = this.service.events().list("primary").setMaxResults(Integer.valueOf(number)).setTimeMin(now)
					.setOrderBy("startTime").setSingleEvents(true).execute();
			List<Event> items = events.getItems();
			if (items.isEmpty()) {
				return "No upcoming events found.";
			}
			for (Event event : items) {
				DateTime start = event.getStart().getDateTime();
				eventCalc(start, event);
			}
			return "You have following upcoming events:\n" + String.join("\n", this.eventList);
		} catch (IOException e) {
			this.logger.error("Sorry, I am not able to get your events.", e);
			return "An error occured.";
		}

	}

	/**
	 * This method contains the logic to show the calendar events today
	 * 
	 * @return the events of the actual day
	 */
	public String getEventsToday() {
		try {
			this.eventList.clear();
			DateTime now = new DateTime(System.currentTimeMillis());
			Events events = this.service.events().list("primary").setTimeMin(now).setOrderBy("startTime")
					.setSingleEvents(true).execute();
			List<Event> items = events.getItems();
			if (items.isEmpty()) {
				return "No upcoming events found.";
			}
			for (Event event : items) {
				String eventdatetime = "";
				String eventdate = "";
				if (event.getStart().getDateTime() != null) {
					eventdatetime = event.getStart().getDateTime().toString().substring(0, 10);
				} else if (event.getStart().getDate() != null) {
					eventdate = event.getStart().getDate().toString().substring(0, 10);
				}
				if (eventdatetime.equals(now.toString().substring(0, 10))) {
					DateTime start = event.getStart().getDateTime();
					eventCalc(start, event);
				} else if (eventdate.equals(now.toString().substring(0, 10))) {
					eventCalc(null, event);
				}
			}
			if (this.eventList.isEmpty()) {
				return "There are no events today.";
			}
			return "You have following upcoming events:\n" + String.join("\n", this.eventList);
		} catch (IOException e) {
			this.logger.error("Sorry, I am not able to get your events.", e);
			return "An error occured.";
		}
	}

	/**
	 * This method formats the date and time of the events and adds them into the list
	 * 
	 * @param start
	 *            start time of the event
	 * @param event
	 *            data of the event
	 */
	public void eventCalc(DateTime start, Event event) {
		String eventData;
		if (start != null) {
			String startData = start.toString();
			String[] parts = startData.split("T");
			String startDate = parts[0];
			String[] dayParts = startDate.split("-");
			String day = dayParts[2];
			String month = dayParts[1];
			String year = dayParts[0];
			String yearpart1 = year.substring(0, 2);
			String yearpart2 = year.substring(2, 4);
			String startTime = parts[1];
			DateTime endtime = event.getEnd().getDateTime();
			eventData = event.getSummary() + " on the " + ordinal(Integer.parseInt(day)) + " of " + getMonth(month)
					+ " " + Integer.parseInt(yearpart1) + Integer.parseInt(yearpart2) + " at "
					+ startTime.substring(0, 5) + " until " + endtime.toString().substring(11, 16) + "\n";
			this.eventList.add(eventData);
		}
		if (start == null) {
			start = event.getStart().getDate();
			String startData = start.toString();
			String[] dayParts = startData.split("-");
			String day = dayParts[2];
			String month = dayParts[1];
			String year = dayParts[0];
			String yearpart1 = year.substring(0, 2);
			String yearpart2 = year.substring(2, 4);
			eventData = event.getSummary() + " on the " + ordinal(Integer.parseInt(day)) + " of " + getMonth(month)
					+ " " + Integer.parseInt(yearpart1) + Integer.parseInt(yearpart2) + " all day long." + "\n";
			this.eventList.add(eventData);
		}
	}

	/**
	 * This method decides in which month the event is
	 * 
	 * @param month
	 *            month of the event as a number in a String
	 * @return current month of year as String (MMMM), e.g. May
	 */
	public String getMonth(String month) {
		String monthString = "";
		if (month.equals("01")) {
			monthString = "January";
		} else if (month.equals("02")) {
			monthString = "February";
		} else if (month.equals("03")) {
			monthString = "March";
		} else if (month.equals("04")) {
			monthString = "April";
		} else if (month.equals("05")) {
			monthString = "May";
		} else if (month.equals("06")) {
			monthString = "June";
		} else if (month.equals("07")) {
			monthString = "July";
		} else if (month.equals("08")) {
			monthString = "August";
		} else if (month.equals("09")) {
			monthString = "September";
		} else if (month.equals("10")) {
			monthString = "October";
		} else if (month.equals("11")) {
			monthString = "November";
		} else if (month.equals("12")) {
			monthString = "December";
		}
		return monthString;
	}

	/**
	 * A method to convert the integer day to an ordinal (from 1 to 31)
	 * 
	 * @param i
	 *            the day as integer
	 * @return the day as ordinal, e.g. 1st
	 */
	public static String ordinal(int i) {
		String[] ordinals = { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		if (10 < i && i < 14) {
			return i + "th";
		}
		return i + ordinals[i % 10];
	}

}