package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.util.Calendar;

/**
 * Class that defines timer attributes and behaviour
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 *
 */
public class Alarm {

	private int id;
	private Calendar alarmDate;
	private boolean active;

	/**
	 * Constructor for the alarm. Every alarm is initially set active
	 * 
	 * @param id
	 *            the alarmcounter
	 * @param hour
	 *            hour of the alarm
	 * @param minute
	 *            minute of the alarm
	 */
	public Alarm(int id, int hour, int minute, boolean active) {
		if (id < 1)
			throw new IllegalArgumentException();

		this.id = id;

		setTime(new int[] { hour, minute });

		this.active = active;
	}

	/**
	 * Returns a string representation of this object
	 * 
	 * @return
	 */
	public String convertToString() {
		return this.id + ":" + this.alarmDate.get(Calendar.HOUR_OF_DAY) + ":" + this.alarmDate.get(Calendar.MINUTE)
				+ ":" + this.active;
	}

	/**
	 * Construct an alarm object from the String that was made by the
	 * convertToString method
	 * 
	 * @param input
	 *            the String made by convertToString method
	 * @return the corresponding alarm object
	 */
	public static Alarm reconstructObject(String input) {
		String[] params = input.split(":");
		if (params.length == 4)
			return new Alarm(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]),
					Boolean.valueOf(params[3]));
		throw new IllegalArgumentException();
	}

	public void setTime(int[] newTime) {
		if (newTime.length == 2) {
			Calendar date = Calendar.getInstance();
			date.set(Calendar.HOUR_OF_DAY, newTime[0]);
			date.set(Calendar.MINUTE, newTime[1]);
			date.set(Calendar.SECOND, 0);
			if (date.before(Calendar.getInstance()))
				date.add(Calendar.DATE, 1);

			this.alarmDate = date;
		} else {
			throw new IllegalArgumentException();
		}

	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Calendar getAlarmDate() {
		return this.alarmDate;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
