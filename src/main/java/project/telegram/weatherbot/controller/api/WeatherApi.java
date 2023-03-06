package project.telegram.weatherbot.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get a weather by its date",
            description = "Возвращает информацию о погоде в указанную дату. Дата задается в формате yyyy-MM-dd")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
                    })
    @GetMapping("/weather")
    @ResponseStatus(HttpStatus.OK)
    WeatherDto getWeather(@RequestParam @Parameter(description = "Дата в формате yyyy-MM-dd") String date);
}
