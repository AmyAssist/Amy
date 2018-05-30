/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * REST Resource for weather
 * 
 * @author Muhammed Kaya
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
	
	@GET
	@Path("today")
	public WeatherReportDay getWeatherToday() {
		return weatherLogic.getReportToday();
		
	}
	
	@GET
	@Path("tomorrow")
	public WeatherReportDay getWeatherTomorrow() {
		return weatherLogic.getReportTomorrow();
	}
	
//	@GET
//	@Path("week")
//	public WeatherReportDay getWeatherWeek() {
//		return weatherLogic.getReportWeek();
//	}


}
