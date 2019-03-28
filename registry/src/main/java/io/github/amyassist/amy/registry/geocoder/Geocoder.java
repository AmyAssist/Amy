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

package io.github.amyassist.amy.registry.geocoder;

import java.util.Properties;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.tuple.Pair;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import io.github.amyassist.amy.core.configuration.ConfigurationManager;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;

/**
 * Simple google geocoder interface
 *
 * @author Benno Krauß
 */
@Service
public class Geocoder {

	private static final String GOOGLE_API_PATH = "maps/api/geocode/json";
	private static final String GOOGLE_API_HOST = "maps.google.com";
	private static final String INVALID_RESPONSE_MESSAGE = "Invalid response";
	private static final String CONFIG_NAME = "geocoder.config";
	private static final String API_CREDENTIALS_KEY = "GOOGLE_API_KEY";

	@Reference
	private ConfigurationManager configurationManager;

	/**
	 * Geocode an address
	 * 
	 * @param address
	 *            the full address string
	 * @return a pair of latitude and longitude values
	 * @throws GeocoderException
	 *             if any error occurs
	 */
	public Pair<Double, Double> geocodeAddress(String address) throws GeocoderException {
		String response = ClientBuilder.newClient()
				.target(UriBuilder.fromPath(GOOGLE_API_PATH).host(GOOGLE_API_HOST).scheme("https"))
				.queryParam("address", address).queryParam("key", this.getAPIKey()).request(MediaType.APPLICATION_JSON)
				.get(String.class);

		JsonObject j = Json.parse(response).asObject();
		String status = j.getString("status", null);
		if (!(status.equals("OK") || status.equals("ZERO_RESULTS"))) {
			throw new GeocoderException(INVALID_RESPONSE_MESSAGE);
		}

		JsonArray results = getJson(j, "results", JsonArray.class);
		if (results.isEmpty()) {
			throw new GeocoderException("Address not found");
		}

		JsonValue result = results.get(0);
		if (!(result instanceof JsonObject)) {
			throw new GeocoderException(INVALID_RESPONSE_MESSAGE);
		}
		JsonObject resultObject = (JsonObject) result;

		JsonObject geometry = getJson(resultObject, "geometry", JsonObject.class);
		JsonObject location = getJson(geometry, "location", JsonObject.class);

		double lat = getJson(location, "lat", JsonValue.class).asDouble();
		double lng = getJson(location, "lng", JsonValue.class).asDouble();

		return Pair.of(lat, lng);
	}

	private String getAPIKey() throws GeocoderException {
		Properties p = this.configurationManager.getConfigurationWithDefaults(CONFIG_NAME);
		String value = p.getProperty(API_CREDENTIALS_KEY);
		if (value.isEmpty()) {
			throw new GeocoderException("Geocoder needs a config file with a value for: " + API_CREDENTIALS_KEY);
		}
		return value;
	}

	private static <T extends JsonValue> T getJson(JsonObject o, String member, Class<T> cls) throws GeocoderException {
		JsonValue r = o.get(member);
		if (!cls.isInstance(r)) {
			throw new GeocoderException(INVALID_RESPONSE_MESSAGE);
		}
		return cls.cast(r);
	}
}
