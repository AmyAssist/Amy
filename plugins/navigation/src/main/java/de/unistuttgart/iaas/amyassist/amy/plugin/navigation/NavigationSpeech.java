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

import java.util.Calendar;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * This class handle the speech input for the navigation plugin
 * 
 * @author Lars Buttgereit
 */
@Service
@SpeechCommand("navigate")
public class NavigationSpeech {

	private static final String LOCATIONS = "(home|work|mother)";

	@Reference
	private DirectionApiLogic logic;

	@Reference
	private RegistryConnection registry;

	/**
	 * speech command for 'be at' feature
	 * 
	 * @param strings
	 *            input
	 * @return output string
	 */
	@Grammar("be at " + LOCATIONS + " at # oh # from " + LOCATIONS)
	public String goToAt(String... strings) {
		ReadableInstant time = this.logic.whenIHaveToGo(this.registry.getAdresse(strings[8]),
				this.registry.getAdresse(strings[2]), TravelMode.DRIVING,
				formatTimes(Integer.parseInt(strings[6]), Integer.parseInt(strings[4])));
		if (time != null) {
			return String.valueOf(time.get(DateTimeFieldType.hourOfDay())).concat(":")
					.concat(String.valueOf(time.get(DateTimeFieldType.minuteOfHour())));
		}
		return "You are to late";
	}

	/**
	 * speech command for 'be at' feature with public transport
	 * 
	 * @param strings
	 *            input
	 * @return output string
	 */
	@Grammar("be at " + LOCATIONS + " at # oh # from " + LOCATIONS + " by ( bus | train | transit )")
	public String goToAtBy(String... strings) {
		ReadableInstant time = this.logic.whenIHaveToGo(this.registry.getAdresse(strings[8]),
				this.registry.getAdresse(strings[2]), TravelMode.TRANSIT,
				formatTimes(Integer.parseInt(strings[6]), Integer.parseInt(strings[4])));
		if (time != null) {
			return String.valueOf(time.get(DateTimeFieldType.hourOfDay())).concat(":")
					.concat(String.valueOf(time.get(DateTimeFieldType.minuteOfHour())));
		}
		return "You are to late";
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
		BestTransportResult result = this.logic.getBestTransportInTime(this.registry.getAdresse(strings[3]),
				this.registry.getAdresse(strings[5]), DateTime.now());
		return "The best transport Mode is ".concat(result.getMode().toString()).concat(".\n")
				+ result.getRoute().summary;
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
		return this.logic.fromTo(this.registry.getAdresse(strings[1]), this.registry.getAdresse(strings[3]),
				this.logic.getTravelMode(strings[5])).routeToShortString();
	}

	/**
	 * speech command for from A to B with ... at ...
	 * 
	 * @param strings
	 *            input
	 * @return output string
	 */
	@Grammar("from " + LOCATIONS + " to " + LOCATIONS + " by (car | transport | bike) at # oh #")
	public String routeFromToWithTime(String... strings) {
		return this.logic.fromToWithDeparture(this.registry.getAdresse(strings[1]),
				this.registry.getAdresse(strings[3]), this.logic.getTravelMode(strings[5]),
				formatTimes(Integer.parseInt(strings[9]), Integer.parseInt(strings[7]))).routeToShortString();
	}

	/**
	 * get a formated time. set the actual date and time to the given time of the same date.
	 * 
	 * @param min
	 * @param hour
	 * @return the modifed date
	 */
	private DateTime formatTimes(int min, int hour) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		return new DateTime(calendar.getTime());
	}
}
