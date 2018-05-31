package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import javax.xml.bind.annotation.XmlRootElement;

import com.github.dvdme.ForecastIOLib.FIODaily;

@XmlRootElement
public class WeatherReportWeek {
    public String preamble;
    public WeatherReportDay[] days;
    public String summary;

    public WeatherReportWeek(String preamble, FIODaily d) {
        this.preamble = preamble;
        this.days = new WeatherReportDay[d.days()];
        for (int i = 0; i < d.days(); i++) {
            this.days[i] = new WeatherReportDay(null, d.getDay(i));
        }
        this.summary = d.getSummary();
    }

    @Override
	public String toString() {
        return (this.preamble != null ? this.preamble + " " : "") + this.summary;
    }
}
