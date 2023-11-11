package pro.sky.teamproject.tgBot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс AllTelegramBotConfiguration представляет собой конфигурацию Telegram-бота, включая его имя, токен и
 * клавиатуры с кнопками для взаимодействия с пользователями.
 *
 * Этот класс является конфигурационным компонентом Spring Framework и используется для настройки параметров
 * Telegram-бота и создания клавиатур с кнопками.
 *
 * @author Michail Z. (GH: HeimTN)
 */
@Configuration
@PropertySource("application.properties")
public class AllTelegramBotConfiguration {

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${telegram.bot.token}")
    private String token;

    private KeyboardRow rowShelters;
    private List<KeyboardRow> rowMainChoice;
    private KeyboardRow rowDefault;
    private List<KeyboardRow> rowInfoShelterChoice;
    private List<KeyboardRow> rowHowGetAnimalChoice;
    private List<KeyboardRow> rowVoloMainChoice;
    /**
     * Конструктор класса AllTelegramBotConfiguration. В конструкторе инициализируются клавиатуры с кнопками.
     */
    public AllTelegramBotConfiguration(){
        rowShelters = new KeyboardRow();
        rowMainChoice = new ArrayList<>();
        rowDefault = new KeyboardRow();
        rowInfoShelterChoice = new ArrayList<>();
        rowHowGetAnimalChoice = new ArrayList<>();
        rowVoloMainChoice = new ArrayList<>();

        rowShelters.add(new KeyboardButton("Кошки"));
        rowShelters.add(new KeyboardButton("Собаки"));

        rowMainChoice.add(new KeyboardRow(List.of(new KeyboardButton("О приюте"))));
        rowMainChoice.add(new KeyboardRow(List.of(new KeyboardButton("Как взять питомца"))));
        rowMainChoice.add(new KeyboardRow(List.of(new KeyboardButton("Отправить отчет"))));
        rowMainChoice.add(new KeyboardRow(List.of(new KeyboardButton("Позвать волонтера"))));

        rowDefault.add("Позвать волонтера");
        rowDefault.add("Выбрать другое животное");
        rowDefault.add("Меню");

        rowInfoShelterChoice.add(new KeyboardRow(List.of(new KeyboardButton("Общая информация"))));
        rowInfoShelterChoice.add(new KeyboardRow(List.of(new KeyboardButton("Адрес и режим работы"))));
        rowInfoShelterChoice.add(new KeyboardRow(List.of(new KeyboardButton("Получить пропуск для машины"))));
        rowInfoShelterChoice.add(new KeyboardRow(List.of(new KeyboardButton("Техника безопасности"))));
        rowInfoShelterChoice.add(new KeyboardRow(List.of(new KeyboardButton("Отправить нам ваши контактные данные"))));
        rowInfoShelterChoice.add(new KeyboardRow(List.of(new KeyboardButton("Позвать волонтера"))));
        rowInfoShelterChoice.add(new KeyboardRow(List.of(new KeyboardButton("Меню"))));

        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Правила знакомства"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Список документов"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Рекомендации по транспортировке"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Рекомендации по обустройству для детёныша"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Рекомендации по обустройству для взрослого животного"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Рекомендации по обустройству для ограниченного животного"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Причины отказа"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Отправить нам ваши контактные данные"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Позвать волонтера"))));
        rowHowGetAnimalChoice.add(new KeyboardRow(List.of(new KeyboardButton("Меню"))));

        rowVoloMainChoice.add(new KeyboardRow(List.of(new KeyboardButton("Просмотреть отчеты за сегодня"))));
        rowVoloMainChoice.add(new KeyboardRow(List.of(new KeyboardButton("Как подтвердить отчеты?"))));
    }

    public String getBotName(){return botName;}
    public String getToken(){return token;}

    public KeyboardRow getRowShelters(){return rowShelters;}
    public List<KeyboardRow> getRowMainChoice(){return rowMainChoice;}
    public KeyboardRow getRowDefault(){return rowDefault;}
    public List<KeyboardRow> getRowInfoShelterChoice(){return rowInfoShelterChoice;}
    public List<KeyboardRow> getRowHowGetAnimalChoice(){return rowHowGetAnimalChoice;}
    public List<KeyboardRow> getRowVoloMainChoice(){return rowVoloMainChoice;}
}
