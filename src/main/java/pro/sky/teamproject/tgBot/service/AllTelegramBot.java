package pro.sky.teamproject.tgBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.teamproject.tgBot.config.AllTelegramBotConfiguration;
import pro.sky.teamproject.tgBot.model.Report;
import pro.sky.teamproject.tgBot.model.Shelter;
import pro.sky.teamproject.tgBot.model.user.Role;
import pro.sky.teamproject.tgBot.model.user.User;

import java.sql.Timestamp;
import java.util.*;

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
        if (update.hasMessage() && update.getMessage().hasText() || update.getMessage().hasContact()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (update.getMessage().hasContact()) {
                startCommandReceived(chatId, update.getMessage());
            } else
            if (userService.findUserByChatId(chatId) != null && userService.findUserByChatId(chatId).getRole().equals(Role.VOLUNTEER)) {
                switch (messageText) {
                    case "Просмотреть отчеты за сегодня" -> {
                        List<Report> reports = reportService.findAllReports();
                        reports.stream().forEach(r -> {
                            if (checkDateTime(r.getDatetime())) {
                                sendMessage(chatId, "Отчет от пользователя: " + r.getUser().getName());
                                sendMessage(chatId, "ID отчета: " + r.getId());
                                sendMessage(chatId, r.getInfo());
                                sendPhoto(chatId, r.getPhoto());
                            }
                        });
                    }
                    case "Как подтвердить отчеты?" -> {
                        sendMessage(chatId, "Введите сообщение в формате:");
                        sendMessage(chatId, "Ок: *ID отчета*");
                    }
                    default -> {
                        if (messageText.startsWith("Ок: ") || messageText.startsWith("Ok: ")) {
                            String[] parts = messageText.split(": ");
                            try {
                                long reportId = Long.parseLong(parts[1]);
                                Report report = reportService.findReportById(reportId);
                                report.setIsReportValid(true);
                                reportService.updateReport(report);
                            } catch (NumberFormatException e) {
                                sendMessage(chatId, "Проверьте корректность ID");
                            }
                        }
                        sendButtons(chatId, "Что бы вы хотели?", telegramBotConfiguration.getRowVoloMainChoice());
                    }
                }
            } else switch (messageText) {
                case "/start" -> {
                    if (userService.findUserByChatId(chatId) == null) createStartButton(chatId);
                    else sendButtons(chatId, "Выбери питомник:", List.of(telegramBotConfiguration.getRowShelters()));
                }
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
                    if (sessions.get(chatId) == 2L) {
                        keyboardRows.add(new KeyboardRow(List.of(new KeyboardButton("Советы кинолога"))));
                    }
                    sendButtons(chatId, "Что вы хотите узнать?", keyboardRows);
                }
                case "Отправить отчет" -> {
                    sendMessage(chatId, "Пожалуйста отправте отчет в формате:");
                    //В данном случаи номер договора = id в DB нашего adoption, предполагается что когда человек будет забирать питомца, ему сообщат номер договора
                    sendMessage(chatId, "Вот мой отчет. Номер договора: Опишите как дела у вашего питомца, все ли хорошо. Возникли ли какие нибуть трудности. Прикрепите фото.");
                    sendMessage(chatId, "Например:");
                    sendMessage(chatId, "Вот мой отчет. 32: У нас с Кузей все хорошо, едим 3 раза в день, вчера сходили в клинику поставить прививки, сейчас Кузя отдыхает, Смотрите какой он милый) Фото.");
                }
                case "Позвать волонтера" -> {
                    Random random = new Random();
                    List<User> volunteers = userService.findUsersByRole("VOLUNTEER");
                    User volunteer = volunteers.get(random.nextInt(volunteers.size()));
                    User client = userService.findUserByChatId(chatId);
                    sendMessage(volunteer.getChatId(), "Свяжитесь пожалуйста с пользователем, у него появились вопросы.");
                    sendMessage(volunteer.getChatId(), "Имя пользователя: " + client.getName());
                    sendMessage(volunteer.getChatId(), "Контактный телефон: " + client.getPhone());
                    sendButtons(chatId, "Мы отправили ваши данные волонтеру, с вами скоро свяжутся. Хотите что-нибуть узнать?", telegramBotConfiguration.getRowMainChoice());
                }
                case "Выбрать другое животное" -> {
                    sessions.remove(chatId);
                    startCommandReceived(chatId, update.getMessage());
                }
                case "Меню" -> sendButtons(chatId, "Что бы вы хотели?", telegramBotConfiguration.getRowMainChoice());
                case "Общая информация"  ->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getInfo());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Адрес и режим работы" ->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getAddressShelter());
                    sendMessage(chatId, shelters.getWorkingTime());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Получить пропуск для машины" -> {
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getDrivingDirections());
                    sendMessage(chatId, shelters.getSecurityContactDetails());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Техника безопасности" ->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getSafetyTechnique());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Список документов"->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getDocList());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Рекомендации по транспортировке" ->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getAdviceTransport());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Рекомендации по обустройству для детёныша"->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getAdviceCub());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Рекомендации по обустройству для взрослого животного"->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getAdviceAdult());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Рекомендации по обустройству для ограниченного животного"->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getAdviceInvalid());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Причины отказа" -> {
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getRefusalReasons());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));

                }
                case "Советы кинолога" ->{
                    Shelter shelters = shelterService.findShelters(sessions.get(chatId));
                    sendMessage(chatId, shelters.getDopInfo());
                    sendButtons(chatId, "Вы хотели бы что-то еще?", List.of(telegramBotConfiguration.getRowDefault()));
                }
                case "Отправить нам ваши контактные данные" -> {
                    checkUser(chatId, update.getMessage());
                }
                default -> {
                    if (messageText.startsWith("Вот мой отчет")) {
                        parseReportMessage(chatId, update.getMessage());
                    } else
                        sendButtons(chatId, "Я затрудняюсь ответить на это, позвать волонтера?", List.of(telegramBotConfiguration.getRowDefault()));
                }
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
        System.out.println(message);
        if(userService.findUserByChatId(chatId)== null){
        User user = new User();
        user.setChatId(chatId);
        user.setName(message.getContact().getFirstName());
        user.setPhone(message.getContact().getPhoneNumber());
        userService.addUser(user);
        }
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
     * Отправляет фотографию в указанный чат.
     *
     * @param chatId Идентификатор чата, куда будет отправлена фотография.
     * @param fileId Идентификатор файла (file_id) фотографии в Telegram.
     */
    private void sendPhoto(long chatId, String fileId){
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(fileId));
        try {
            execute(photo);
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
        if(userService.findUserByChatId(chatId) == null){
        User user = new User();
        user.setChatId(chatId);
        user.setName(message.getContact().getFirstName());
        user.setPhone(message.getContact().getPhoneNumber());
        userService.addUser(user);
        }
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
        if(parts.length == 2 && message.hasPhoto()){
            String id = parts[0].replaceAll("[^0-9]", "");
            String text = parts[1];
            Report report = new Report();
            report.setUser(userService.findUserByChatId(chatId));
            report.setInfo(text);
            List<PhotoSize> photos = message.getPhoto();
            PhotoSize photo = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
            if(photo != null){
               report.setPhoto(photo.getFileId());
            }
            report.setDatetime(new Timestamp(System.currentTimeMillis()));
            reportService.addReport(report);
            sendButtons(chatId, "Ваш отчет сохранен, вы хотите что нибуть еще?", List.of(telegramBotConfiguration.getRowDefault()));
        } else sendButtons(chatId, "Ваш отчет нераспознан, попробуйте снова.", List.of(telegramBotConfiguration.getRowDefault()));
    }

    /**
     * Проверяет, находится ли временная метка в пределах сегодняшнего дня.
     *
     * @param timeTask Временная метка, которую необходимо проверить.
     * @return Возвращает true, если временная метка находится в пределах сегодняшнего дня, иначе возвращает false.
     */
    private boolean checkDateTime(Timestamp timeTask){
        Timestamp today = new Timestamp(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(today.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp startOfToday = new Timestamp(calendar.getTime().getTime());

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Timestamp endOfToday = new Timestamp(calendar.getTime().getTime());

        return timeTask.after(startOfToday) && timeTask.before(endOfToday);
    }
    private void createStartButton(long chatId){
        KeyboardButton startButton = new KeyboardButton("Поделится контактной информацией");
        startButton.setRequestContact(true);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(List.of(new KeyboardRow(List.of(startButton))));

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Нажимая на кнопку вы предоставляете нам вашу контактную информацию");
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
