package project.telegram.weatherbot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import project.telegram.weatherbot.model.User;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByTelegramChatId(Long telegramChatId);
    List<User> findByNotifyIsTrue();

}
