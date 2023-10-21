package pro.sky.teamproject.tgBot.api.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.teamproject.tgBot.api.config.AllTelegramBotConfiguration;
import pro.sky.teamproject.tgBot.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AllTelegramBot extends TelegramLongPollingBot {

    private final AllTelegramBotConfiguration telegramBotConfiguration;
    private Map<Long, String> sessions;

    public AllTelegramBot(AllTelegramBotConfiguration telegramBotConfiguration){
        this.telegramBotConfiguration = telegramBotConfiguration;
        sessions = new HashMap<>();
    }
    @Override
    public String getBotUsername(){
        return telegramBotConfiguration.getBotName();
    }
    @Override
    public String getBotToken(){
        return telegramBotConfiguration.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start" -> startCommandReceived(chatId, update.getMessage());
                case "Кошки" -> {
                    sendButtons(chatId, "Что бы вы хотели?", telegramBotConfiguration.getRowMainChoice());
                    sessions.put(chatId, "cat");
                }
                case "Собаки" -> {
                    sendButtons(chatId, "Что бы вы хотели?", telegramBotConfiguration.getRowMainChoice());
                    sessions.put(chatId, "dog");
                }
                case "Информация о приюте" -> sendMessage(chatId, "заглушка");
                case "Информация как взять питомца" -> sendMessage(chatId, "заглушка");
                case "Отправить отчет" -> sendMessage(chatId, "заглушка");
                case "Позвать волонтера" -> sendMessage(chatId, "заглушка");
                case "Выбрать другое животное" -> {
                    sessions.remove(chatId);
                    startCommandReceived(chatId, update.getMessage());
                }
                case "Меню" ->
                        sendButtons(chatId, "Что бы вы хотели?", telegramBotConfiguration.getRowMainChoice());
                default ->
                        sendButtons(chatId, "Я затрудняюсь ответить на это, позвать волонтера?", List.of(telegramBotConfiguration.getRowDefault()));
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    private void startCommandReceived(long chatId, Message message) {
        //Тут должна быть проверка в БД по чатID, если чела нет в бд, то его данные заносятся в БД
        //if(userService.getUserByChatId() == null){
        //User user = new User();
       // user.setChatId(chatId);
        //user.setName(message.getContact().getFirstName());
        //user.setPhone(message.getContact().getPhoneNumber());
        //userService.addUser(user);
        //}
       sendMessage(chatId, "Привет");
       sendButtons(chatId, "Выбери питомник:", List.of(telegramBotConfiguration.getRowShelters()));


    }

    private void sendMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }

    }

    private void sendButtons(long chatId,String text, List<KeyboardRow> rows){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
