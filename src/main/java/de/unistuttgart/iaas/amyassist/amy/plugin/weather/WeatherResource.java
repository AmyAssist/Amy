/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * REST Resource for weather
 * 
 * @author Muhammed Kaya, Christian Bräuner
 */
@Path("weather")
public class WeatherResource {
	
	@Reference
	private WeatherDarkSkyAPI weatherLogic;
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public WeatherReportDay getWeather() {
//		return weatherEntity;
//
//	}
	
	/**
	 * get the weather forecast for today
	 * 
	 * @return todays weather forecast
	 */
	@GET
	@Path("today")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportDay getWeatherToday() {
		return this.weatherLogic.getReportToday();
		
	}
	/**
	 * get the weather forecast for tomorrow
	 * 
	 * @return tomorrows weather forecast
	 */
	@GET
	@Path("tomorrow")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportDay getWeatherTomorrow() {
		return this.weatherLogic.getReportTomorrow();
	}
	
	/**
	 * get the weather forecast for the week
	 * 
	 * @return this weeks weather forecast
	 */
	@GET
	@Path("week")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportWeek getWeatherWeek() {
		return this.weatherLogic.getReportWeek();
	}


}
