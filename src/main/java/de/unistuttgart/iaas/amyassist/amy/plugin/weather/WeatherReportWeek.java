package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import com.github.dvdme.ForecastIOLib.FIODaily;

public class WeatherReportWeek {
    public String preamble;
    public WeatherReportDay[] days;
    public String summary;

    public WeatherReportWeek(String preamble, FIODaily d) {
        this.preamble = preamble;
        this.days = new WeatherReportDay[d.days()];
        for (int i = 0; i < d.days(); i++) {
            days[i] = new WeatherReportDay(null, d.getDay(i));
        }
        this.summary = d.getSummary();
    }

    public String toString() {
        return (preamble != null ? preamble + " " : "") + this.summary;
    }
}
