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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityProvider;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.registry.Location;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * Speech class for the weather plugin
 * 
 * @author Benno Krauss
 */
@SpeechCommand
public class WeatherSpeechCommand {

	@Reference
	private WeatherDarkSkyAPI weatherAPI;

	@Reference
	private LocationRegistry locationRegistry;

	/**
	 * speech command for the weather forecast for today
	 * 
	 * @param entities
	 *            not set here
	 * @return the weather forecast
	 */
	@Intent()
	public String weatherToday(Map<String, EntityData> entities) {
		return this.weatherAPI.getReportToday().toString();
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
		return this.weatherAPI.getReportTomorrow().toString();
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
		return this.weatherAPI.getReportWeek().toString();
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

		WeatherReportWeek report = this.weatherAPI.getReportWeek();
		Calendar c = Calendar.getInstance();

		int weekday = c.get(Calendar.DAY_OF_WEEK);
		switch (weekday) {
		case Calendar.SATURDAY:
			if (report.days.length < 2) {
				throw new RuntimeException("WeatherAPI not working as expected");
			}
			return "Today, " + report.days[0].shortDescription() + " and tomorrow, "
					+ report.days[1].shortDescription();
		case Calendar.SUNDAY:
			return "Today, " + report.days[0].shortDescription();
		default:
			// Get weekend days
			String saturdayReport = null;
			String sundayReport = null;
			for (WeatherReportDay d : report.days) {
				c.setTime(new Date(d.getTimestamp() * 1000));
				weekday = c.get(Calendar.DAY_OF_WEEK);
				if (weekday == Calendar.SATURDAY) {
					saturdayReport = d.shortDescription();
				} else if (weekday == Calendar.SUNDAY) {
					sundayReport = d.shortDescription();
				}
			}
			return "On Saturday, " + saturdayReport + " and on Sunday " + sundayReport;
		}
	}

	/**
	 * speech command to set a new weather location. only registry entries are allowed
	 * 
	 * @param entities input. contains the name of the tag
	 * @return return the name of the location
	 */
	@Intent()
	public String setLocation(Map<String, EntityData> entities) {
		for (Location loc : this.locationRegistry.getAll()) {
			if (loc.getTag().equalsIgnoreCase(entities.get("weatherlocation").getString())) {
				this.weatherAPI.setLocation(loc.getPersistentId());
				return loc.getName();
			}
		}
		return "new location not found";
	}
	
	/**
	 * provide all location tags to the speech
	 * @return all tags
	 */
	@EntityProvider("weatherlocation")
	public List<String> getAllLocationTags(){
		List<String> locationNames = new ArrayList<>();
		for(Location loc : this.locationRegistry.getAll()) {
			locationNames.add(loc.getTag());
		}
		return locationNames;
	}
}
