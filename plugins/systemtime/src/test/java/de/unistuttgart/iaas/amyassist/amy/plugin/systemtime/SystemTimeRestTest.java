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

package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import static de.unistuttgart.iaas.amyassist.amy.test.matcher.rest.ResponseMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * test for the rest resource of the system time
 * 
 * @author Muhammed Kaya
 */
@ExtendWith(FrameworkExtension.class)
class SystemTimeRestTest {

	@Reference
	private TestFramework testFramework;

	private SystemTimeLogic logic;

	private WebTarget target;

	private LocalDateTime ldt;

	/**
	 * setUp
	 */
	@BeforeEach
	public void setUp() {
		this.target = this.testFramework.setRESTResource(SystemTimeResource.class);
		this.logic = this.testFramework.mockService(SystemTimeLogic.class);

		this.ldt = LocalDateTime.parse("2015-05-29T08:00:00");
	}

	/**
	 * Test method for {@link de.unistuttgart.iaas.amyassist.amy.plugin.systemtime.SystemTimeResource#getTimeStamp()}.
	 */
	@Test
	void testGetTimeStamp() {
		Mockito.when(this.logic.getTimeStamp()).thenReturn(this.ldt);
		try (Response response = this.target.path("timeStamp").request().get()) {
			assertThat(response.readEntity(String.class), equalTo(this.ldt.toString()));
			assertThat(response, status(200));
			Mockito.verify(this.logic).getTimeStamp();
		}
	}

}
