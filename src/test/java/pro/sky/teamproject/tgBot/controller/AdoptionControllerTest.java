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
import pro.sky.teamproject.tgBot.model.adoption.Adoption;
import pro.sky.teamproject.tgBot.model.adoption.Status;
import pro.sky.teamproject.tgBot.model.user.User;
import pro.sky.teamproject.tgBot.service.AdoptionServiceImpl;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@WebMvcTest(AdoptionController.class)
class AdoptionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockBean
    private AdoptionServiceImpl adoptionService;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    @Test
    void testGetAllAdoption() throws Exception {
        Adoption adoption = new Adoption();
        List<Adoption> adoptions = Arrays.asList(adoption);
        given(adoptionService.findAdoptions()).willReturn(adoptions);

        mockMvc.perform(get("/adoption"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(adoptions)));

        verify(adoptionService).findAdoptions();
    }
    @Test
    void getAllAdoptions_ShouldReturnNotFound() throws Exception {
        given(adoptionService.findAdoptions()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/adoption"))
                .andExpect(status().isNotFound());

        verify(adoptionService).findAdoptions();
    }
    @Test
    void testAddAdoption() throws Exception {
        Adoption adoption = new Adoption();
        given(adoptionService.addAdoption(1l, 2l)).willReturn(adoption);

        mockMvc.perform(post("/adoption/user/{userId}/animal/{animalId}", 1l, 2l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoption)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(adoption)));

        verify(adoptionService).addAdoption(1l, 2l);
    }
    @Test
    void testGetAdoptionById() throws Exception {
        long id = 1L;
        Adoption adoption = new Adoption();
        adoption.setId(id);
//        adoption.set("Test Animal");
        given(adoptionService.findAdoption(id)).willReturn(adoption);

        mockMvc.perform(get("/adoption/{id}", adoption.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(adoption)));

        verify(adoptionService).findAdoption(id);
    }
    @Test
    void testUpdateAdoption() throws Exception {
        Adoption adoption = createAdoption();
        given(adoptionService.updateAdoption(any(Adoption.class))).willReturn(adoption);

        mockMvc.perform(put("/adoption")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoption)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(adoption)));

        verify(adoptionService).updateAdoption(any(Adoption.class));
    }
    @Test
    void testDeleteAdoption() throws Exception {
        Long adoptionId = 1L;
        Adoption adoption = createAdoption();
        adoption.setId(adoptionId);

        given(adoptionService.findAdoption(adoptionId)).willReturn(adoption);
        doNothing().when(adoptionService).deleteAdoption(adoptionId);

        mockMvc.perform(delete("/adoption/{id}", adoptionId))
                .andExpect(status().isOk());

        verify(adoptionService).deleteAdoption(adoptionId);
    }

    private Adoption createAdoption() {
        Adoption adoption = new Adoption();
        User user = new User();
        Animal animal = new Animal();
        adoption.setId(1L);
        adoption.setAnimal(animal);
        adoption.setUser(user);
        adoption.setAdoptedDate(LocalDate.now());
        adoption.setTrialEndDate(LocalDate.now().plusDays(30));
        adoption.setStatus(Status.CURRENT);
        return adoption;
    }

}

