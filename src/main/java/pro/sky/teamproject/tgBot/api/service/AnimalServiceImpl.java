package pro.sky.teamproject.tgBot.api.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.sky.teamproject.tgBot.model.Animal;
import pro.sky.teamproject.tgBot.api.repository.AnimalRepository;

import java.util.List;
/**
 * Service class for Animal domain
 *
 * @author Artem beshik7
 */
@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService{
private final AnimalRepository repository;

    @Override
    public Animal addAnimal(Animal animal) {
        return repository.save(animal);
    }
    @Override
    public List<Animal> findAllAnimals() {
        return repository.findAll();
    }
    /**
     * Находит животное по ID.
     *
     * @param id ID животного.
     * @return животное найдено.
     * @throws EntityNotFoundException если животное не найдено.
     */
    @Override
    public Animal findAnimalById(Long id) {
        return repository.findById(id)
                .orElseThrow(
                () -> new EntityNotFoundException(String.format("Animal not found [animalId=%s]", id)));
    }
    @Override
    public Animal updateAnimal(Animal animal) {
        Animal foundAnimal = findAnimalById(animal.getId());

        foundAnimal.setName(animal.getName());
        foundAnimal.setPhotoUrl(animal.getPhotoUrl());
        foundAnimal.setBirthday(animal.getBirthday());
        foundAnimal.setSpecies(animal.getSpecies());
        foundAnimal.setGender(animal.getGender());

        return repository.save(foundAnimal);
    }
    @Override
    public void deleteAnimal(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(String.format("Animal not found [Id=%s]", id));
        }
        repository.deleteById(id);
    }




}

