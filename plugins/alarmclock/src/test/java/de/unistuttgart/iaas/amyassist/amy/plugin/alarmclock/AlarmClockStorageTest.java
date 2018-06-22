package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

/**
 * Test class for the alarm clock storage class
 * 
 * @author padyf
 *
 */
@ExtendWith(FrameworkExtention.class)
public class AlarmClockStorageTest {

	@Reference
	private TestFramework framework;

	private AlarmClockStorage acs;

	private IStorage storage;

	private static final String ALARMCOUNTER = "alarmCounter";

	private static final String TIMERCOUNTER = "timerCounter";

	/**
	 * Initializes the class variables before every test
	 */
	@BeforeEach
	void setup() {
		this.storage = this.framework.storage();
		this.acs = this.framework.setServiceUnderTest(AlarmClockStorage.class);
		Mockito.reset(this.storage);
	}

	@Test
	void testStoreAlarm() {
		Alarm alarm = new Alarm(2, 15, 30, true);

		this.acs.storeAlarm(alarm);
		Mockito.verify(this.storage).put("alarm2", "2:15:30:true");
	}

	@Test
	void testStoreTimer() {
		Timer timer = new Timer(3, 1, 0, 1, false);

		this.acs.storeTimer(timer);
		Mockito.verify(this.storage).put("timer3", timer.convertToString());
	}

	@Test
	void testGetAlarmCounter() {
		this.storage.put(ALARMCOUNTER, "1");
		Mockito.reset(this.storage);

		assertThat(this.acs.getAlarmCounter(), is(1));
		Mockito.verify(this.storage, Mockito.only()).get(ALARMCOUNTER);
	}

	@Test
	void testGetTimerCounter() {
		this.storage.put(TIMERCOUNTER, "20");
		Mockito.reset(this.storage);

		assertThat(this.acs.getTimerCounter(), is(20));
		Mockito.verify(this.storage, Mockito.only()).get(TIMERCOUNTER);
	}

	@Test
	void testPutAlarmCounter() {
		this.storage.put(ALARMCOUNTER, "10");
		Mockito.reset(this.storage);

		this.acs.putAlarmCounter(20);
		Mockito.verify(this.storage, Mockito.only()).put(ALARMCOUNTER, "20");
		assertThat(this.storage.get(ALARMCOUNTER), is("20"));
	}

	@Test
	void testPutTimerCounter() {
		this.storage.put(TIMERCOUNTER, "10");
		Mockito.reset(this.storage);

		this.acs.putTimerCounter(20);
		Mockito.verify(this.storage, Mockito.only()).put(TIMERCOUNTER, "20");
		assertThat(this.storage.get(TIMERCOUNTER), is("20"));
	}

	@Test
	void testIncrementAlarmCounter() {
		this.storage.put(ALARMCOUNTER, "9");
		Mockito.reset(this.storage);

		assertThat(10, is(this.acs.incrementAlarmCounter()));
		Mockito.verify(this.storage).get(ALARMCOUNTER);
		Mockito.verify(this.storage).put(ALARMCOUNTER, "10");
		assertThat(this.storage.get(ALARMCOUNTER), is("10"));
	}

	@Test
	void testIncrementTimerCounter() {
		this.storage.put(TIMERCOUNTER, "9");
		Mockito.reset(this.storage);

		assertThat(10, is(this.acs.incrementTimerCounter()));
		Mockito.verify(this.storage).get(TIMERCOUNTER);
		Mockito.verify(this.storage).put(TIMERCOUNTER, "10");
		assertThat(this.storage.get(TIMERCOUNTER), is("10"));
	}

	@Test
	void testHasKey() {
		this.storage.put("foo", "foo");

		assertThat(this.acs.hasKey("foo"), is(true));
		assertThat(this.acs.hasKey("oof"), is(false));
		Mockito.verify(this.storage, Mockito.times(2)).has(ArgumentMatchers.anyString());
	}

	@Test
	void testDeleteKey() {
		this.storage.put("foo", "foo");

		this.acs.deleteKey("foo");
		Mockito.verify(this.storage).delete("foo");
	}

	@Test
	void testGetAlarm() {
		// wrong id
		assertThrows(NoSuchElementException.class, () -> this.acs.getAlarm(1));

		// usual case
		this.storage.put("alarm1", "1:15:20:true");
		assertThat(this.acs.getAlarm(1).convertToString(), is("1:15:20:true"));
	}

	@Test
	void testGetTimer() {
		// wrong id
		assertThrows(NoSuchElementException.class, () -> this.acs.getTimer(1));

		// usual case
		this.storage.put("timer1", "1:20:20:20:true");
	}
}
