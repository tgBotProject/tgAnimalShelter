package pro.sky.teamproject.tgBot.api.service;

import pro.sky.teamproject.tgBot.model.adoption.Adoption;
import pro.sky.teamproject.tgBot.model.adoption.Status;

import java.util.List;

public interface AdoptionService {

    Adoption addAdoption(Adoption adoption);

    List<Adoption> findAdoptions();

    Adoption findAdoption(Long id);

    List<Adoption> findAdoptionsByUser(Long userId);

    List<Adoption> findAdoptionsByAnimal(Long animalId);

    Adoption updateAdoption(Adoption adoption);

    Adoption updateStatus(Long id, Status status, String note);

    Adoption prolongTrialForDays(Long id, Integer days);

    void deleteAdoption(Long id);
}
