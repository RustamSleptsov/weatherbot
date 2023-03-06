package project.telegram.weatherbot.service;

import org.springframework.stereotype.Service;
import project.telegram.weatherbot.dto.WeatherDto;
import project.telegram.weatherbot.mapper.WeatherMapper;
import project.telegram.weatherbot.model.Weather;
import project.telegram.weatherbot.repository.WeatherRepository;

import java.time.LocalDate;

@Service
public class WeatherService {

    private final WeatherMapper weatherMapper;
    private final WeatherRepository weatherRepository;

    public WeatherService(WeatherMapper weatherMapper, WeatherRepository weatherRepository) {
        this.weatherMapper = weatherMapper;
        this.weatherRepository = weatherRepository;
    }

    public WeatherDto getWeather(String date) {
        try {
            LocalDate.parse(date, TelegramBotService.YANDEX_WEATHER_API_DATE_FORMAT);
            Weather weather = weatherRepository.findByDate(date);
            if (weather != null) {
                return weatherMapper.toDto(weather);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public void save(Weather weather) {
        weatherRepository.save(weather);
    }
}
