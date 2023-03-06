package project.telegram.weatherbot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.telegram.weatherbot.properties.AppProperties;

@Configuration
public class SwaggerConfig {

    private final AppProperties appProperties;

    public SwaggerConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title(appProperties.getName()).version(appProperties.getVersion()));
    }

    @Bean
    public GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("http")
                .pathsToMatch("/**")
                .build();
    }
}
