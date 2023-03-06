package project.telegram.weatherbot.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Weather {

    private String date;
    private Integer nightTempMin;
    private Integer dayTempMax;
}
