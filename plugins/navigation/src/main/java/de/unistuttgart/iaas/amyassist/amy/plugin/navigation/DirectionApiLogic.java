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

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class implements the logic that is needed to call the DirectionsApiCalls and processing the results
 * @author Lars Buttgereit
 */
@Service
public class DirectionApiLogic {

	@Reference
	private DirectionsApiCalls calls;
	
/*	public DirectionsRoute fromTo() {
		DirectionsRoute[] routes = this.calls.fromToWithDepartureTime("Friolzheim", "Universität Stuttgart", getTravelMode("Car"), DateTime.now());
		ReadableInstant minTime = null;
		for(DirectionsRoute route : routes) {
			for(DirectionsLeg leg : route.legs) {
				if(minTime == null) {
					minTime = leg.arrivalTime;
				}
				else if(minTime.)
			}
		}
		
	}*/
	
	private TravelMode getTravelMode(String mode) {
		switch(mode.toLowerCase()) {
		case "driving":
		case "car":
			return TravelMode.DRIVING;
		case "bicycling":
		case "bike":
			return TravelMode.BICYCLING;
		case "transit":
		case "public transport":
			return TravelMode.TRANSIT;
		case "walking":
		case "walk":
			return TravelMode.WALKING;
		default:
			return null;
		}
	}
	
}
