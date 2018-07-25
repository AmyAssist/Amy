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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.registry.LocationRegistry;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

/**
 * This is the test class for the Navigation Speech. At the moment only for help methods
 * 
 * @author Lars Buttgereit
 */
@ExtendWith({ MockitoExtension.class, FrameworkExtension.class })
public class TestTimeFormatter {

	@Reference
	private TestFramework testFramework;

	private TimeFormatter timeFormatter;
	private Environment environment;

	@BeforeEach
	void init() {
		this.environment = this.testFramework.mockService(Environment.class);
		this.timeFormatter = this.testFramework.setServiceUnderTest(TimeFormatter.class);
		when(environment.getCurrentLocalDateTime()).thenReturn(LocalDateTime.MIN);
	}

	@Test
	void testFormatTimeNull() {
		assertThat(this.timeFormatter.formatTimes(null), equalTo(null));
	}

	@Test
	void testFromatTimeOClock() {
		String[] input1 = { "9", "o", "clock" };
		String[] input2 = { "23", "o", "clock" };
		String[] input3 = { "25", "o", "clock" };
		String[] input4 = { "-1", "o", "clock" };
		assertThat(this.timeFormatter.formatTimes(input1), equalTo(LocalDateTime.MIN.plusHours(9)));
		assertThat(this.timeFormatter.formatTimes(input2), equalTo(LocalDateTime.MIN.plusHours(23)));
		assertThat(this.timeFormatter.formatTimes(input3), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input4), equalTo(null));
	}

	@Test
	void testFromatTimeQuarterPast() {
		String[] input1 = { "quarter", "past", "2" };
		String[] input2 = { "quarter", "past", "22" };
		String[] input3 = { "quarter", "past", "25" };
		String[] input4 = { "quarter", "past", "-1" };
		assertThat(this.timeFormatter.formatTimes(input1), equalTo(LocalDateTime.MIN.plusHours(2).plusMinutes(15)));
		assertThat(this.timeFormatter.formatTimes(input2), equalTo(LocalDateTime.MIN.plusHours(22).plusMinutes(15)));
		assertThat(this.timeFormatter.formatTimes(input3), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input4), equalTo(null));
	}

	@Test
	void testFromatTimeQuarterTo() {
		String[] input1 = { "quarter", "to", "2" };
		String[] input2 = { "quarter", "to", "22" };
		String[] input3 = { "quarter", "to", "25" };
		String[] input4 = { "quarter", "to", "-1" };
		assertThat(this.timeFormatter.formatTimes(input1), equalTo(LocalDateTime.MIN.plusHours(1).plusMinutes(45)));
		assertThat(this.timeFormatter.formatTimes(input2), equalTo(LocalDateTime.MIN.plusHours(21).plusMinutes(45)));
		assertThat(this.timeFormatter.formatTimes(input3), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input4), equalTo(null));
	}

	@Test
	void testFromatTimeTo() {
		String[] input1 = { "10", "to", "2" };
		String[] input2 = { "21", "to", "22" };
		String[] input3 = { "-1", "to", "25" };
		String[] input4 = { "61", "to", "20" };
		String[] input5 = { "10", "to", "-1" };
		String[] input6 = { "21", "to", "25" };
		assertThat(this.timeFormatter.formatTimes(input1), equalTo(LocalDateTime.MIN.plusHours(1).plusMinutes(50)));
		assertThat(this.timeFormatter.formatTimes(input2), equalTo(LocalDateTime.MIN.plusHours(21).plusMinutes(39)));
		assertThat(this.timeFormatter.formatTimes(input3), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input4), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input5), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input6), equalTo(null));
	}

	@Test
	void testFromatTimePast() {
		String[] input1 = { "10", "past", "2" };
		String[] input2 = { "21", "past", "22" };
		String[] input3 = { "-1", "past", "25" };
		String[] input4 = { "61", "past", "20" };
		String[] input5 = { "10", "past", "-1" };
		String[] input6 = { "21", "past", "25" };
		assertThat(this.timeFormatter.formatTimes(input1), equalTo(LocalDateTime.MIN.plusHours(2).plusMinutes(10)));
		assertThat(this.timeFormatter.formatTimes(input2), equalTo(LocalDateTime.MIN.plusHours(22).plusMinutes(21)));
		assertThat(this.timeFormatter.formatTimes(input3), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input4), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input5), equalTo(null));
		assertThat(this.timeFormatter.formatTimes(input6), equalTo(null));
	}
}
