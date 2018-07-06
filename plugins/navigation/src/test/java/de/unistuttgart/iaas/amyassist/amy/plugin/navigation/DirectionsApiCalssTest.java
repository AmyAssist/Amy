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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doReturn;

import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.google.maps.model.TravelMode;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for the api calls
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class DirectionsApiCalssTest {
	private Properties mockProperties;
	private DirectionsApiCalls calls;
	private Logger logger;

	@Reference
	private TestFramework testFramework;
	
	@BeforeEach
	public void init() {
		
		this.mockProperties = this.testFramework.mockService(Properties.class);
		this.calls = this.testFramework.setServiceUnderTest(DirectionsApiCalls.class);
		doReturn("GOOGLEKEY").when(this.mockProperties).getProperty("GOOGLE_API_KEY");
		this.calls.init();
	}
	
	@Test
	public void testFromToWothArrvialTime() {
		assertThat(this.calls.fromToWithArrivalTime("start", "ende", TravelMode.DRIVING, new DateTime()).length, equalTo(0));
	}
	
	@Test
	public void testFromTo() {
		assertThat(this.calls.fromTo("start", "ende", TravelMode.DRIVING).length, equalTo(0));
	}
	
	@Test
	public void testFromToWithDepartureTime() {
		assertThat(this.calls.fromToWithDepartureTime("start", "ende", TravelMode.DRIVING, new DateTime()).length, equalTo(0));
	}
}
