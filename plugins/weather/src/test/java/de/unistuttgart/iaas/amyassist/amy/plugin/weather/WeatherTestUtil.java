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

package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import java.util.Random;
import java.util.TimeZone;

/**
 * A utility class for weather tests
 * 
 * @author Tim Neumann
 */
public class WeatherTestUtil {

	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz ";

	private static final String[] PERCIP_TYPES = new String[] { "rain", "snow", "sleet" };

	private static final String[] ICON_TYPE = new String[] { "clear-day", "clear-night", "rain", "snow", "sleet",
			"wind", "fog", "cloudy", "partly-cloudy-day", "partly-cloudy-night" };

	private WeatherTestUtil() {
		// hide constrcutor
	}

	private static String rPerType(Random rng) {
		return PERCIP_TYPES[rng.nextInt(PERCIP_TYPES.length)];
	}

	private static String rIconType(Random rng) {
		return ICON_TYPE[rng.nextInt(ICON_TYPE.length)];
	}

	private static double randomDouble(Random rng, int max) {
		return rng.nextDouble() * max;
	}

	private static double randomDoubleWithNegative(Random rng, int max) {
		double d = randomDouble(rng, max);
		return rng.nextBoolean() ? d : -d;
	}

	private static String randomTimezone(Random rng) {
		String[] timeZones = TimeZone.getAvailableIDs();
		return timeZones[rng.nextInt(timeZones.length)];
	}

	/**
	 * Generate a random weather report
	 * 
	 * @param rng
	 *            The Random to use
	 * @return The generated report
	 */
	public static WeatherReport generateWeatherReport(Random rng) {
		WeatherReportInstant now = new WeatherReportInstant(randomString(rng, rng.nextInt(40)), rng.nextDouble(),
				rPerType(rng), randomDoubleWithNegative(rng, 40), rng.nextLong(), randomDouble(rng, 50),
				rIconType(rng));
		WeatherReportDay[] days = new WeatherReportDay[8];
		for (int i = 0; i < days.length; i++) {
			days[i] = generateWeatherReportDay(rng);
		}
		return new WeatherReport(now, new WeatherReportWeek(randomString(rng, rng.nextInt(40)), days),
				randomTimezone(rng));
	}

	/**
	 * Generate a random weather report for a day
	 * 
	 * @param rng
	 *            The Random to use
	 * @return The generated report
	 */
	public static WeatherReportDay generateWeatherReportDay(Random rng) {
		return new WeatherReportDay(randomString(rng, rng.nextInt(40)), rng.nextDouble(), rPerType(rng),
				randomDoubleWithNegative(rng, 40), randomDoubleWithNegative(rng, 40), rng.nextLong(), rng.nextLong(),
				rng.nextLong(), randomDouble(rng, 50), rIconType(rng));
	}

	/**
	 * Generate a random string from the alphabet {@value #ALPHABET} with lower and uppercase.
	 * 
	 * @param rng
	 *            The Random to use
	 * @param length
	 *            The length of the string.
	 * @return The generated string
	 */
	public static String randomString(Random rng, int length) {
		String alphabetWithLarge = ALPHABET + ALPHABET.toUpperCase();
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = alphabetWithLarge.charAt(rng.nextInt(alphabetWithLarge.length()));
		}
		return new String(chars);
	}
}
