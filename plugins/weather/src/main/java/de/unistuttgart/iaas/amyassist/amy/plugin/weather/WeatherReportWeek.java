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
