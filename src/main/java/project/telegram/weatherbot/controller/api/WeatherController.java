package project.telegram.weatherbot.controller.api;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.telegram.weatherbot.dto.WeatherDto;
import project.telegram.weatherbot.service.WeatherService;

@RestController
public class WeatherController implements WeatherApi {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public WeatherDto getWeather(@RequestParam String date) {
        return weatherService.getWeather(date);
    }
}
