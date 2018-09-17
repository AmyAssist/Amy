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

import org.apache.commons.lang3.tuple.Pair;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityProvider;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.plugin.weather.WeatherLogic.GeoCoordinatePair;
import de.unistuttgart.iaas.amyassist.amy.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;
import de.unistuttgart.iaas.amyassist.amy.registry.geocoder.Geocoder;
import de.unistuttgart.iaas.amyassist.amy.registry.geocoder.GeocoderException;

/**
 * 
 * Speech class for the weather plugin
 * 
 * @author Benno Krauss
 */
@SpeechCommand
public class WeatherSpeechCommand {
	/** The name for the persistence used. */
	private static final String NO_LOCATION_STRING = "No location configured.";
	private static final String CURRENT_LOCATION_KEY = "currentLocation";

	@Reference
	private WeatherLogic weatherLogic;

	@Reference
	private LocationRegistry locationRegistry;

	@Reference
	private IStorage storage;

	@Reference
	Geocoder geocoder;

	private @Nullable GeoCoordinatePair getCurrentLocation() {
		if (!this.storage.has(CURRENT_LOCATION_KEY)) {
			if (this.locationRegistry.getAll().isEmpty())
				return null;

			setCurrentLocation(new GeoCoordinatePair(this.locationRegistry.getAll().get(0)));
		}
		return new GeoCoordinatePair(this.storage.get(CURRENT_LOCATION_KEY));
	}

	private void setCurrentLocation(GeoCoordinatePair loc) {
		this.storage.put(CURRENT_LOCATION_KEY, loc.getStringRepresentation());
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
		String result = preamble + " " + report.getSummary();
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
		return preamble + " " + report.getSummary();
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
		GeoCoordinatePair curr = getCurrentLocation();
		if (curr == null)
			return NO_LOCATION_STRING;
		WeatherReport report = this.weatherLogic.getWeatherReport(curr);

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
		GeoCoordinatePair curr = getCurrentLocation();
		if (curr == null)
			return NO_LOCATION_STRING;
		WeatherReport report = this.weatherLogic.getWeatherReport(curr);

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
		GeoCoordinatePair curr = getCurrentLocation();
		if (curr == null)
			return NO_LOCATION_STRING;
		WeatherReport report = this.weatherLogic.getWeatherReport(curr);
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
		GeoCoordinatePair curr = getCurrentLocation();
		if (curr == null)
			return NO_LOCATION_STRING;
		WeatherReport report = this.weatherLogic.getWeatherReport(curr);
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
	 * tells user if its gonna rain today / tomorrow etc
	 * 
	 * @param entities
	 *            information
	 * @return the answer
	 */
	@Intent
	public String rainCheck(Map<String, EntityData> entities) {
		WeatherReport report;
		if (entities.get("locationname") != null) {
			String locationName = entities.get("locationname").getString();
			try {
				Pair<Double, Double> coordinates = this.geocoder.geocodeAddress(locationName);
				GeoCoordinatePair pair = new GeoCoordinatePair(coordinates.getRight(), coordinates.getLeft());
				report = this.weatherLogic.getWeatherReport(pair);
			} catch (GeocoderException e) {
				return "I couldn't find this location";
			}
		} else {
			GeoCoordinatePair curr = getCurrentLocation();
			if (curr == null)
				return NO_LOCATION_STRING;
			report = this.weatherLogic.getWeatherReport(curr);
		}

		String chanceOf = "% chance of ";
		String timespan = entities.get("timespan").getString();
		if (timespan.equals("today")) {
			WeatherReportDay today = report.getWeek().getDays()[0];
			return round(today.getPrecipProbability() * 100) + chanceOf + today.getPrecipType() + " today";
		} else if (timespan.equals("tomorrow")) {
			WeatherReportDay tomorrow = report.getWeek().getDays()[1];
			return round(tomorrow.getPrecipProbability() * 100) + chanceOf + tomorrow.getPrecipType() + " tomorrow";
		} else if (timespan.equals("on the weekend")) {
			StringBuilder builder = new StringBuilder();
			String sat = "";
			String sun = "";
			for (WeatherReportDay d : report.getWeek().getDays()) {
				Instant instant = Instant.ofEpochSecond(d.getTimestamp());
				ZonedDateTime date = ZonedDateTime.ofInstant(instant, ZoneId.of(report.getTimezone()));
				DayOfWeek day = date.getDayOfWeek();
				if (day == DayOfWeek.SATURDAY) {
					sat = round(d.getPrecipProbability() * 100) + chanceOf + d.getPrecipType() + " on Saturday";
				} else if (day == DayOfWeek.SUNDAY) {
					sun = round(d.getPrecipProbability() * 100) + chanceOf + d.getPrecipType() + " on Sunday";
				}
			}
			builder.append(sat + "\n" + sun);
			return builder.toString();
		}

		return "i don't understand the date";
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
				this.setCurrentLocation(new GeoCoordinatePair(loc));
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

	@Intent()
	public String weatherAtLocation(Map<String, EntityData> entities) {
		String locationName = entities.get("locationname").getString();
		try {
			Pair<Double, Double> coordinates = this.geocoder.geocodeAddress(locationName);
			GeoCoordinatePair pair = new GeoCoordinatePair(coordinates.getRight(), coordinates.getLeft());
			WeatherReport report = this.weatherLogic.getWeatherReport(pair);
			return stringifyDayWeatherReport("This is the weather report for " + locationName + ". ",
					report.getWeek().getDays()[0], report.getTimezone(), false);
		} catch (GeocoderException e) {
			return "I couldn't find this location";
		}
	}
}
