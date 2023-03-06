package project.telegram.weatherbot.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import project.telegram.weatherbot.dto.WeatherDto;
import project.telegram.weatherbot.model.Weather;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(MockitoExtension.class)
public class WeatherMapperTest {

    private final WeatherMapper weatherMapper
            = Mappers.getMapper(WeatherMapper.class);

    private static final String DATE = "2023.01.01";
    private static final Integer NIGHT_TEMP_MIN = -10;
    private static final Integer DAY_TEMP_MAX = 20;


    @Test
    public void testWeatherMapper() {
        Weather weather = new Weather();
        weather.setDate(DATE);
        weather.setNightTempMin(NIGHT_TEMP_MIN);
        weather.setDayTempMax(DAY_TEMP_MAX);

        WeatherDto weatherDto = weatherMapper.toDto(weather);

        then(weatherDto.getNightTempMin()).isEqualTo(NIGHT_TEMP_MIN);
        then(weatherDto.getDayTempMax()).isEqualTo(DAY_TEMP_MAX);

    }

}
