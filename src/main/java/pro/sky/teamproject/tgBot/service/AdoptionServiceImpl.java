package pro.sky.teamproject.tgBot.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.teamproject.tgBot.model.Animal;

import pro.sky.teamproject.tgBot.model.Report;
import pro.sky.teamproject.tgBot.model.adoption.Status;
import pro.sky.teamproject.tgBot.model.user.Role;

import pro.sky.teamproject.tgBot.model.user.User;
import pro.sky.teamproject.tgBot.repository.AdoptionRepository;
import pro.sky.teamproject.tgBot.model.adoption.Adoption;
import pro.sky.teamproject.tgBot.repository.ReportRepository;
import pro.sky.teamproject.tgBot.repository.UserRepository;
import pro.sky.teamproject.tgBot.utils.MethodLog;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for Adoptions
 *
 * @author Dmitry Ldv236
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdoptionServiceImpl implements AdoptionService {

    private final AdoptionRepository repository;

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AllTelegramBot allTelegramBot;

    private final UserService userService;
    private final AnimalService animalService;


    /**
     * Создать новую запись об усыновлении.
     * Время усыновления устанавливается на текущую дату, время окончания испытательного срока - через 30 дней.
     * @throws IllegalArgumentException если для указанного пользователя или животного уже есть запись
     * об активном усыновлении
     * @param userId,animalId идентификаторы пользователя и животного
     * @return adoption сохраненная в БД запись
     */
    @Override
    public Adoption addAdoption(Long userId, Long animalId) {
        log.info("Method {}, userId - {}, animalId - {}", MethodLog.getMethodName(), userId, animalId);

        //проверяем, что юзер с таким id существует, и что у него нет усыновлений на испытательном сроке
        //(отмененный или успешные допускаются)
        User user = userService.findUser(userId);
        if (!repository.findByUserIdAndStatusLike(userId, Status.CURRENT).isEmpty()) {
            throw new IllegalArgumentException(String.format("User {%d} already has current adoption", userId));
        }

        //проверяем, что животное с таким id существует, и что у него нет активных усыновлений
        //(которые на испытательном сроке или успешно завершены, т.е. допускаются отмененные)
        Animal animal = animalService.findAnimalById(animalId);
        if (repository.findAdoptionsByAnimalId(animalId).stream()
                .anyMatch(a -> !a.getStatus().equals(Status.CANCELLED))) {
            throw new IllegalStateException(String.format("Animal {%d} already has active adoption", animalId));
        }

        //после всех проверок создаем новое усыновление, устанавливаются нужные даты и статус
        Adoption newAdoption = new Adoption(animal, user);
        newAdoption.setAdoptedDate(LocalDate.now());
        newAdoption.setTrialEndDate(LocalDate.now().plusDays(30));
        newAdoption.setStatus(Status.CURRENT);

        Adoption addedAdoption = repository.save(newAdoption);
        log.info("Создано усыновление: {}", addedAdoption);
        return addedAdoption;
    }

    @Override
    public List<Adoption> findAdoptions() {
        log.info("Method {}", MethodLog.getMethodName());
        return repository.findAll();
    }

    @Override
    public Adoption findAdoption(Long id) {
        log.info("Method {}, userId - {}", MethodLog.getMethodName(), id);
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Adoption not found [userId=%d]", id)));
    }

    @Override
    public List<Adoption> findAdoptionsByUser(Long userId) {
        log.info("Method {}, userId - {}", MethodLog.getMethodName(), userId);
        List<Adoption> foundAdoptions = repository.findAdoptionsByUserId(userId);

        if (foundAdoptions.isEmpty()) {
            throw new EntityNotFoundException(String.format("For User [id=%d] not exist any aoptions", userId));
        }
        return foundAdoptions;
    }

    @Override
    public List<Adoption> findAdoptionsByAnimal(Long animalId) {
        log.info("Method {}, animalId - {}", MethodLog.getMethodName(), animalId);
        List<Adoption> foundAdoptions = repository.findAdoptionsByAnimalId(animalId);

        if (foundAdoptions.isEmpty()) {
            throw new EntityNotFoundException(String.format("For animal [id=%d] not exist any adoptions", animalId));
        }
        return foundAdoptions;
    }

    @Override
    public Adoption updateAdoption(Adoption adoption) {
        log.info("Method {}, adoption - {}", MethodLog.getMethodName(), adoption);

        Adoption foundAdoption = findAdoption(adoption.getId());
        if (!foundAdoption.getStatus().equals(Status.CURRENT)) {
            throw new IllegalStateException("This adoption is not current, it cannot be edited");
        }

        foundAdoption.setTrialEndDate(adoption.getTrialEndDate());
        foundAdoption.setStatus(adoption.getStatus());
        foundAdoption.setNote(adoption.getNote());

        return repository.save(foundAdoption);
    }

    @Override
    public Adoption updateStatus(Long id, Status status, String note) {
        log.info("Method {}, id - {}, status - {}", MethodLog.getMethodName(), id, status);

        Adoption foundAdoption = findAdoption(id);
        if (!foundAdoption.getStatus().equals(Status.CURRENT)) {
            throw new IllegalStateException("This adoption is not current, it cannot be edited");
        }

        foundAdoption.setTrialEndDate(LocalDate.now());
        foundAdoption.setStatus(status);
        foundAdoption.setNote(note);

        return repository.save(foundAdoption);
    }

    @Override
    public Adoption prolongTrialForDays(Long id, @Positive Integer days) {
        log.info("Method {}, id - {}, days - {}", MethodLog.getMethodName(), id, days);

        Adoption foundAdoption = findAdoption(id);
        foundAdoption.setTrialEndDate(foundAdoption.getTrialEndDate().plusDays(days));
        return repository.save(foundAdoption);
    }

    @Override
    public void deleteAdoption(Long id) {
        log.info("Method {}, id - {}", MethodLog.getMethodName(), id);
        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(String.format("Adoption not found [Id=%d]", id));
        }
        repository.deleteById(id);
        log.info("Удалено усыновление ID {}", id);
    }

    /**
     * Ежедневно в 23 часа проверяет записи о текущих усыновлениях (статус CURRENT).
     * Устанавливает статус COMPLETED для тех записей, испытательный срок по которым истек.
     */
    @Scheduled(cron = "0 0 23 * * *")
    public void checkAdoptionsOnEndTimeReached() {
        log.info("Method {}", MethodLog.getMethodName());

        List<Adoption> adoptions = repository.findByTrialEndDateIsBeforeAndStatusLike
                (LocalDate.now(), Status.CURRENT);
        log.info("Найдено {} записей об усыновлении", adoptions.size());
        if (adoptions.isEmpty()) {return;}

        for (Adoption adoption : adoptions) {
            adoption.setStatus(Status.COMPLETED);
            repository.save(adoption);
            log.info("Усыновление ID {} отмечено выполненным", adoption.getId());
        }
    }

    /**
     * Проверяет наличие актуальных отчетов по каждому усыновлению.
     * Уведомляет волонтеров, если отчеты отсутствуют более двух дней.
     * @author Artem beshik7
     */
    @Scheduled(cron = "0 0 23 * * *")
    public void checkReportsAndNotify() {
        List<Adoption> adoptions = repository.findByTrialEndDateIsBeforeAndStatusLike(LocalDate.now(), Status.CURRENT);
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);

        for (Adoption adoption : adoptions) {
            Animal animal = adoption.getAnimal();
            Long animalId = animal.getId();
//            List<Report> reports = reportRepository.findReportsByDatetimeAfter(animalId, LocalDateTime.now());
            List<Report> reports = reportRepository.findReportsByDatetimeAfter(twoDaysAgo.atStartOfDay());
//            List<Report> reports = reportRepository.findReportsByAnimalIdAndDatetimeAfter(animalId, twoDaysAgo.atStartOfDay());

            if (reports.isEmpty()) {
                // Нет отчетов за последние два дня, уведомляем волонтеров.
                notifyVolunteers(adoption);
            }
        }

    }
    private void notifyVolunteers(Adoption adoption) {
        List<User> volunteers = userRepository.findUsersByRole(Role.VOLUNTEER);
        for (User volunteer : volunteers) {
            Long volunteerChatId = volunteer.getChatId();
            if (volunteerChatId != null) {
                // Используем метод sendMessage из класса AllTelegramBot для отправки уведомлений.
                String message = String.format("Требуется ваша помощь с животным: %s. Отчеты не поступали более двух дней.",
                        adoption.getAnimal().getName());
                allTelegramBot.sendMessage(volunteerChatId, message);
            }
        }
    }
}
