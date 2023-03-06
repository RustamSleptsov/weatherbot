package project.telegram.weatherbot.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import project.telegram.weatherbot.dto.WeatherDto;

@Tag(name = "Weather API")
@RequestMapping("/api")
public interface WeatherApi {

    @GetMapping("/weather")
    @ResponseStatus(HttpStatus.OK)
    WeatherDto getWeather(@RequestParam String date);
}
