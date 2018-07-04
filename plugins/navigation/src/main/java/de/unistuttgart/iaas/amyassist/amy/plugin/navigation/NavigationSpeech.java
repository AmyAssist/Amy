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

	@Reference
	private DirectionApiLogic logic;

	// TODO replace work with registry entries
	@Grammar("be at work at # oh #")
	public String goToAt(String... strings) {
		ReadableInstant time = this.logic.whenIHaveToGo("Friolzheim", "Universität Stuttgart", TravelMode.DRIVING, formatTimes(Integer.parseInt(strings[6]), Integer.parseInt(strings[4])));
		if (time != null) {
			return String.valueOf(time.get(DateTimeFieldType.hourOfDay())).concat(":").concat(String.valueOf(time.get(DateTimeFieldType.minuteOfHour())));
		}
		return "You are to late";
	}
	
	@Grammar("be at work at # oh # by ( bus | train | transit )")
	public String GoToAtBy(String... strings) {
		ReadableInstant time = this.logic.whenIHaveToGo("Friolzheim", "Universität Stuttgart", TravelMode.TRANSIT, formatTimes(Integer.parseInt(strings[6]), Integer.parseInt(strings[4])));
		if (time != null) {
			return String.valueOf(time.get(DateTimeFieldType.hourOfDay())).concat(":").concat(String.valueOf(time.get(DateTimeFieldType.minuteOfHour())));
		}
		return "You are to late";
	}
	
	private DateTime formatTimes(int min, int hour) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		return new DateTime(calendar.getTime());
	}
}
