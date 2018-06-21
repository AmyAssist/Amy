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
		this.id = id;

		this.alarmDate = Calendar.getInstance();
		this.alarmDate.set(Calendar.HOUR_OF_DAY, hour);
		this.alarmDate.set(Calendar.MINUTE, minute);
		this.alarmDate.set(Calendar.SECOND, 0);
		if (this.alarmDate.before(Calendar.getInstance())) {
			this.alarmDate.add(Calendar.DATE, 1);
		}

		this.active = active;
	}

	/**
	 * Returns a string representation of this object
	 * 
	 * @return
	 */
	public String convertToString() {
		return this.alarmDate.get(Calendar.HOUR_OF_DAY) + ":" + this.alarmDate.get(Calendar.MINUTE) + ":" + this.active;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Calendar getAlarmDate() {
		return this.alarmDate;
	}

	public void setAlarmDate(Calendar alarmDate) {
		this.alarmDate = alarmDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
