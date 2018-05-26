/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * Test class for the AlarmClock plug-in
 * 
 * @author Patrick Singer
 */
@ExtendWith(MockitoExtension.class)
public class AlarmClockTest {

	@Mock
	private ICore core;

	@Mock
	private IStorage storage;

	private AlarmClockLogic acl;

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

	}

	@Test
	public void testGetAll() {

	}

}
