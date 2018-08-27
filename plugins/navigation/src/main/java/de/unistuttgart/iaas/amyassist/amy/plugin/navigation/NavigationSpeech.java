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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.EntityProvider;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.SpeechCommand;

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

	private static final String WRONG_PLACE = "One or more places are not in the registry";
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
		LocalDateTime now = this.environment.getCurrentLocalDateTime();
		LocalTime inputTime = entities.get("time").getTime();
		if (inputTime != null) {
			DateTime time = new DateTime(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), inputTime.getHour(),
					inputTime.getMinute());
			ReadableInstant outputTime = null;
			outputTime = this.logic.whenIHaveToGo(
					this.registryConnection.getAddress(entities.get(START_KEY).getString()),
					this.registryConnection.getAddress(entities.get(END_KEY).getString()),
					this.logic.getTravelMode(entities.get("mode").getString().trim()), time);
			if (outputTime != null) {
				return "You should go at ".concat(String.valueOf(outputTime.get(DateTimeFieldType.hourOfDay())))
						.concat(":").concat(String.valueOf(outputTime.get(DateTimeFieldType.minuteOfHour())));
			}
			return "You are too late";
		}
		return WRONG_PLACE;
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
		LocalDateTime now = this.environment.getCurrentLocalDateTime();
		LocalTime inputTime = entities.get("time").getTime();
		DateTime time = null;
		if (inputTime != null) {
			time = new DateTime(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), inputTime.getHour(),
					inputTime.getMinute());
		} else if (entities.get("time").getString().trim().equalsIgnoreCase("now")) {
			time = DateTime.now();
		}
		if (time != null) {
			BestTransportResult result = this.logic.getBestTransportInTime(
					this.registryConnection.getAddress(entities.get(START_KEY).getString()),
					this.registryConnection.getAddress(entities.get(END_KEY).getString()), time);
			if (result != null) {
				return "The best transport Mode is ".concat(result.getMode().toString()).concat(".\n")
						.concat(result.routeToShortString());
			}
		}
		return WRONG_PLACE;
	}

	/**
	 * speech command to get route informations
	 * 
	 * @param entities
	 *            entities input. contains the start and end location and transportation
	 * @return a output string
	 */
	@Intent()
	public String routeFromtTo(Map<String, EntityData> entities) {
		return this.logic
				.fromToWithDeparture(this.registryConnection.getAddress(entities.get(START_KEY).getString()),
						this.registryConnection.getAddress(entities.get(END_KEY).getString()),
						this.logic.getTravelMode(entities.get("mode").getString().trim()), DateTime.now())
				.routeToShortString();
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
