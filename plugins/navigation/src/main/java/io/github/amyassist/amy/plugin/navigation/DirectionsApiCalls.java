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

package io.github.amyassist.amy.plugin.navigation;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.joda.time.ReadableInstant;
import org.slf4j.Logger;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * This class handle all call to the Google maps services library
 * 
 * @author Lars Buttgereit
 */
@Service
public class DirectionsApiCalls {
	@Reference
	private Properties properties;
	@Reference
	private Logger logger;

	private GeoApiContext context;

	private static final String ERROR_TAG = "Google API Error";
	private static final String GOOGLE_STATIC_API_KEY = "GOOGLE_STATIC_API_KEY";

	/**
	 * load the api key and create the context to create calls
	 */
	@PostConstruct
	public void init() {
		this.context = new GeoApiContext.Builder().apiKey(this.properties.getProperty("GOOGLE_API_KEY")).maxRetries(1)
				.retryTimeout(2, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).build();
	}

	/**
	 * 
	 * @param origin
	 *            of the requested route
	 * @param destination
	 *            of the requested route
	 * @param mode
	 *            e.g. driving, walking, etc.
	 * @return a array with routes
	 */
	public DirectionsRoute[] fromTo(String origin, String destination, TravelMode mode) {
		return errorHandling(new DirectionsApiRequest(this.context).origin(origin).destination(destination).mode(mode)
				.units(Unit.METRIC).alternatives(true));
	}

	/**
	 * 
	 * @param origin
	 *            of the requested route
	 * @param destination
	 *            of the requested route
	 * @param mode
	 *            e.g. driving, walking, etc.
	 * @param arrivalTime
	 *            for the route
	 * @return a array with routes
	 */
	public DirectionsRoute[] fromToWithArrivalTime(String origin, String destination, TravelMode mode,
			ReadableInstant arrivalTime) {
		return errorHandling(new DirectionsApiRequest(this.context).origin(origin).destination(destination).mode(mode)
				.arrivalTime(arrivalTime).alternatives(true));
	}

	/**
	 * 
	 * @param origin
	 *            of the requested route
	 * @param destination
	 *            of the requested route
	 * @param mode
	 *            e.g. driving, walking, etc.
	 * @param departureTime
	 *            for the route
	 * @return a array with routes
	 */
	public DirectionsRoute[] fromToWithDepartureTime(String origin, String destination, TravelMode mode,
			ReadableInstant departureTime) {
		return errorHandling(new DirectionsApiRequest(this.context).origin(origin).destination(destination).mode(mode)
				.departureTime(departureTime).alternatives(true).trafficModel(TrafficModel.BEST_GUESS));
	}

	/**
	 * handle the thrown exception from the request
	 * 
	 * @param request
	 *            to check for exceptions
	 * @return the result from the call or a empty array
	 */
	private DirectionsRoute[] errorHandling(DirectionsApiRequest request) {
		try {
			return request.await().routes;
		} catch (InterruptedException e) {
			this.logger.warn(ERROR_TAG, e);
			Thread.currentThread().interrupt();
		} catch (ApiException | IOException | IllegalStateException e) {
			this.logger.warn(ERROR_TAG, e);
		}
		return new DirectionsRoute[0];
	}

	/**
	 * Get the "google maps static" api-key from the configuration.
	 * This key is restricted to websites from our domain (via referer-header)
	 * @return api key
	 */
	String getStaticAPIKey() {
		return this.properties.getProperty(GOOGLE_STATIC_API_KEY);
	}
}
