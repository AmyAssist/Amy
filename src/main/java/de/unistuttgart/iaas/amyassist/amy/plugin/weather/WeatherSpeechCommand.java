package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import de.unistuttgart.iaas.amyassist.amy.core.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.SpeechCommand;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;


@SpeechCommand({"weather"})
public class WeatherSpeechCommand {

    @Reference
    WeatherDarkSkyAPI weatherAPI;

    @Grammar("today")
    public String weatherToday(String... words) {
        return weatherAPI.getReportToday();
    }

    @Grammar("tomorrow")
    public String weatherTomorrow(String... words) {
        return weatherAPI.getReportTomorrow();
    }

    @Grammar("week")
    public String weatherWeek(String... words) {
        return weatherAPI.getReportWeek();
    }
}
