package pro.sky.teamproject.tgBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.teamproject.tgBot.config.AllTelegramBotConfiguration;
import pro.sky.teamproject.tgBot.model.Report;
import pro.sky.teamproject.tgBot.model.user.Role;
import pro.sky.teamproject.tgBot.model.user.User;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AllTelegramBotTest {
    @InjectMocks
    private AllTelegramBot telegramBot;

    @Mock
    private UserService userService;
    @Mock
    private ReportService reportService;
    @Mock
    private ShelterService shelterService;
    private AllTelegramBotConfiguration telegramBotConfiguration;

    @BeforeEach
    public void setUp() {
        telegramBotConfiguration = new AllTelegramBotConfiguration();
        telegramBot = new AllTelegramBot(telegramBotConfiguration,shelterService,userService,reportService);
    }

    @Test
    public void testOnUpdateReceived_ViewReportsToday() {

        Update mockUpdate = Mockito.mock(Update.class);
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.when(mockUpdate.getMessage()).thenReturn(mockMessage);
        Mockito.when(mockMessage.getText()).thenReturn("Просмотреть отчеты за сегодня");
        Mockito.when(mockMessage.getChatId()).thenReturn(12345L);


        User mockUser = Mockito.mock(User.class);
        Mockito.when(userService.findUserByChatId(Mockito.anyLong())).thenReturn(mockUser);
        Mockito.when(mockUser.getRole()).thenReturn(Role.VOLUNTEER);

        List<Report> mockReports = new ArrayList<>();

        Mockito.when(reportService.findAllReports()).thenReturn(mockReports);


        telegramBot.onUpdateReceived(mockUpdate);


        Mockito.verify(userService).findUserByChatId(Mockito.anyLong());
        Mockito.verify(reportService).findAllReports();
    }

    @Test
    public void testViewReportsTodayNonVolunteer() {
        Update mockUpdate = Mockito.mock(Update.class);
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.when(mockUpdate.getMessage()).thenReturn(mockMessage);
        Mockito.when(mockMessage.getText()).thenReturn("Просмотреть отчеты за сегодня");
        Mockito.when(mockMessage.getChatId()).thenReturn(12345L);

        User mockUser = Mockito.mock(User.class);
        Mockito.when(userService.findUserByChatId(Mockito.anyLong())).thenReturn(mockUser);
        Mockito.when(mockUser.getRole()).thenReturn(Role.ADOPTER);

        telegramBot.onUpdateReceived(mockUpdate);

        Mockito.verify(userService).findUserByChatId(Mockito.anyLong());
        Mockito.verifyNoInteractions(reportService);
    }

    @Test
    public void testConfirmingReports() {
        Update mockUpdate = Mockito.mock(Update.class);
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.when(mockUpdate.getMessage()).thenReturn(mockMessage);
        Mockito.when(mockMessage.getText()).thenReturn("Как подтвердить отчеты?");
        Mockito.when(mockMessage.getChatId()).thenReturn(12345L);

        User mockUser = Mockito.mock(User.class);
        Mockito.when(userService.findUserByChatId(Mockito.anyLong())).thenReturn(mockUser);
        Mockito.when(mockUser.getRole()).thenReturn(Role.VOLUNTEER);

        telegramBot.onUpdateReceived(mockUpdate);

        Mockito.verify(userService).findUserByChatId(Mockito.anyLong());
        Mockito.verify(telegramBot).sendMessage(12345L, "Введите сообщение в формате:");
        Mockito.verify(telegramBot).sendMessage(12345L, "Ок: *ID отчета*");
        Mockito.verifyNoInteractions(reportService);
    }

    @Test
    public void testConfirmReportByIdFormat() {
        Update mockUpdate = Mockito.mock(Update.class);
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.when(mockUpdate.getMessage()).thenReturn(mockMessage);
        Mockito.when(mockMessage.getText()).thenReturn("Ок: 1");
        Mockito.when(mockMessage.getChatId()).thenReturn(12345L);

        User mockUser = Mockito.mock(User.class);
        Mockito.when(userService.findUserByChatId(Mockito.anyLong())).thenReturn(mockUser);
        Mockito.when(mockUser.getRole()).thenReturn(Role.VOLUNTEER);

        Report mockReport = Mockito.mock(Report.class);
        Mockito.when(reportService.findReportById(Mockito.anyLong())).thenReturn(mockReport);

        telegramBot.onUpdateReceived(mockUpdate);

        Mockito.verify(userService).findUserByChatId(Mockito.anyLong());
        Mockito.verify(reportService).findReportById(1L);
        Mockito.verify(reportService).updateReport(mockReport);
        Mockito.verify(telegramBot).sendMessage(12345L, "Отчет с ID 1 подтвержен.");
    }
}
