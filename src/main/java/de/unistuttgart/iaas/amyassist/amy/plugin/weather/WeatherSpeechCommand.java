package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;


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
