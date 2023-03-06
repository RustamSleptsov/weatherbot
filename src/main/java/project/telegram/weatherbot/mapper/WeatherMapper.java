package project.telegram.weatherbot.mapper;

import org.mapstruct.Mapper;
import project.telegram.weatherbot.dto.WeatherDto;
import project.telegram.weatherbot.model.Weather;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    WeatherDto toDto(Weather weather);

}
