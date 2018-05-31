/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * TODO: Description
 * 
 * @author Leon Kiefer
 */
class SystemTimeLogicTest {
	private SystemTimeLogic systemTimeLogic;
	private Calendar calendar;

	@BeforeEach
	public void setup() {
		this.systemTimeLogic = Mockito.mock(SystemTimeLogic.class,
				Mockito.CALLS_REAL_METHODS);

		this.calendar = Calendar.getInstance();
	}

	@Test
	void test() {
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getTimeStamp(), equalTo(time));
	}

	@Test
	void test8() {
		this.calendar.set(Calendar.HOUR_OF_DAY, 8);
		Date time = this.calendar.getTime();
		Mockito.doReturn(time).when(this.systemTimeLogic).getTimeStamp();

		assertThat(this.systemTimeLogic.getHour(), equalTo("08"));
	}

}
