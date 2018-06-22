package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.rest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock.Alarm;

class TimestampTest {

	@Test
	void testConstructors() {
		Timestamp ts = new Timestamp();
		assertEquals(0, ts.getHour());
		assertEquals(0, ts.getMinute());
		assertEquals("", ts.getLink());
		
		ts = new Timestamp(15, 20);
		assertEquals(15, ts.getHour());
		assertEquals(20, ts.getMinute());
		assertEquals("", ts.getLink());
		
		ts = new Timestamp("16:55");
		assertEquals(16, ts.getHour());
		assertEquals(55, ts.getMinute());
		assertEquals("", ts.getLink());
		
		Alarm alarm = new Alarm(2, 5, 17, true);
		ts = new Timestamp(alarm);
		ts.setLink("/" + alarm.getId());
		assertEquals(5, ts.getHour());
		assertEquals(17, ts.getMinute());
		assertEquals("/2", ts.getLink());
		
		try {
			alarm = null;
			ts = new Timestamp(alarm);
			fail("");
		} catch (IllegalArgumentException e) {
			
		}
		
		try {
			ts = new Timestamp("abc");
			fail("");
		} catch (IllegalArgumentException e) {
			assertEquals("abc", e.getMessage());
		}		
	}

	@Test
	void testToString() {
		Timestamp ts = new Timestamp(0, 15);
		assertEquals("00:15", ts.toString());
		
		ts = new Timestamp(15, 20);
		assertEquals("15:20", ts.toString());
		
		ts = new Timestamp(15, 2);
		assertEquals("15:02", ts.toString());
		ts = new Timestamp(6, 5);
		assertEquals("06:05", ts.toString());
	}

	@Test
	void testIsValid() {
		Timestamp ts = new Timestamp(15, 20);
		assertTrue(ts.isValid());
		
		ts = new Timestamp(15, 90);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(60, 30);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(60, 90);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(-1, 30);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(10, -90);
		assertFalse(ts.isValid());
		
		ts = new Timestamp(-60, -90);
		assertFalse(ts.isValid());
	}
	
	@Test
	void testEquals() {
		Timestamp ts = new Timestamp();
		ts.setHour(15);
		ts.setMinute(15);
		assertTrue(ts.equals(new Timestamp(15, 15)));
		assertFalse(ts.equals("15:15"));
		assertFalse(ts.equals(new Timestamp()));
	}

}
