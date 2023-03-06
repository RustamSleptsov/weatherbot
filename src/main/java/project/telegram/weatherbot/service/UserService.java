package project.telegram.weatherbot.service;

import org.springframework.stereotype.Service;
import project.telegram.weatherbot.model.Role;
import project.telegram.weatherbot.model.User;
import project.telegram.weatherbot.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean createSubscriber(Long chatId) {
        if (userRepository.findByTelegramChatId(chatId) != null && userRepository.findByTelegramChatId(chatId).getNotify()) {
            return false;
        }
        if (userRepository.findByTelegramChatId(chatId) != null && !userRepository.findByTelegramChatId(chatId).getNotify()) {
            User user = userRepository.findByTelegramChatId(chatId);
            user.setNotify(true);
            userRepository.save(user);
            return true;
        }
        User user = new User();
        user.setRole(Role.USER);
        user.setNotify(true);
        user.setTelegramChatId(chatId);
        addUser(user);
        return true;
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public void deleteSubscriber(Long chatId) {
        User user = userRepository.findByTelegramChatId(chatId);
        if (user != null) {
            if (user.getRole().equals(Role.ADMIN)) {
                user.setNotify(false);
                userRepository.save(user);
            } else {
                userRepository.delete(user);
            }
        }
    }

    public User getUserOfTelegramChatId(Long chatId) {
        return userRepository.findByTelegramChatId(chatId);
    }

    public Integer getCountSubscribers() {
        return userRepository.findByNotifyIsTrue().size();
    }
    public List<User> getSubscribers() {
        return userRepository.findByNotifyIsTrue();
    }
}
