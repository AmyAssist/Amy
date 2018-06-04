/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.TestFramework;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api.TaskSchedulerAPI;

/**
 * Test class for the AlarmClock plug-in
 * 
 * @author Patrick Singer
 */
@ExtendWith(FrameworkExtention.class)
public class AlarmClockTest {
	@Reference
	private TestFramework framework;

	private AlarmClockLogic acl;

	private TaskSchedulerAPI mockService;

	@BeforeEach
	public void setup() {
		this.mockService = this.framework.mockService(TaskSchedulerAPI.class);
		this.acl = this.framework.setServiceUnderTest(AlarmClockLogic.class);
		this.acl.init(null);
	}

	/**
	 * Tests if the keywords are correct
	 */
	@Test
	public void testKeywords() {

	}

	/**
	 * Tests the setAlarms method
	 */
	@Test
	public void testSet() {
		this.acl.setAlarm("15:20");

		Mockito.verify(this.mockService).schedule(
				ArgumentMatchers.any(Runnable.class),
				ArgumentMatchers.any(Date.class));
	}

	@Test
	public void testGetAll() {

	}

}
