package project.telegram.weatherbot.yandexweather;

import lombok.Data;

import java.util.List;

@Data
public class YandexWeatherResponse {

    private Fact fact;
    private List<Forecasts> forecasts;
}
