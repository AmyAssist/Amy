package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.time.Duration;
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
		if (id < 1)
			throw new IllegalArgumentException();
		this.id = id;

		this.timerDate = Calendar.getInstance();
		this.timerDate.add(Calendar.HOUR, hours);
		this.timerDate.add(Calendar.MINUTE, minutes);
		this.timerDate.add(Calendar.SECOND, seconds);

		this.active = active;
	}

	/**
	 * Returns a string representation of this object
	 * @see java.lang.Object#toString()
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return this.id + ":" + this.timerDate.get(Calendar.HOUR_OF_DAY) + ":" + this.timerDate.get(Calendar.MINUTE)
				+ ":" + this.timerDate.get(Calendar.SECOND) + ":" + this.active;
	}

	/**
	 * Construct an alarm object from the String that was made by the
	 * convertToString method
	 * 
	 * @param input
	 *            the String made by convertToString method
	 * @return the corresponding alarm object
	 */
	public static Timer reconstructObject(String input) {
		String[] params = input.split(":");
		if (params.length == 5)
			return new Timer(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]),
					Integer.parseInt(params[3]), Boolean.valueOf(params[4]));
		throw new IllegalArgumentException();
	}

	/**
	 * Returns this timers delay until it goes off
	 * 
	 * @return hourDiff, minuteDiff, secondDiff
	 */
	public int[] getRemainingTime() {
		Calendar current = Calendar.getInstance();
		Calendar future = this.timerDate;

		Duration duration = Duration.between(current.toInstant(), future.toInstant());
		int diff = (int) duration.getSeconds();

		int hourDiff = diff / 3600;
		diff %= 3600;

		int minuteDiff = diff / 60;
		diff %= 60;

		int secondDiff = diff;

		return new int[] { hourDiff, minuteDiff, secondDiff };
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
