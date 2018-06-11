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

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

import java.util.Calendar;
import java.util.Date;


@SpeechCommand({"weather"})
public class WeatherSpeechCommand {

    @Reference
    WeatherDarkSkyAPI weatherAPI;

    @Grammar("today")
    public String weatherToday(String... words) {
        return weatherAPI.getReportToday().toString();
    }

    @Grammar("tomorrow")
    public String weatherTomorrow(String... words) {
        return weatherAPI.getReportTomorrow().toString();
    }

    @Grammar("week")
    public String weatherWeek(String... words) {
        return weatherAPI.getReportWeek().toString();
    }

    @Grammar("weekend")
    public String weatherWeekend(String... words) {

        WeatherReportWeek report = weatherAPI.getReportWeek();
        Calendar c = Calendar.getInstance();

        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if (weekday == Calendar.SATURDAY) {
            if (report.days.length < 2) {
                throw new RuntimeException("WeatherAPI not working as expected");
            }
            return "Today, " + report.days[0].shortDescription() + " and tomorrow, " + report.days[1].shortDescription();
        }
        else if (weekday == Calendar.SUNDAY) {
            return "Today, " + report.days[0].shortDescription();
        }
        else {
            // Get weekend days
            String saturdayReport = null;
            String sundayReport = null;
            for (WeatherReportDay d: report.days) {
                c.setTime(new Date(d.timestamp * 1000));
                weekday = c.get(Calendar.DAY_OF_WEEK);
                if (weekday == Calendar.SATURDAY) {
                    saturdayReport = d.shortDescription();
                }
                else if (weekday == Calendar.SUNDAY) {
                    sundayReport = d.shortDescription();
                }
            }
            return "On Saturday, " + saturdayReport + " and on Sunday " + sundayReport;
        }
    }
}
