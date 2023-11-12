package pro.sky.teamproject.tgBot.service;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import java.util.Collections;
import jakarta.persistence.EntityNotFoundException;
import pro.sky.teamproject.tgBot.model.Animal;
import pro.sky.teamproject.tgBot.repository.AnimalRepository;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {
    @InjectMocks
    private AnimalServiceImpl animalService;

    @Mock
    private AnimalRepository repository;

    private Animal testAnimal;

    @BeforeEach
    public void setUp() {
        testAnimal = new Animal();
        testAnimal.setId(1L);
        testAnimal.setName("Test");
    }

    @Test
    public void addAnimalTest() {
        when(repository.save(any(Animal.class))).thenReturn(testAnimal);
        animalService.addAnimal(testAnimal);
        verify(repository).save(testAnimal);
    }

    @Test
    public void findAllAnimalsTest() {
        when(repository.findAll()).thenReturn(Collections.singletonList(testAnimal));
        animalService.findAllAnimals();
        verify(repository).findAll();
    }

    @Test
    public void findAnimalByIdTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(testAnimal));
        animalService.findAnimalById(1L);
        verify(repository).findById(1L);
    }

    @Test
    public void findAnimalByIdNotFoundTest() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> animalService.findAnimalById(1L));
    }

    @Test
    public void updateAnimalTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(testAnimal));
        when(repository.save(testAnimal)).thenReturn(testAnimal);
        animalService.updateAnimal(testAnimal);
        verify(repository).save(testAnimal);
    }

    @Test
    public void deleteAnimalTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(testAnimal));
        animalService.deleteAnimal(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    public void deleteAnimalNotFoundTest() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> animalService.deleteAnimal(1L));
    }
}
