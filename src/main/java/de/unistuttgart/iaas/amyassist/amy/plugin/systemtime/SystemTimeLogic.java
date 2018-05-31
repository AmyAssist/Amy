/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * The Logic for the system time
 * 
 * @author Florian Bauer, Patrick Gebhardt
 */
@Service
public class SystemTimeLogic {

	/**
	 * 
	 * @return object of type Date
	 */
	public Date getTimeStamp() {
		return Calendar.getInstance().getTime();
	}

	public String getDay() {
		DateFormat dayFormat = new SimpleDateFormat("dd");
		return dayFormat.format(this.getTimeStamp()).toString();
	}

	public String getMonth() {
		DateFormat monthFormat = new SimpleDateFormat("MM");
		return monthFormat.format(this.getTimeStamp()).toString();
	}

	public String getYear() {
		DateFormat yearFormat = new SimpleDateFormat("yy");
		return yearFormat.format(this.getTimeStamp()).toString();
	}

	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd MM yy");
		return dateFormat.format(this.getTimeStamp()).toString();
	}

	public String getHour() {
		DateFormat hourFormat = new SimpleDateFormat("HH");
		return hourFormat.format(this.getTimeStamp()).toString();
	}

	public String getMinute() {
		DateFormat minuteFormat = new SimpleDateFormat("mm");
		return minuteFormat.format(this.getTimeStamp()).toString();
	}

	public String getSecond() {
		DateFormat secondFormat = new SimpleDateFormat("ss");
		return secondFormat.format(this.getTimeStamp()).toString();
	}

	public String getTime() {
		DateFormat timeFormat = new SimpleDateFormat("HH mm ss");
		return timeFormat.format(this.getTimeStamp()).toString();
	}
}
