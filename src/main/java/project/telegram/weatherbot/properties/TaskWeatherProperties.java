package project.telegram.weatherbot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "task.weather")
public class TaskWeatherProperties {

    private Integer notificationHour;
    private Integer saveHour;
}
