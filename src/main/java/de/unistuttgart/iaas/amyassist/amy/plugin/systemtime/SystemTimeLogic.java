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

	/**
	 * 
	 * @return current day of month as String (dd), e.g. 01
	 */
	public String getDay() {
		DateFormat dayFormat = new SimpleDateFormat("dd");
		return dayFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current month of year as String (MM), e.g. 06
	 */
	public String getMonth() {
		DateFormat monthFormat = new SimpleDateFormat("MM");
		return monthFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current year as String (yyyy), e.g. 2018
	 */
	public String getYear() {
		DateFormat yearFormat = new SimpleDateFormat("yyyy");
		return yearFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current date as String (dd MM yy), e.g. 01 06 18
	 */
	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd MM yy");
		return dateFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current hour as String (HH), e.g. 12
	 */
	public String getHour() {
		DateFormat hourFormat = new SimpleDateFormat("HH");
		return hourFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current minute as String (mm), e.g. 45
	 */
	public String getMinute() {
		DateFormat minuteFormat = new SimpleDateFormat("mm");
		return minuteFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current second as String (ss), e.g. 20
	 */
	public String getSecond() {
		DateFormat secondFormat = new SimpleDateFormat("ss");
		return secondFormat.format(this.getTimeStamp()).toString();
	}

	/**
	 * 
	 * @return current time as String (HH mm ss), e.g. 12 45 20
	 */
	public String getTime() {
		DateFormat timeFormat = new SimpleDateFormat("HH mm ss");
		return timeFormat.format(this.getTimeStamp()).toString();
	}
}
