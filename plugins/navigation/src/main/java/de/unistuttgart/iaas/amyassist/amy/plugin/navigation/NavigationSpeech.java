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

import java.time.ZoneId;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.EntityData;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.Intent;
import de.unistuttgart.iaas.amyassist.amy.core.natlang.api.SpeechCommand;

/**
 * This class handle the speech input for the navigation plugin
 * 
 * @author Lars Buttgereit
 */
@SpeechCommand
public class NavigationSpeech {

	private static final String LOCATIONS = "(home|work|mother)";
	private static final String WRONG_PLACE = "One or more places are not in the registry";
	private static final String TIME_STRING = "((# o clock|(#|quarter)(past|to)#)|# x # [pm|am])";
	private static final String INVALID_INPUT = "Invlaid Input";

	@Reference
	private DirectionApiLogic logic;

	@Reference
	private Environment environment;

	@Reference
	private RegistryConnection registryConnection;

	@Reference
	private TimeFormatter timeFormatter;

	@Intent()
	public String testTime(Map<String, EntityData> entities) {
		if (entities.get("time").getTime() != null) {
			return entities.get("time").getTime().toString();
		}
		return "wrong time";
	}

	/**
	 * speech command for 'be at' feature
	 * 
	 * @param strings
	 *            input
	 * @return output string
	 */
	@Grammar("when i have to leave " + LOCATIONS + "to get" + LOCATIONS + "at " + TIME_STRING)
	public String goToAt(String... strings) {
		String[] rawTime;
		if (strings.length == 13) {
			String[] t = { strings[10], strings[11], strings[12] };
			rawTime = t;
		} else if (strings.length == 14) {
			String[] t = { strings[10], strings[11], strings[12], strings[13] };
			rawTime = t;
		} else {
			return INVALID_INPUT;
		}
		if (this.registryConnection.getAddress(strings[5]) != null
				&& this.registryConnection.getAddress(strings[8]) != null) {
			ReadableInstant time = this.logic.whenIHaveToGo(this.registryConnection.getAddress(strings[5]),
					this.registryConnection.getAddress(strings[8]), TravelMode.DRIVING, new DateTime(this.timeFormatter
							.formatTimes(rawTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
			if (time != null) {
				return "You should go at ".concat(String.valueOf(time.get(DateTimeFieldType.hourOfDay()))).concat(":")
						.concat(String.valueOf(time.get(DateTimeFieldType.minuteOfHour())));
			}
			return "You are too late";
		}
		return WRONG_PLACE;
	}

	/**
	 * speech command for 'be at' feature with public transport
	 * 
	 * @param strings
	 *            input
	 * @return output string
	 */
	@Grammar("when i have to leave " + LOCATIONS + "to get" + LOCATIONS + "at " + TIME_STRING
			+ " by ( bus | train | transit )")
	public String goToAtBy(String... strings) {
		String[] rawTime;
		if (strings.length == 15) {
			String[] t = { strings[10], strings[11], strings[12] };
			rawTime = t;
		} else if (strings.length == 16) {
			String[] t = { strings[10], strings[11], strings[12], strings[13] };
			rawTime = t;
		} else {
			return INVALID_INPUT;
		}
		if (this.registryConnection.getAddress(strings[5]) != null
				&& this.registryConnection.getAddress(strings[8]) != null) {
			ReadableInstant time = this.logic.whenIHaveToGo(this.registryConnection.getAddress(strings[5]),
					this.registryConnection.getAddress(strings[8]), TravelMode.TRANSIT, new DateTime(this.timeFormatter
							.formatTimes(rawTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
			if (time != null) {
				return "You should go at ".concat(String.valueOf(time.get(DateTimeFieldType.hourOfDay()))).concat(":")
						.concat(String.valueOf(time.get(DateTimeFieldType.minuteOfHour())));
			}
			return "You are too late";
		}
		return WRONG_PLACE;
	}

	/**
	 * speech command for best transport from A to B
	 * 
	 * @param strings
	 *            input
	 * @return output string
	 */
	@Grammar("best transport from " + LOCATIONS + " to " + LOCATIONS + " now")
	public String bestRouteSM(String... strings) {
		if (this.registryConnection.getAddress(strings[3]) != null
				&& this.registryConnection.getAddress(strings[5]) != null) {
			BestTransportResult result = this.logic.getBestTransportInTime(
					this.registryConnection.getAddress(strings[3]), this.registryConnection.getAddress(strings[5]),
					DateTime.now());
			return "The best transport Mode is ".concat(result.getMode().toString()).concat(".\n")
					.concat(result.routeToShortString());
		}
		return WRONG_PLACE;
	}

	/**
	 * speech command for from A to B with ...
	 * 
	 * @param strings
	 *            input
	 * @return output string
	 */
	@Grammar("from " + LOCATIONS + " to " + LOCATIONS + " by (car | transport | bike)")
	public String routeFromTo(String... strings) {
		if (this.registryConnection.getAddress(strings[1]) != null
				&& this.registryConnection.getAddress(strings[3]) != null) {
			return this.logic
					.fromTo(this.registryConnection.getAddress(strings[1]),
							this.registryConnection.getAddress(strings[3]), this.logic.getTravelMode(strings[5]))
					.routeToShortString();
		}
		return WRONG_PLACE;
	}

	/**
	 * speech command for from A to B with ... at ...
	 * 
	 * @param strings
	 *            input
	 * @return output string
	 */
	@Grammar("from " + LOCATIONS + " to " + LOCATIONS + " by (car | transport | bike) at " + TIME_STRING)
	public String routeFromToWithTime(String... strings) {
		String[] rawTime;
		if (strings.length == 10) {
			String[] t = { strings[7], strings[8], strings[9] };
			rawTime = t;
		} else if (strings.length == 11) {
			String[] t = { strings[7], strings[8], strings[9], strings[10] };
			rawTime = t;
		} else {
			return INVALID_INPUT;
		}
		if (this.registryConnection.getAddress(strings[1]) != null
				&& this.registryConnection.getAddress(strings[3]) != null) {
			return this.logic.fromToWithDeparture(this.registryConnection.getAddress(strings[1]),
					this.registryConnection.getAddress(strings[3]), this.logic.getTravelMode(strings[5]),
					new DateTime(this.timeFormatter.formatTimes(rawTime).atZone(ZoneId.systemDefault()).toInstant()
							.toEpochMilli()))
					.routeToShortString();
		}
		return WRONG_PLACE;
	}

}
