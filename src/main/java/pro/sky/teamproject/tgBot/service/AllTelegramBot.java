package pro.sky.teamproject.tgBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.teamproject.tgBot.config.AllTelegramBotConfiguration;
import pro.sky.teamproject.tgBot.model.Report;
import pro.sky.teamproject.tgBot.model.Shelter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Класс AllTelegramBot представляет собой Telegram-бота, который обрабатывает входящие обновления и
 * выполняет различные действия, включая отправку сообщений и клавиатур с кнопками.
 *
 * Этот класс является компонентом Spring Framework и используется для обработки входящих запросов и
 * взаимодействия с Telegram API.
 *
 * @author Michail Z. (GH: HeimTN)
 */
@Component
public class AllTelegramBot extends TelegramLongPollingBot {

    private final AllTelegramBotConfiguration telegramBotConfiguration;
    // 1.long - chatId; 2.long - id shelter on DB
    private Map<Long, Long> sessions;

    private ShelterService shelterService;
    private UserService userService;
    private ReportService reportService;

    public AllTelegramBot(AllTelegramBotConfiguration telegramBotConfiguration,
                          ShelterService shelterService,
                          UserService userService,
                          ReportService reportService){
        this.telegramBotConfiguration = telegramBotConfiguration;
        sessions = new HashMap<>();
        this.shelterService = shelterService;
        this.userService = userService;
        this.reportService = reportService;
    }
    @Override
    public String getBotUsername(){
        return telegramBotConfiguration.getBotName();
    }
    @Override
    public String getBotToken(){
        return telegramBotConfiguration.getToken();
    }

    /**
     * Обработчик события onUpdateReceived. Этот метод вызывается при получении нового обновления
     * от Telegram API. Он анализирует обновление, определяет тип сообщения и выполняет соответствующие
     * действия в зависимости от содержания сообщения.
     *
     * @param update Объект Update, представляющий полученное обновление от Telegram API.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if(messageText.startsWith("Вот мой отчет")){
                parseReportMessage(chatId, update.getMessage());
            }
            else switch (messageText) {
                case "/start" -> startCommandReceived(chatId, update.getMessage());
                case "Кошки" -> {
                    sendButtons(chatId, "Что бы вы хотели?", telegramBotConfiguration.getRowMainChoice());
                    sessions.put(chatId, 1L);
                }
                case "Собаки" -> {
                    sendButtons(chatId, "Что бы вы хотели?", telegramBotConfiguration.getRowMainChoice());
                    sessions.put(chatId, 2L);
                }
                case "О приюте" -> sendButtons(chatId, "Что вы хотите узнать?", telegramBotConfiguration.getRowInfoShelterChoice());
                case "Как взять питомца" -> {
                    List<KeyboardRow> keyboardRows = telegramBotConfiguration.getRowHowGetAnimalChoice();
                    if(sessions.get(chatId) == 2L){
                        keyboardRows.add(new KeyboardRow(List.of(new KeyboardButton("Советы кинолога"))));
                    }
                    sendButtons(chatId, "Что вы хотите узнать?", keyboardRows);
                }
                case "Отправить отчет" -> {
                    sendMessage(chatId, "Пожалуйста отправте отчет в формате:");
                    //В данном случаи номер договора = id в DB нашего adoption, предполагается что когда человек будет забирать питомца, ему сообщат номер договора
                    sendMessage(chatId, "Вот мой отчет. Номер договора: Опишите как дела у вашего питомца, все ли хорошо. Возникли ли какие нибуть трудности. Прикрепите фото.");
                    sendMessage(chatId,"Например:");
                    sendMessage(chatId, "Вот мой отчет. 32: У нас с Кузей все хорошо, едим 3 раза в день, вчера сходили в клинику поставить прививки, сейчас Кузя отдыхает, Смотрите какой он милый) Фото.");
                }
                case "Позвать волонтера" -> sendMessage(chatId, "заглушка");
                case "Выбрать другое животное" -> {
                    sessions.remove(chatId);
                    startCommandReceived(chatId, update.getMessage());
                }
                case "Меню" ->
                        sendButtons(chatId, "Что бы вы хотели?", telegramBotConfiguration.getRowMainChoice());
                //Так как инфа у нас пока что только одна в шелтере, то пока что так. Позже надо подправить
                case "Общая информация", "Адрес и режим работы", "Получить пропуск для машины",
                        "Техника безопасности", "Правила знакомства", "Список документов", "Рекомендации по транспортировке",
                        "Рекомендации по обустройству для детёныша", "Рекомендации по обустройству для взрослого животного",
                        "Рекомендации по обустройству для ограниченного животного", "Причины отказа" ->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getInfo());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));

                }
                case "Отправить нам ваши контактные данные" ->{
                    checkUser(chatId, update.getMessage());
                }
                default ->
                        sendButtons(chatId, "Я затрудняюсь ответить на это, позвать волонтера?", List.of(telegramBotConfiguration.getRowDefault()));
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
    /**
     * Обработчик команды "/start". Этот метод вызывается при получении команды "/start" от пользователя и
     * выполняет инициализацию чата и отправку приветственного сообщения с кнопками выбора питомника.
     * Добавляет пользователя в базу данных, если он обращается впервые.
     *
     * @param chatId   Уникальный идентификатор чата пользователя.
     * @param message  Объект Message, содержащий информацию о сообщении от пользователя, включая контактные данные.
     */
    private void startCommandReceived(long chatId, Message message) {
//        if(userService.getUserByChatId() == null){
//        User user = new User();
//        user.setChatId(chatId);
//        user.setName(message.getContact().getFirstName());
//        user.setPhone(message.getContact().getPhoneNumber());
//        userService.addUser(user);
//        }
       sendMessage(chatId, "Привет");
       sendButtons(chatId, "Выбери питомник:", List.of(telegramBotConfiguration.getRowShelters()));


    }
    /**
     * Отправляет текстовое сообщение заданному чату (пользователю) через Telegram API.
     *
     * @param chatId Уникальный идентификатор чата, куда будет отправлено сообщение.
     * @param text   Текст сообщения, который необходимо отправить.
     */
    public void sendMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }

    }
    /**
     * Отправляет сообщение с кнопками пользователю через Telegram API.
     *
     * @param chatId  Уникальный идентификатор чата, куда будет отправлено сообщение с кнопками.
     * @param text    Текст сообщения, который сопровождает кнопки.
     * @param rows    Список строк кнопок, представленных в виде объектов KeyboardRow.
     */
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

    /**
     * Проверяет наличие пользователя по идентификатору чата и обновляет данные, если пользователь не найден.
     *
     * @param chatId   идентификатор чата пользователя
     * @param message  объект сообщения, содержащий информацию о пользователе
     */
    private void checkUser(long chatId, Message message){
//        if(userService.getUserByChatId() == null){
//        User user = new User();
//        user.setChatId(chatId);
//        user.setName(message.getContact().getFirstName());
//        user.setPhone(message.getContact().getPhoneNumber());
//        userService.addUser(user);
//        }
        sendMessage(chatId, "Ваши данные обновленны");
    }

    /**
     * Парсит сообщение, содержащее отчет о питомце, и сохраняет его в базу данных.
     *
     * @param chatId   идентификатор чата пользователя
     * @param message  объект сообщения, содержащий отчет о питомце
     */
    private void parseReportMessage(long chatId, Message message){
        String messageText = message.getText();
        String[] parts = messageText.split(": ", 2);
        if(parts.length == 2){
            String id = parts[0].replaceAll("[^0-9]", "");
            String text = parts[1];
            Report report = new Report();
           // report.setUser(userService.getUserByChatId(chatId));
            report.setInfo(text);
           // report.setPhoto(message.getPhoto());
            report.setDatetime(new Timestamp(System.currentTimeMillis()));
            reportService.addReport(report);
            sendButtons(chatId, "Ваш отчет сохранен, вы хотите что нибуть еще?", List.of(telegramBotConfiguration.getRowDefault()));
        } else sendButtons(chatId, "Ваш отчет нераспознан, попробуйте снова.", List.of(telegramBotConfiguration.getRowDefault()));
    }
}
