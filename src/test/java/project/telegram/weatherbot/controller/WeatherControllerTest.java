package project.telegram.weatherbot.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import project.telegram.weatherbot.controller.api.WeatherController;
import project.telegram.weatherbot.dto.WeatherDto;
import project.telegram.weatherbot.service.WeatherService;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private WeatherService weatherService;

    private static final String DATE = "2023-01-01";
    private static final Integer NIGHT_TEMP_MIN = -10;
    private static final Integer DAY_TEMP_MAX = 5;

    @Test
    public void getAllEmployeesAPI() throws Exception {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setDayTempMax(DAY_TEMP_MAX);
        weatherDto.setNightTempMin(NIGHT_TEMP_MIN);
        given(weatherService.getWeather(Mockito.any())).willReturn(weatherDto);
        mvc.perform(get("/api/weather?date={}", DATE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nightTempMin", is(NIGHT_TEMP_MIN)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dayTempMax", is(DAY_TEMP_MAX)));
    }
}
