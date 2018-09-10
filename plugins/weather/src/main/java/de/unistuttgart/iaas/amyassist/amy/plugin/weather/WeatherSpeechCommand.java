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

package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityProvider;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.core.persistence.Persistence;
import de.unistuttgart.iaas.amyassist.amy.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;

/**
 * 
 * Speech class for the weather plugin
 * 
 * @author Benno Krauss
 */
@SpeechCommand
public class WeatherSpeechCommand {
	/** The name for the persistence used. */
	public static final String PERSISTENCE_NAME = "WeatherCurrentLocation";
	private static final int PERSISTENCE_ID = 1;
	private static final String NO_LOCATION_STRING = "No location configured.";

	@Reference
	private WeatherLogic weatherLogic;

	@Reference
	private LocationRegistry locationRegistry;

	@Reference
	private Persistence persistence;

	@PostConstruct
	private void init() {
		this.persistence.register(CurrentLocationPersistence.class);
	}

	private @Nullable Location getCurrentLocation() {
		EntityManager em = this.persistence.getEntityManager(PERSISTENCE_NAME);
		CurrentLocationPersistence result;
		try {
			result = em
					.createQuery("SELECT l FROM " + CurrentLocationPersistence.class.getName()
							+ " l WHERE l.primaryId = :id", CurrentLocationPersistence.class)
					.setParameter("id", PERSISTENCE_ID).getSingleResult();
			return result.getCurrentLocation();
		} catch (NoResultException e) { // NOSONAR
			Location loc = this.locationRegistry.getAll().get(0);

			setCurrentLocation(loc);
			return loc;
		}
	}

	private void setCurrentLocation(Location data) {
		EntityManager em = this.persistence.getEntityManager(PERSISTENCE_NAME);
		CurrentLocationPersistence d = new CurrentLocationPersistence(data, PERSISTENCE_ID);
		if (em.contains(d)) {
			em.getTransaction().begin();
			em.merge(d);
			em.getTransaction().commit();
		} else {
			em.getTransaction().begin();
			em.persist(d);
			em.getTransaction().commit();
			em.detach(d);
		}
	}

	private int round(double value) {
		return (int) Math.round(value);
	}

	private String dateString(long timeStamp, String timezone) {
		DateFormat dfm = new SimpleDateFormat("HH:mm");
		dfm.setTimeZone(TimeZone.getTimeZone(timezone));
		return dfm.format(timeStamp * 1000);
	}

	private String stringifyDayWeatherReport(String preamble, WeatherReportDay report, String timezone, boolean tldr) {
		String result = preamble + report.getSummary();
		if (report.getPrecipProbability() > 0) {
			result += " " + round(report.getPrecipProbability() * 100) + "% probability of " + report.getPrecipType()
					+ ".";
		}
		result += " Between " + round(report.getTemperatureMin()) + " and " + round(report.getTemperatureMax()) + "°C.";

		if (!tldr) {
			result += " Sunrise is at " + dateString(report.getSunriseTime(), timezone) + " and sunset at "
					+ dateString(report.getSunsetTime(), timezone);
		}
		return result;
	}

	private String stringifyWeekWeatherReport(String preamble, WeatherReportWeek report) {
		return preamble + report.getSummary();
	}

	/**
	 * speech command for the weather forecast for today
	 * 
	 * @param entities
	 *            not set here
	 * @return the weather forecast
	 */
	@Intent()
	public String weatherToday(Map<String, EntityData> entities) {
		Location curr = getCurrentLocation();
		if (curr == null)
			return NO_LOCATION_STRING;
		WeatherReport report = this.weatherLogic.getWeatherReport(curr.getPersistentId());

		return stringifyDayWeatherReport("This is the weather report for today.", report.getWeek().getDays()[0],
				report.getTimezone(), false);
	}

	/**
	 * speech command for the weather forecast for tomorrow
	 * 
	 * @param entities
	 *            not set here
	 * @return the weather forecast
	 */
	@Intent()
	public String weatherTomorrow(Map<String, EntityData> entities) {
		Location curr = getCurrentLocation();
		if (curr == null)
			return NO_LOCATION_STRING;
		WeatherReport report = this.weatherLogic.getWeatherReport(curr.getPersistentId());

		return stringifyDayWeatherReport("This is the weather report for tomorrow.", report.getWeek().getDays()[1],
				report.getTimezone(), false);
	}

	/**
	 * speech command for the weather forecast for the week
	 * 
	 * @param entities
	 *            not set here
	 * @return the weather forecast
	 */
	@Intent()
	public String weatherWeek(Map<String, EntityData> entities) {
		Location curr = getCurrentLocation();
		if (curr == null)
			return NO_LOCATION_STRING;
		WeatherReport report = this.weatherLogic.getWeatherReport(curr.getPersistentId());
		return stringifyWeekWeatherReport("This is the weather report for the week.", report.getWeek());
	}

	/**
	 * speech command for the weather forecast for the weekend
	 * 
	 * @param entities
	 *            not set here
	 * @return the weather forecast
	 */
	@Intent()
	public String weatherWeekend(Map<String, EntityData> entities) {
		Location curr = getCurrentLocation();
		if (curr == null)
			return NO_LOCATION_STRING;
		WeatherReport report = this.weatherLogic.getWeatherReport(curr.getPersistentId());
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of(report.getTimezone()));

		DayOfWeek weekday = now.getDayOfWeek();
		switch (weekday) {
		case SATURDAY:
			if (report.getWeek().getDays().length < 2)
				throw new IllegalStateException("WeatherAPI not working as expected");
			return "Today, " + stringifyDayWeatherReport("", report.getWeek().getDays()[0], report.getTimezone(), true)
					+ " and tomorrow, "
					+ stringifyDayWeatherReport("", report.getWeek().getDays()[1], report.getTimezone(), true);
		case SUNDAY:
			return "Today, " + stringifyDayWeatherReport("", report.getWeek().getDays()[0], report.getTimezone(), true);
		default:
			// Get weekend days
			String saturdayReport = null;
			String sundayReport = null;
			for (WeatherReportDay d : report.getWeek().getDays()) {
				Instant instant = Instant.ofEpochSecond(d.getTimestamp());
				ZonedDateTime date = ZonedDateTime.ofInstant(instant, ZoneId.of(report.getTimezone()));
				DayOfWeek day = date.getDayOfWeek();
				if (day == DayOfWeek.SATURDAY) {
					saturdayReport = stringifyDayWeatherReport("", d, report.getTimezone(), true);
				} else if (day == DayOfWeek.SUNDAY) {
					sundayReport = stringifyDayWeatherReport("", d, report.getTimezone(), true);
				}
			}
			return "On Saturday, " + saturdayReport + " and on Sunday " + sundayReport;
		}
	}

	/**
	 * speech command to set a new weather location. only registry entries are allowed
	 * 
	 * @param entities
	 *            input. contains the name of the tag
	 * @return return the name of the location
	 */
	@Intent()
	public String setLocation(Map<String, EntityData> entities) {
		for (Location loc : this.locationRegistry.getAll()) {
			if (loc.getTag().equalsIgnoreCase(entities.get("weatherlocation").getString())) {
				this.setCurrentLocation(loc);
				return loc.getName();
			}
		}
		return "new location not found";
	}

	/**
	 * provide all location tags to the speech
	 * 
	 * @return all tags
	 */
	@EntityProvider("weatherlocation")
	public List<String> getAllLocationTags() {
		List<String> locationNames = new ArrayList<>();
		for (Location loc : this.locationRegistry.getAll()) {
			locationNames.add(loc.getTag());
		}
		return locationNames;
	}
}
