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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * test class for system Time logic
 *
 * @author Leon Kiefer, Florian Bauer
 */
@ExtendWith(FrameworkExtension.class)
class SystemTimeLogicTest {

	@Reference
	private TestFramework framework;

	private SystemTimeLogic systemTimeLogic;
	private Environment environment;

	/**
	 * setup of the test
	 */
	@BeforeEach
	public void setup() {
		this.environment = this.framework.mockService(Environment.class);
		this.systemTimeLogic = this.framework.setServiceUnderTest(SystemTimeLogic.class);
	}

	/**
	 * test getTimeStamp()
	 */
	@Test
	void test() {
		LocalDateTime localDateTime = LocalDateTime.parse("2015-05-29T08:00:00");
		Mockito.doReturn(localDateTime).when(this.environment).getCurrentLocalDateTime();

		assertThat(this.systemTimeLogic.getTimeStamp(), is(localDateTime));
	}

}
