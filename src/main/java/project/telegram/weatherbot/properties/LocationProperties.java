package project.telegram.weatherbot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "location")
public class LocationProperties {

    private String latitude;
    private String longitude;
    private String name;
    private String timeZone;
}
