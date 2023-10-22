package pro.sky.teamproject.tgBot.api.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pro.sky.teamproject.tgBot.api.service.AllTelegramBot;
/**
 * Класс BotInitializer представляет собой инициализатор бота, который регистрирует Telegram-бота для
 * обработки входящих обновлений после события запуска контекста приложения.
 *
 * Этот класс является компонентом Spring Framework и обеспечивает настройку и регистрацию Telegram-бота
 * для взаимодействия с Telegram API.
 *
 * @author Michail Z. (GH: HeimTN)
 */
@Component
public class BotInitializer {

    private AllTelegramBot telegramBot;

    public BotInitializer(AllTelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }
    /**
     * Метод init() регистрирует бота при запуске контекста приложения.
     *
     * @throws TelegramApiException Возникает в случае ошибки при регистрации бота в Telegram API.
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException{
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try{
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
