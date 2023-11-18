package pro.sky.teamproject.tgBot.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.teamproject.tgBot.model.Animal;
import pro.sky.teamproject.tgBot.service.AnimalServiceImpl;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@WebMvcTest(AnimalController.class)
public class AnimalControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockBean
    private AnimalServiceImpl animalService;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // Регистрация модуля для Java Time API
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Отключение сериализации дат в виде временных меток
    }
    @Test
    void testGetAllAnimals() throws Exception {
        Animal animal = new Animal();
        List<Animal> animals = Arrays.asList(animal);
        given(animalService.findAllAnimals()).willReturn(animals);

        mockMvc.perform(get("/animal"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(animals)));

        verify(animalService).findAllAnimals();
    }
    @Test
    void getAllAnimals_ShouldReturnNotFound() throws Exception {
        given(animalService.findAllAnimals()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/animal"))
                .andExpect(status().isNotFound());

        verify(animalService).findAllAnimals();
    }
    @Test
    void testAddAnimal() throws Exception {
        Animal animal = new Animal();
        given(animalService.addAnimal(any(Animal.class))).willReturn(animal);

        mockMvc.perform(post("/animal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animal)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(animal)));

        verify(animalService).addAnimal(any(Animal.class));
    }
    @Test
    void testGetAnimalById() throws Exception {
        long animalId = 1L; // Устанавливаем ID для тестового животного
        Animal animal = new Animal();
        animal.setId(animalId); // Задаем ID животного
        animal.setName("Test Animal");
        given(animalService.findAnimalById(animal.getId())).willReturn(animal);

        mockMvc.perform(get("/animal/{id}", animal.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(animal)));

        verify(animalService).findAnimalById(animal.getId());
    }
    @Test
    void testUpdateAnimal() throws Exception {
        Animal animal = createAnimal();
        given(animalService.updateAnimal(any(Animal.class))).willReturn(animal);

        mockMvc.perform(put("/animal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animal)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(animal)));

        verify(animalService).updateAnimal(any(Animal.class));
    }
    @Test
    void testDeleteAnimal() throws Exception {
        Long animalId = 1L;
        Animal animal = new Animal();
        animal.setId(animalId);

        // Мокируем findAnimalById, чтобы он возвращал животное, а не null.
        given(animalService.findAnimalById(animalId)).willReturn(animal);
        // Подтверждаем, что deleteAnimal не выбросит исключение.
        doNothing().when(animalService).deleteAnimal(animalId);

        mockMvc.perform(delete("/animal/{id}", animalId))
                .andExpect(status().isOk());

        verify(animalService).deleteAnimal(animalId);
    }

    private Animal createAnimal() {
        Animal animal = new Animal();
        animal.setId(1L);
        animal.setName("Test Animal");
        animal.setPhotoUrl("http://example.com/photo.jpg");
        animal.setBirthday(LocalDate.of(2020, 1, 1));
        animal.setSpecies(Animal.Species.CAT);
        animal.setGender(Animal.Gender.FEMALE);
        return animal;
    }

}

