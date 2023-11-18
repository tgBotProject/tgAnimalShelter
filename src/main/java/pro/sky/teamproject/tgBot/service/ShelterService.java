package pro.sky.teamproject.tgBot.service;

import pro.sky.teamproject.tgBot.model.Shelter;

import java.util.List;

public interface ShelterService {

    Shelter addShelters(Shelter shelter);

    Shelter findShelters(Long id);

    Shelter editShelter(Shelter shelter);

    void deleteShelter(Long id);

    String getAddressShelterById(Long id);

    String getWorkingTimeById(Long id);

    String getDrivingDirectionsById(Long id);

    String getSecurityContactDetailsById(Long id);

    String getInfoById(Long id);
    List<Shelter> findAllShelters();
}
