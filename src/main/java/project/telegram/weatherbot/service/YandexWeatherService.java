package project.telegram.weatherbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import project.telegram.weatherbot.api.Sender;
import project.telegram.weatherbot.model.Weather;
import project.telegram.weatherbot.properties.TaskWeatherProperties;
import project.telegram.weatherbot.yandexweather.Forecasts;
import project.telegram.weatherbot.yandexweather.YandexWeatherResponse;
import project.telegram.weatherbot.properties.LocationProperties;
import project.telegram.weatherbot.properties.YandexWeatherProperties;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class YandexWeatherService {

    private final Sender sender;
    private final LocationProperties locationProperties;
    private final YandexWeatherProperties yandexWeatherProperties;
    private final TaskWeatherProperties taskWeatherProperties;
    private final WeatherService weatherService;

    public YandexWeatherService(Sender sender, LocationProperties locationProperties,
                                YandexWeatherProperties yandexWeatherProperties, TaskWeatherProperties taskWeatherProperties, WeatherService weatherService) {
        this.sender = sender;
        this.locationProperties = locationProperties;
        this.yandexWeatherProperties = yandexWeatherProperties;
        this.taskWeatherProperties = taskWeatherProperties;
        this.weatherService = weatherService;
    }

    private final static String API_YANDEX_WEATHER_HOST = "api.weather.yandex.ru";
    private final static String API_YANDEX_WEATHER_PATH = "/v2/forecast";
    private final static String API_YANDEX_WEATHER_KEY = "X-Yandex-API-Key";

    private final static String API_LATITUDE = "lat";
    private final static String API_LONGITUDE = "lon";
    private final static String WEATHER_FORECAST_MESSAGE = """
            Прогноз погоды %s:
            Минимальная температура ночью %d
            Максимальная температура днем %d
            """;
    private final static String WEATHER_FORECAST_TODAY = """
            Температура сейчас %s
            Максимальная температура днем %s
            """;
    private final static String FORECAST_ERROR_MESSAGE = "Прогноз в данный момент не доступен";

    public YandexWeatherResponse requestYandexWeather() {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_YANDEX_WEATHER_KEY, yandexWeatherProperties.getApiKey());
        Map<String, String> params = new HashMap<>();
        params.put(API_LATITUDE, locationProperties.getLatitude());
        params.put(API_LONGITUDE, locationProperties.getLongitude());

        try {
            ResponseEntity<YandexWeatherResponse> response = sender.sendWithParams(API_YANDEX_WEATHER_HOST, API_YANDEX_WEATHER_PATH, params,
                    headers,  HttpMethod.GET, YandexWeatherResponse.class);
            YandexWeatherResponse responseBody = response.getBody();
            if (responseBody != null) {
                log.info(responseBody.toString());

                if (response.getStatusCode().value() == 200) {
                    return responseBody;
                } else {
                    return null;
                }
            }
            log.error("log");
        } catch (Exception e) {
            log.error("Exception");
        }
        return null;
    }

    public String getForecastForToday() {
        YandexWeatherResponse response = requestYandexWeather();
        return String.format(WEATHER_FORECAST_TODAY, response.getFact().getTemp(),
                response.getForecasts().get(0).getParts().getDay().getTemp_max());
    }

    public String getTemperature() {
        YandexWeatherResponse response = requestYandexWeather();
        return response.getFact().getTemp();
    }

    public String getForecastOfDate(String date) {
        Optional<Forecasts> forecast = requestYandexWeather().getForecasts().stream().filter(forecasts -> forecasts.getDate().equals(date)).findFirst();
        if (forecast.isPresent()) {
            return String.format(WEATHER_FORECAST_MESSAGE, forecast.get().getDate(), forecast.get().getParts().getNight().getTemp_min(),
                    forecast.get().getParts().getDay().getTemp_max());
        }
        return FORECAST_ERROR_MESSAGE;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void saveWeather() {
        try {
            if (LocalDateTime.now(ZoneId.of(locationProperties.getTimeZone())).getHour() == taskWeatherProperties.getSaveHour()) {
                log.debug("Start processing save weather");
                Weather weather = new Weather();
                YandexWeatherResponse response = requestYandexWeather();
                weather.setDate(response.getForecasts().get(0).getDate());
                weather.setDayTempMax(response.getForecasts().get(0).getParts().getDay().getTemp_max());
                weather.setNightTempMin(response.getForecasts().get(0).getParts().getNight().getTemp_min());
                weatherService.save(weather);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
