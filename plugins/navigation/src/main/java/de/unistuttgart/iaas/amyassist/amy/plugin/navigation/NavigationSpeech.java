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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;

/**
 * Speech class for the navigation plugin
 * 
 * @author Lars Buttgereit
 */
@Service
@SpeechCommand
public class NavigationSpeech {
	@Reference
	private DirectionApiLogic logic;

	@Reference
	private Environment environment;

	@Reference
	private RegistryConnection registryConnection;

	@Reference
	private Logger logger;

	private static final String WRONG_TIME = "Time Input is wrong. Please try again";
	private static final String END_KEY = "end";
	private static final String START_KEY = "start";

	/**
	 * speech command for 'when i have to leave' feature
	 * 
	 * @param entities
	 *            input. contains start and end location and arrival time
	 * @return output string
	 */
	@Intent()
	public String goToAt(Map<String, EntityData> entities) {
		if (entities.get("time") != null) {
			DateTime time = dateTimeConversion(entities.get("time"));
			ReadableInstant outputTime = null;
			outputTime = this.logic.whenIHaveToGo(
					this.registryConnection.getAddress(cutEndWords(entities.get(START_KEY).getString(), "to")),
					this.registryConnection.getAddress(cutEndWords(entities.get(END_KEY).getString(), "by")),
					this.logic.getTravelMode(entities.get("mode").getString().trim()), time);
			if (outputTime != null) {
				if (outputTime.get(DateTimeFieldType.minuteOfHour()) > 9) {
					return "You should go at ".concat(String.valueOf(outputTime.get(DateTimeFieldType.hourOfDay())))
							.concat(":").concat(String.valueOf(outputTime.get(DateTimeFieldType.minuteOfHour())));
				}
				return "You should go at ".concat(String.valueOf(outputTime.get(DateTimeFieldType.hourOfDay())))
						.concat(":0").concat(String.valueOf(outputTime.get(DateTimeFieldType.minuteOfHour())));
			}
			return "You are too late";
		}
		return WRONG_TIME;
	}

	/**
	 * speech command to get the best transportation for the given route
	 * 
	 * @param entities
	 *            input. contains the start and end location and the departure time
	 * @return a output string
	 */
	@Intent()
	public String bestTransport(Map<String, EntityData> entities) {
		if (entities.get("time") != null) {
			DateTime time = dateTimeConversion(entities.get("time"));
			BestTransportResult result = this.logic.getBestTransportInTime(
					this.registryConnection.getAddress(cutEndWords(entities.get(START_KEY).getString(), "to")),
					this.registryConnection.getAddress(cutEndWords(entities.get(END_KEY).getString(), "at")), time);
			if (result != null) {
				return "The best transport Mode is ".concat(result.getMode().toString()).concat(".\n")
						.concat(result.routeToShortString());
			}
		}
		return WRONG_TIME;
	}

	/**
	 * speech command to get route informations
	 * 
	 * @param entities
	 *            entities input. contains the start and end location and transportation
	 * @return a output string
	 */
	@Intent()
	public Response routeFromtTo(Map<String, EntityData> entities) {

		DateTime time = entities.get("time") != null ? dateTimeConversion(entities.get("time")) : DateTime.now();

		BestTransportResult result = this.logic.fromToWithDeparture(
			this.registryConnection.getAddress(cutEndWords(entities.get(START_KEY).getString(), "to")),
			this.registryConnection.getAddress(cutEndWords(entities.get(END_KEY).getString(), "by")),
			this.logic.getTravelMode(entities.get("mode").getString().trim()),
			time
		);

		return Response.text(result.routeToShortString()).widget("app-route-widget")
				.attachment(result.routeToWidgetInfo()).build();
	}

	/**
	 * helper method to cut words at the end for example to or by that comes from the speech
	 * 
	 * @param location
	 *            location input
	 * @param cut
	 *            string to cut
	 * @return the location string without the end word
	 */
	private String cutEndWords(String location, String cut) {
		if (location.endsWith(cut)) {
			return location.substring(0, location.length() - cut.length() - 1);
		}
		return location.trim();
	}

	/**
	 * Transform the LocalDateTime or LocalTime from EntityData to a DateTime from the api.
	 * 
	 * @param inputTime
	 *            the EntityData from the time entity
	 * @return a DateTime object
	 */
	private DateTime dateTimeConversion(EntityData inputTime) {
		DateTime outputTime = null;
		try {
			outputTime = new DateTime(
					inputTime.getDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		} catch (DateTimeParseException e) {
			LocalDateTime now = this.environment.getCurrentLocalDateTime();
			outputTime = new DateTime(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
					inputTime.getTime().getHour(), inputTime.getTime().getMinute());
		}
		return outputTime;
	}

	/**
	 * provide location tags to the speech
	 * 
	 * @return a list of all tags
	 */
	@EntityProvider("startregistry")
	@EntityProvider("endregistry")
	public List<String> getLocationTags() {
		return Arrays.asList(this.registryConnection.getAllLocationTags());
	}
}
