package pro.sky.teamproject.tgBot.api.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.teamproject.tgBot.model.Shelters;
import pro.sky.teamproject.tgBot.api.repository.ShelterRepository;

@Service
public class ShelterService {
    Logger logger = LoggerFactory.getLogger(Shelters.class);
    private ShelterRepository shelterRepository;

    /**
     * Метод добавления приюта.
     */
    public Shelters addShelters(Shelters shelter) {
        logger.info("Was invoked method for add shelter");
        return shelterRepository.save(shelter);
    }

    /**
     * Метод поиска приюта по id.
     */
    public Shelters findShelters(long id) {
        logger.info("Was invoked method for find shelter");
        return shelterRepository.findById(id).get();
    }

    /**
     * Метод редактирования информации о приюте.
     */
    public Shelters editShelters(Shelters shelter) {
        logger.info("Was invoked method for edit shelter");
        return shelterRepository.save(shelter);
    }

    /**
     * Метод удаления приюта по id.
     */
    public void deleteShelter(long id) {
        logger.info("Was invoked method for delete shelter");
        shelterRepository.deleteById(id);
    }
}
