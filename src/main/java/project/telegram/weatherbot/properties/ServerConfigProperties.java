package project.telegram.weatherbot.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "server.config")
public class ServerConfigProperties {

    private String id;
}
