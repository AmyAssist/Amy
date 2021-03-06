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

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import io.github.amyassist.amy.plugin.navigation.rest.WidgetRouteInfo;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * This class is needed to store the best route and the transport type
 * 
 * @author Lars Buttgereit, Muhammed Kaya, Benno Krauß
 */
@XmlRootElement
public class BestTransportResult {
	private TravelMode mode;
	private DirectionsRoute route;

	private static final String THE_ROUTE_IS = "The route is ";
	private static final String LONG_AND_NEED = " long and you will need ";
	private static final String TIME_FORMAT = "HH:mm";

	/**
	 * constructor
	 */
	public BestTransportResult() {
		// Needed for JSON
	}

	/**
	 * constructor
	 * 
	 * @param mode
	 * @param route
	 */
	public BestTransportResult(TravelMode mode, DirectionsRoute route) {
		this.mode = mode;
		this.route = route;
	}

	/**
	 * Get's {@link #mode mode}
	 * 
	 * @return mode
	 */
	public TravelMode getMode() {
		return this.mode;
	}

	/**
	 * Get's {@link #route route}
	 * 
	 * @return route
	 */
	public DirectionsRoute getRoute() {
		return this.route;
	}

	/**
	 * Not implemented yet get a string with the detailed steps and times.
	 * 
	 * @return
	 */
	public String routeToLongString() {
		return null;
	}

	/**
	 * get a String with informations for example arrival, departure time, duration or distance
	 * 
	 * @return
	 */
	public String routeToShortString() {
		if (this.route != null && this.route.legs != null && this.route.legs[0] != null) {
			DirectionsLeg leg = this.route.legs[0];
			StringBuilder builder = new StringBuilder();
			switch (this.mode) {
			case DRIVING:
				if (leg.durationInTraffic != null) {
					return builder.append(THE_ROUTE_IS).append(leg.distance.humanReadable).append(LONG_AND_NEED)
							.append(leg.durationInTraffic.humanReadable).append(" time in traffic").toString();
				}
				return builder.append(THE_ROUTE_IS).append(leg.distance.humanReadable).append(LONG_AND_NEED)
						.append(leg.duration.humanReadable).append(" time").toString();
			case TRANSIT:
				int i = 0;
				while (leg.steps[i].transitDetails == null) {
					i++;
				}
				if (i > 0) {
					return builder.append("You should go at ").append(leg.departureTime.toString(TIME_FORMAT))
							.append(". departure time of the first public transport is ")
							.append(leg.steps[i].transitDetails.departureTime.toString(TIME_FORMAT))
							.append(" at the station ").append(leg.steps[i].transitDetails.departureStop.name)
							.append(" with line ").append(leg.steps[i].transitDetails.line.shortName).append(" to ")
							.append(leg.steps[i].transitDetails.headsign).append(". The arrival time is ")
							.append(leg.arrivalTime.toString(TIME_FORMAT)).toString();
				}
				return builder.append("Departure is ").append(leg.departureTime.toString(TIME_FORMAT)).append(" at ")
						.append(leg.steps[i].transitDetails.departureStop.name).append(" with line ")
						.append(leg.steps[i].transitDetails.line.shortName).append(" to ")
						.append(leg.steps[i].transitDetails.headsign).append(", arrival time is ")
						.append(leg.arrivalTime.toString(TIME_FORMAT)).toString();
			case BICYCLING:
				return builder.append(THE_ROUTE_IS).append(leg.distance.humanReadable).append(LONG_AND_NEED)
						.append(leg.duration.humanReadable).append(" time").toString();
			default:
				break;
			}
		}
		return "No route found";
	}

	private String urlEncode(String string) throws UnsupportedEncodingException {
		return URLEncoder.encode(string, StandardCharsets.UTF_8.name());
	}

	public WidgetRouteInfo routeToWidgetInfo(String mapsStaticAPIKey) {

		try {
			if (this.route.legs != null && this.route.legs.length > 0) {
				LatLng start = this.route.legs[0].startLocation;
				LatLng end = this.route.legs[0].endLocation;
				String travelMode = this.mode.toUrlValue();

				// Locale.ROOT is used to force points as decimal separators as those are requires by the gmaps web api
				String linkURLString = String.format(Locale.ROOT, "https://www.google.com/maps/dir/?api=1&" +
								"origin=%f,%f&destination=%f,%f&travelmode=%s",
						start.lat, start.lng, end.lat, end.lng, travelMode);
				URL link = new URL(linkURLString);


				String imageURLString = String.format(Locale.ROOT, "https://maps.googleapis.com/maps/api/staticmap?" +
								"size=300x300&path=enc:%s&markers=%s&markers=%s&key=%s",
						this.urlEncode(this.route.overviewPolyline.getEncodedPath()),
						this.urlEncode("color:blue|label:S|" + start.toUrlValue()),
						this.urlEncode("color:red|label:E|" + end.toUrlValue()),
						mapsStaticAPIKey
				);
				URL image = new URL(imageURLString);

				return new WidgetRouteInfo(image, link, "Start in Google Maps");
			}
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			throw new IllegalStateException("Couldn't create widget info", e);
		}
		return null;
	}

	/**
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BestTransportResult) {
			BestTransportResult best = (BestTransportResult) obj;
			return this.mode.equals(best.mode) && this.route.equals(best.route);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mode == null) ? 0 : this.mode.hashCode());
		result = prime * result + ((this.route == null) ? 0 : this.route.hashCode());
		return result;
	}

}
