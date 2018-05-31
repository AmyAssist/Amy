package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import static java.lang.Math.round;

import javax.xml.bind.annotation.XmlRootElement;

import com.github.dvdme.ForecastIOLib.FIODataPoint;

@XmlRootElement
public class WeatherReportDay {
    public String preamble;
    public String summary;
    public String precipProbability;
    public String precipType;
    public long temperatureMin;
    public long temperatureMax;
    public String sunriseTime;
    public String sunsetTime;

    private String trimQuotes(String s) {
        return s.replaceAll("^\"|\"$", "");
    }

    public WeatherReportDay(String preamble, FIODataPoint p) {
        this.preamble = preamble;
        this.summary = trimQuotes(p.summary());
        this.precipProbability = round(p.precipProbability() * 100) + "%";
        this.precipType = trimQuotes(p.precipType());
        this.temperatureMin = Math.round(p.temperatureMin());
        this.temperatureMax = Math.round(p.temperatureMax());
        this.sunriseTime = p.sunriseTime();
        this.sunsetTime = p.sunsetTime();
    }

    public String toString() {
        return (this.preamble != null ? this.preamble + " " : "") + this.summary + " " + this.precipProbability +  " probability of " + this.precipType + ". Between " + this.temperatureMin + " and " + this.temperatureMax + "°C" +". Sunrise is at " + this.sunriseTime + " and sunset at " + this.sunsetTime;
    }
}
