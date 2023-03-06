package project.telegram.weatherbot.yandexweather;

import lombok.Data;

@Data
public class Forecasts {

    private String date;
    private Parts parts;

}
