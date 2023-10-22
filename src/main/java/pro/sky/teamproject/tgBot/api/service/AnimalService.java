package pro.sky.teamproject.tgBot.api.service;

import pro.sky.teamproject.tgBot.model.Animal;

import java.util.List;

public interface AnimalService {
    List<Animal> findAllAnimals();
    Animal findAnimalById(Long id);
    Animal addAnimal(Animal animal);
    Animal updateAnimal(Animal animal);
    void deleteAnimal(Long id);
}
