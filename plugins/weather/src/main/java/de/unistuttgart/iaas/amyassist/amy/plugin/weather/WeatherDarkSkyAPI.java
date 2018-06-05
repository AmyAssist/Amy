/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public WeatherReportDay getReportToday() {
        FIODaily d = getDailyReports();
        return new WeatherReportDay("This is the weather report for today.", d.getDay(0));
    }

    public WeatherReportDay getReportTomorrow() {
        FIODaily d = getDailyReports();
        return new WeatherReportDay("This is the weather report for tomorrow.", d.getDay(1));
    }

    public WeatherReportWeek getReportWeek() {
        return new WeatherReportWeek("This is the weather report for the week. ", getDailyReports());
    }
}
