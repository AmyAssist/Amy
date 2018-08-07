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

package de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for Route
 * 
 * @author Muhammed Kaya
 */
class RouteTest {

	private Route route;

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#Route()}.
	 */
	@Test
	@BeforeEach
	void testRoute() {
		this.route = new Route();
		this.route.setOrigin("Start");
		this.route.setDestination("Destination");
		this.route.setTravelmode("Car");
		this.route.setTime(ZonedDateTime.parse("1960-01-02T20:20:20+00:00"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#getOrigin()}.
	 */
	@Test
	void testGetOrigin() {
		assertThat(this.route.getOrigin(), is("Start"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#setOrigin(java.lang.String)}.
	 */
	@Test
	void testSetOrigin() {
		this.route.setOrigin("Begin");
		assertThat(this.route.getOrigin(), is("Begin"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#getDestination()}.
	 */
	@Test
	void testGetDestination() {
		assertThat(this.route.getDestination(), is("Destination"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#setDestination(java.lang.String)}.
	 */
	@Test
	void testSetDestination() {
		this.route.setDestination("Finish");
		assertThat(this.route.getDestination(), is("Finish"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#getTravelmode()}.
	 */
	@Test
	void testGetTravelmode() {
		assertThat(this.route.getTravelmode(), is("Car"));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#setTravelmode(java.lang.String)}.
	 */
	@Test
	void testSetTravelmode() {
		this.route.setTravelmode("walking");
		;
		assertThat(this.route.getTravelmode(), is("walking"));
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#getTime()}.
	 */
	@Test
	void testGetTime() {
		assertThat(this.route.getTime(), is(ZonedDateTime.parse("1960-01-02T20:20:20+00:00")));
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest.Route#setTime(java.time.ZonedDateTime)}.
	 */
	@Test
	void testSetTime() {
		this.route.setTime(ZonedDateTime.parse("1960-01-02T21:21:21+00:00"));
		assertThat(this.route.getTime(), is(ZonedDateTime.parse("1960-01-02T21:21:21+00:00")));
	}

}
