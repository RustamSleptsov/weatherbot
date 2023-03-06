package project.telegram.weatherbot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import project.telegram.weatherbot.model.Weather;

@Repository
public interface WeatherRepository extends MongoRepository<Weather, String> {

    Weather findByDate(String date);
}
