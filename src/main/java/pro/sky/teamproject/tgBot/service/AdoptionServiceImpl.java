package pro.sky.teamproject.tgBot.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for Adoptions
 *
 * @author Dmitry Ldv236
 */
@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl implements AdoptionService {

    private final AdoptionRepository repository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AllTelegramBot allTelegramBot;

    /**
     * Создать новую запись об усыновлении.
     * Время усыновления устанавливается на текущую дату, время окончания испытательного срока - через 30 дней.
     * @throws IllegalArgumentException если для указанного пользователя уже есть запись об усыновлении,
     * по которой идет испытательный срок (статус CURRENT)
     * @param adoption новая запись
     * @return adoption сохраненная в БД запись
     */
    @Override
    public Adoption addAdoption(Adoption adoption) {

        Long userId = adoption.getUser().getId();
        if (!repository.findByUserIdAndStatusLike(userId, Status.CURRENT).isEmpty()) {
            throw new IllegalArgumentException(String.format("User {%d} already has current adoption", userId));
        }
        adoption.setAdoptedDate(LocalDate.now());
        adoption.setTrialEndDate(LocalDate.now().plusDays(30));
        adoption.setStatus(Status.CURRENT);
        return repository.save(adoption);
    }

    @Override
    public List<Adoption> findAdoptions() {
        return repository.findAll();
    }

    @Override
    public Adoption findAdoption(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Adoption not found [userId=%d]", id)));
    }

    @Override
    public List<Adoption> findAdoptionsByUser(Long userId) {
        List<Adoption> foundAdoptions = repository.findAdoptionsByUserId(userId);

        if (foundAdoptions.isEmpty()) {
            throw new EntityNotFoundException(String.format("For User [id=%d] not exist any aoptions", userId));
        }
        return foundAdoptions;
    }

    @Override
    public List<Adoption> findAdoptionsByAnimal(Long animalId) {
        List<Adoption> foundAdoptions = repository.findAdoptionsByAnimalId(animalId);

        if (foundAdoptions.isEmpty()) {
            throw new EntityNotFoundException(String.format("For animal [id=%d] not exist any adoptions", animalId));
        }
        return foundAdoptions;
    }

    @Override
    public Adoption updateAdoption(Adoption adoption) {

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

        Adoption foundAdoption = findAdoption(id);
        foundAdoption.setTrialEndDate(foundAdoption.getTrialEndDate().plusDays(days));
        return repository.save(foundAdoption);
    }

    @Override
    public void deleteAdoption(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(String.format("Adoption not found [Id=%d]", id));
        }
        repository.deleteById(id);
    }

    /**
     * Ежедневно в 23 часа проверяет записи о текущих усыновлениях (статус CURRENT).
     * Устанавливает статус COMPLETED для тех записей, испытательный срок по которым истек.
     */
    @Scheduled(cron = "0 0 23 * * *")
    public void checkAdoptionsOnEndTimeReached() {

        List<Adoption> adoptions = repository.findByTrialEndDateIsBeforeAndStatusLike
                (LocalDate.now(), Status.CURRENT);
        if (adoptions.isEmpty()) {return;}

        for (Adoption adoption : adoptions) {
            adoption.setStatus(Status.COMPLETED);
            repository.save(adoption);
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
            List<Report> reports = reportRepository.findReportsByAnimalIdAndDatetimeAfter(animalId, twoDaysAgo.atStartOfDay());

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
