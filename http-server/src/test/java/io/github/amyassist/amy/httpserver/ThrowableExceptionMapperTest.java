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

package io.github.amyassist.amy.httpserver;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ThrowableExceptionMapper}.
 * 
 * @author Leon Kieer
 */
class ThrowableExceptionMapperTest {

	private ThrowableExceptionMapper tEM;

	@BeforeEach
	void setup() {
		this.tEM = new ThrowableExceptionMapper();
	}

	@Test
	void testStatus() {
		try (Response response = this.tEM.toResponse(new WebApplicationException(404));) {
			assertThat(response.getStatus(), is(404));
		}
	}

	@Test
	void testStatus500() {
		try (Response response = this.tEM.toResponse(new WebApplicationException(500));) {
			assertThat(response.getStatus(), is(500));
		}
	}

	@Test
	void testMessage() {
		try (Response response = this.tEM.toResponse(new WebApplicationException("Forbidden", 403));) {
			assertThat(response.getStatus(), is(403));
			assertThat(response.getEntity(), is("Forbidden"));
		}
	}

	@Test
	void testCause() {
		try (Response response = this.tEM
				.toResponse(new WebApplicationException(new IllegalArgumentException("Input is not a String")));) {
			assertThat(response.getStatus(), is(500));
			assertThat(response.getEntity(), is(
					"HTTP 500 Internal Server Error caused by java.lang.IllegalArgumentException: Input is not a String"));
		}
	}

	@Test
	void testUnkownThrowable() {
		try (Response response = this.tEM.toResponse(new IllegalArgumentException("Input is not a String"));) {
			assertThat(response.getStatus(), is(500));
			assertThat(response.getEntity(), is("java.lang.IllegalArgumentException: Input is not a String"));
		}
	}

}
