package project.telegram.weatherbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import project.telegram.weatherbot.model.Role;
import project.telegram.weatherbot.model.User;
import project.telegram.weatherbot.properties.LocationProperties;
import project.telegram.weatherbot.properties.TaskWeatherProperties;
import project.telegram.weatherbot.properties.TelegramProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class TelegramBotService {

    private final UserService userService;
    private final TelegramProperties telegramProperties;
    private final LocationProperties locationProperties;
    private final YandexWeatherService yandexWeatherService;
    private final TaskWeatherProperties taskWeatherProperties;

    private TelegramBot bot;

    public final static String COMMAND_START = "/start";
    public final static String COMMAND_SUBSCRIBE = "/subscribe";
    public final static String COMMAND_UNSUBSCRIBE = "/unsubscribe";
    public final static String COMMAND_SUBSCRIBE_DESCRIPTION = "Подписаться на уведомления";
    public final static String COMMAND_UNSUBSCRIBE_DESCRIPTION = "Отписаться от уведомлений";

    public final static String BUTTON_WEATHER_NOW = "Погода сейчас";
    public final static String BUTTON_WEATHER_FORECAST = "Прогноз погоды";
    public final static String BUTTON_COUNT_SUBSCRIBERS = "Количество подписчиков";

    public final static String WELCOME_MESSAGE = "%sЗдравствуйте! Это бот, который информирует вас о погоде в городе %s";
    public final static String SUBSCRIBE_MESSAGE = """
            Вы подписались на уведомления!%s
            Каждый день в %s часов вам будет приходить прогноз погоды на день!
            """;
    public final static String ALREADY_SUBSCRIBE_MESSAGE = "%sВы уже подписаны";
    public final static String UNSUBSCRIBE_MESSAGE = "Вы отписались от уведомлений%s";
    public final static String COUNT_SUBSCRIBERS_MESSAGE = "%sКоличество подписчиков: %s";
    public final static String FORECAST_MESSAGE = "%sПрогноз погоды на неделю";

    public final static String WEATHER_ICON = "\ud83c\udf26\ufe0f";
    public final static String WEATHER_BAD_ICON = "\u26c8\ufe0f";
    public final static String WEATHER_SUN_ICON = "\ud83c\udf24\ufe0f";

    public final static String API_LANGUAGE = "ru";
    public final static String SYMBOL_COLON = ": ";

    public static final DateTimeFormatter YANDEX_WEATHER_API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TelegramBotService(UserService userService, TelegramProperties telegramProperties, LocationProperties locationProperties, YandexWeatherService yandexWeatherService, TaskWeatherProperties taskWeatherProperties) {
        this.userService = userService;
        this.telegramProperties = telegramProperties;
        this.locationProperties = locationProperties;
        this.yandexWeatherService = yandexWeatherService;
        this.taskWeatherProperties = taskWeatherProperties;
    }

    @PostConstruct
    private void init() {
        bot = new TelegramBot(telegramProperties.getToken());
        bot.execute(new SetMyCommands(new BotCommand(COMMAND_SUBSCRIBE, COMMAND_SUBSCRIBE_DESCRIPTION),
                new BotCommand(COMMAND_UNSUBSCRIBE, COMMAND_UNSUBSCRIBE_DESCRIPTION)
        ));
    }

    public void sendMessage(Long chatId, String htmlText, InlineKeyboardMarkup keyboard, TelegramBot bot) {
        if (bot == null) {
            log.info("Telegram bot is not configured");
            return;
        }
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton(BUTTON_WEATHER_NOW),
                new KeyboardButton(BUTTON_WEATHER_FORECAST));

        User user = userService.getUserOfTelegramChatId(chatId);
        if (user != null && user.getRole().equals(Role.ADMIN)) {
            replyKeyboardMarkup.addRow(BUTTON_COUNT_SUBSCRIBERS);
        }
        replyKeyboardMarkup.resizeKeyboard(true);
        replyKeyboardMarkup.oneTimeKeyboard(false);
        replyKeyboardMarkup.selective(false);
        SendMessage sendMessage = new SendMessage(chatId, htmlText);
        if (keyboard != null) {
            sendMessage.replyMarkup(keyboard);
        }
        sendMessage.parseMode(ParseMode.HTML);
        sendMessage.disableWebPagePreview(true);

        bot.execute(sendMessage);
    }

    /**
     * Process basic text message
     */
    public void processMessage(Message message) {
        Long chatId = message.chat().id();
        String messageText = message.text();
        if (messageText != null) {
            switch (messageText) {
                case COMMAND_START:
                    sendMessage(chatId, String.format(WELCOME_MESSAGE, WEATHER_ICON, locationProperties.getName()),
                            null, bot);
                    break;
                case COMMAND_SUBSCRIBE:
                    boolean isAdd = userService.createSubscriber(chatId);
                    if (isAdd) {
                        sendMessage(chatId, String.format(SUBSCRIBE_MESSAGE, WEATHER_SUN_ICON,
                                taskWeatherProperties.getNotificationHour()), null, bot);
                    } else {
                        sendMessage(chatId, String.format(ALREADY_SUBSCRIBE_MESSAGE, WEATHER_ICON), null, bot);
                    }
                    break;
                case COMMAND_UNSUBSCRIBE:
                    userService.deleteSubscriber(chatId);
                    sendMessage(chatId, String.format(UNSUBSCRIBE_MESSAGE, WEATHER_BAD_ICON), null , bot);
                    break;
                case BUTTON_WEATHER_NOW:
                    sendMessage(chatId, WEATHER_ICON + yandexWeatherService.getTemperature(), null, bot);
                    break;
                case BUTTON_WEATHER_FORECAST:
                    notifyForecasts(chatId);
                    break;
                case BUTTON_COUNT_SUBSCRIBERS:
                    if (userService.getUserOfTelegramChatId(chatId).getRole().equals(Role.ADMIN)) {
                        sendMessage(chatId, String.format(COUNT_SUBSCRIBERS_MESSAGE, WEATHER_ICON,
                                userService.getCountSubscribers()), null, bot);
                    }
                    break;
            }
        }
    }

    /**
     * Process callback command manage action or support chat
     */
    public void processCallbackQuery(CallbackQuery callbackQuery) {
        Message message = callbackQuery.message();
        Long chatId = message.chat().id();
        String callbackData = callbackQuery.data();
        if (callbackData.startsWith(WEATHER_ICON)) {
            String date = callbackData.substring(WEATHER_ICON.length());
            String messageForecast = yandexWeatherService.getForecastOfDate(date);
            sendMessage(chatId, messageForecast, null, bot);
        }
    }

    public void notifyForecasts(Long chatId) {
        List<LocalDate> localDates = new ArrayList<>();
        for (int i = 0; i<7; i++) {
            LocalDate localDate = LocalDate.now(ZoneId.of(locationProperties.getTimeZone()));
            localDates.add(localDate.plusDays(i));
        }
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
            localDates.stream().map(forecast -> new InlineKeyboardButton[]{
                new InlineKeyboardButton(
                    WEATHER_ICON.concat(forecast.format(YANDEX_WEATHER_API_DATE_FORMAT).concat(SYMBOL_COLON).
                    concat(forecast.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag(API_LANGUAGE))))
                ).callbackData(WEATHER_ICON.concat(forecast.format(YANDEX_WEATHER_API_DATE_FORMAT)))
            }).toList().toArray(new InlineKeyboardButton[localDates.size()][1])
        );
        sendMessage(chatId, String.format(FORECAST_MESSAGE, WEATHER_ICON), inlineKeyboard, bot);
    }

    /**
     * Every hour check hour and on 10 am (or another by settings) every day, notify subscribers about weather today
     */
    @Scheduled(cron = "0 0 * * * *")
    public void notifySubscribersAboutWeatherEach24Hour() {
        try {
            if (LocalDateTime.now().getHour() == taskWeatherProperties.getNotificationHour()) {
                log.debug("Start processing action notifications");
                notifySubscribersAboutWeather();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void notifySubscribersAboutWeather() {
        List<User> subscribers = userService.getSubscribers();
        String message = yandexWeatherService.getForecastForToday();
        subscribers.forEach(subscriber -> sendMessage(subscriber.getTelegramChatId(), message, null, bot));
    }
}
