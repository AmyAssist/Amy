package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.util.Calendar;

/**
 * Class that defines alarm attributes and behaviour
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 *
 */
public class Timer {

	private int id;
	private Calendar timerDate;
	private boolean active;

	public Timer(int id, int hours, int minutes, int seconds, boolean active) {
		this.id = id;

		this.timerDate = Calendar.getInstance();
		this.timerDate.add(Calendar.HOUR, hours);
		this.timerDate.add(Calendar.MINUTE, minutes);
		this.timerDate.add(Calendar.SECOND, seconds);

		this.active = active;
	}

	public String convertToString() {
		return this.timerDate.get(Calendar.HOUR_OF_DAY) + ":" + this.timerDate.get(Calendar.MINUTE) + ":"
				+ this.timerDate.get(Calendar.SECOND) + ":" + this.active;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Calendar getTimerDate() {
		return timerDate;
	}

	public void setTimerDate(Calendar timerDate) {
		this.timerDate = timerDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
