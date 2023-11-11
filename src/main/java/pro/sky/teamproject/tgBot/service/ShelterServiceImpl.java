package pro.sky.teamproject.tgBot.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.teamproject.tgBot.model.Shelter;
import pro.sky.teamproject.tgBot.repository.ShelterRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShelterServiceImpl implements ShelterService {
    private final ShelterRepository shelterRepository;

    /**
     * Метод добавления приюта.
     */
    @Override
    public Shelter addShelters(Shelter shelter) {
        log.info("Was invoked method for add shelter");
        return shelterRepository.save(shelter);
    }

    /**
     * Метод поиска приюта по id.
     */
    @Override
    public Shelter findShelters(Long id) {
        log.debug("Was invoked method for find shelter");
        return shelterRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Shelter not found [ShelterId=%s]", id)));
    }

    /**
     * Метод редактирования информации о приюте.
     */
    @Override
    public Shelter editShelter(Shelter shelter) {
        log.info("Was invoked method for edit shelter");
        Shelter foundShelter = findShelters(shelter.getId());

        foundShelter.setName(shelter.getName());
        foundShelter.setAddressShelter(shelter.getAddressShelter());
        foundShelter.setWorkingTime(shelter.getWorkingTime());
        foundShelter.setDrivingDirections(shelter.getDrivingDirections());
        foundShelter.setSecurityContactDetails(shelter.getSecurityContactDetails());
        foundShelter.setInfo(shelter.getInfo());

        return shelterRepository.save(foundShelter);
    }
    /**
     * Метод удаления приюта по id.
     */
    @Override
    public void deleteShelter(Long id) {
        log.info("Was invoked method for delete shelter");
        shelterRepository.deleteById(id);
    }

    /**
     * Получить: адрес приюта.
     */
    @Override
    public String getAddressShelterById(Long id) {
        return shelterRepository.getAddressShelterById(id);
    }

    /**
     * Получить: график работы приюта.
     */
    @Override
    public String getWorkingTimeById(Long id) {
        return shelterRepository.getWorkingTimeById(id);
    }

    /**
     * Получить: схема проезда до приюта.
     */
    @Override
    public String getDrivingDirectionsById(Long id) {
        return shelterRepository.getDrivingDirectionsById(id);
    }

    /**
     * Получить: контактные данные охраны.
     */
    @Override
    public String getSecurityContactDetailsById(Long id) {
        return shelterRepository.getSecurityContactDetailsById(id);
    }

    /**
     * Получить: общая информация о приюте.
     */
    @Override
    public String getInfoById(Long id) {
        return shelterRepository.getInfoById(id);
    }

    @Override
    public List<Shelter> findAllShelters() {
        return shelterRepository.findAll();
    }
}
