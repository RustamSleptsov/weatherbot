package project.telegram.weatherbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.telegram.weatherbot.model.User;
import project.telegram.weatherbot.repository.UserRepository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private static final Long TELEGRAM_CHAT_ID = 1234L;

    @BeforeEach
    protected void init() {
        userService = new UserService(userRepository);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setTelegramChatId(TELEGRAM_CHAT_ID);
        user.setNotify(true);

        given(userRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).willReturn(user);
        boolean isCreate = userService.createSubscriber(TELEGRAM_CHAT_ID);
        then(isCreate).isFalse();

        user.setNotify(false);
        isCreate = userService.createSubscriber(TELEGRAM_CHAT_ID);
        then(isCreate).isTrue();

        given(userRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).willReturn(null);
        isCreate = userService.createSubscriber(TELEGRAM_CHAT_ID);
        then(isCreate).isTrue();

    }
}
