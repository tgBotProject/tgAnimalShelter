package pro.sky.teamproject.tgBot.api.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;


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

    public AllTelegramBotConfiguration(){
        rowShelters = new KeyboardRow();
        rowMainChoice = new ArrayList<>();
        rowDefault = new KeyboardRow();

        rowShelters.add(new KeyboardButton("Кошки"));
        rowShelters.add(new KeyboardButton("Собаки"));


        KeyboardRow temp = new KeyboardRow();
        KeyboardRow temp1 = new KeyboardRow();
        KeyboardRow temp2 = new KeyboardRow();
        KeyboardRow temp3 = new KeyboardRow();
        temp.add("Информация о приюте");
        temp1.add("Информация как взять питомца");
        temp2.add("Отправить отчет");
        temp3.add("Позвать волонтера");
        rowMainChoice.add(temp);
        rowMainChoice.add(temp1);
        rowMainChoice.add(temp2);
        rowMainChoice.add(temp3);

        rowDefault.add("Позвать волонтера");
        rowDefault.add("Выбрать другое животное");
        rowDefault.add("Меню");
    }

    public String getBotName(){return botName;}
    public String getToken(){return token;}

    public KeyboardRow getRowShelters(){return rowShelters;}
    public List<KeyboardRow> getRowMainChoice(){return rowMainChoice;}
    public KeyboardRow getRowDefault(){return rowDefault;}
}
