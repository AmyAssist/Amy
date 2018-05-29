package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import com.github.dvdme.ForecastIOLib.FIODaily;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

@Service
public class WeatherDarkSkyAPI {
    private static final String STUTTGART_COORDINATES_LAT = "48.745295";
    private static final String STUTTGART_COORDINATES_LONG = "9.10502";
    private static final String API_SECRET = System.getenv("DARKSKY_API_SECRET");

    private FIODaily getDailyReports() {
        ForecastIO fio = new ForecastIO(API_SECRET);
        fio.setUnits(ForecastIO.UNITS_SI);
        fio.getForecast(STUTTGART_COORDINATES_LAT, STUTTGART_COORDINATES_LONG);

        FIODaily dailyReports = new FIODaily(fio);
        for (int i = 0; i < dailyReports.days(); i++) {
            FIODataPoint report = dailyReports.getDay(i);
            report.setTimezone(fio.getTimezone());
        }

        return dailyReports;
    }

    private String trimQuotes(String s) {
        return s.replaceAll("^\"|\"$", "");
    }

    private long round(double d) {
        return Math.round(d);
    }

    private String formatDaySummary(FIODataPoint p) {
        return trimQuotes(p.summary()) + " " + round(p.precipProbability() * 100) + "% probability of " + trimQuotes(p.precipType()) + ". Between " + round(p.temperatureMin()) + " and " + round(p.temperatureMax()) + "°C" +". Sunrise is at " + p.sunriseTime() + " and sunset at " + p.sunsetTime();
    }

    public String getReportToday() {
        FIODaily d = getDailyReports();
        return "This is the weather report for today. " + formatDaySummary(d.getDay(0));
    }

    public String getReportTomorrow() {
        FIODaily d = getDailyReports();
        return "This is the weather report for tomorrow. " + formatDaySummary(d.getDay(1));
    }

    public String getReportWeek() {
        FIODaily d = getDailyReports();
        return "This is the weather report for the week. " + d.getSummary();
    }
}
