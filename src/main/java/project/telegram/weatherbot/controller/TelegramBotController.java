package project.telegram.weatherbot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import project.telegram.weatherbot.properties.TelegramProperties;
import project.telegram.weatherbot.service.TelegramBotService;

@Slf4j
@RestController
public class TelegramBotController {

    protected TelegramBot bot;

    private final TelegramBotService telegramBotService;
    private final TelegramProperties telegramProperties;

    public TelegramBotController(TelegramBotService telegramBotService, TelegramProperties telegramProperties) {
        this.telegramBotService = telegramBotService;
        this.telegramProperties = telegramProperties;
    }

    @PostConstruct
    private void init() {
        bot = new TelegramBot(telegramProperties.getToken());
        bot.setUpdatesListener(updates -> {updates.forEach(this::post);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void post(Update update) {
        Message message = update.message();
        if (message != null && message.chat() != null) {
            telegramBotService.processMessage(message);
        } else {
            CallbackQuery callbackQuery = update.callbackQuery();
            if (callbackQuery != null) {
                telegramBotService.processCallbackQuery(callbackQuery);
            }
        }
    }

}
