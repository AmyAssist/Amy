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

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.TravelMode;

/**
 * This class is needed to store the best route and the transport type
 * @author Lars Buttgereit
 */
public class BestTransportResult {
	private TravelMode mode;
	private DirectionsLeg route;
	
	/**
	 * constructor
	 * @param mode
	 * @param route
	 */
	public BestTransportResult(TravelMode mode, DirectionsLeg route) {
		this.mode = mode;
		this.route = route;
	}

	/**
	 * Get's {@link #mode mode}
	 * @return  mode
	 */
	public TravelMode getMode() {
		return this.mode;
	}

	/**
	 * Get's {@link #route route}
	 * @return  route
	 */
	public DirectionsLeg getRoute() {
		return this.route;
	}
	
}
