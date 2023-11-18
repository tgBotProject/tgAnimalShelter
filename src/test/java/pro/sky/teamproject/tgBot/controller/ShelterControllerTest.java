package pro.sky.teamproject.tgBot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.teamproject.tgBot.model.Shelter;
import pro.sky.teamproject.tgBot.repository.ShelterRepository;
import pro.sky.teamproject.tgBot.service.ShelterServiceImpl;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShelterController.class)
public class ShelterControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private ShelterServiceImpl shelterService;
    @MockBean
    private ShelterRepository shelterRepository;

    @Test
    public void updateShelter() throws Exception {
        long id = 1L;

        Shelter shelter = new Shelter(id, "text","text","text", "text","text","text","text","text", "text","text"
        ,"text","text","text", "text","text",null);
        Shelter updatedShelter = new Shelter(id,"new name", "new address","text", "text","text","text","text","text", "text","text"
                ,"text","text","text", "text","text",null);

        when(shelterRepository.findById(id)).thenReturn(Optional.of(shelter));
        when(shelterRepository.save(any(Shelter.class))).thenReturn(updatedShelter);

        mockMvc.perform(put("/shelter", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedShelter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedShelter.getName()))
                .andExpect(jsonPath("$.addressShelter").value(updatedShelter.getAddressShelter()))
                .andDo(print());
    }

    @Test
    public void testAddShelter() throws Exception {

        Long id = 1L;
        String name = "text name";
        String addressShelter = "text";
        String workingTime = "text";
        String drivingDirections = "text";
        String securityContactDetails = "text";
        String info = "text";

        JSONObject shelterObject = new JSONObject();
        shelterObject.put("text name", name);

        Shelter shelter = new Shelter();
        shelter.setId(id);
        shelter.setName(name);
        shelter.setAddressShelter(addressShelter);
        shelter.setWorkingTime(workingTime);
        shelter.setDrivingDirections(drivingDirections);
        shelter.setSecurityContactDetails(securityContactDetails);
        shelter.setInfo(info);

        when(shelterRepository.save(any(Shelter.class))).thenReturn(shelter);
        when(shelterRepository.findById(any(Long.class))).thenReturn(Optional.of(shelter));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/shelter")
                        .content(shelterObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.addressShelter").value(addressShelter))
                .andExpect(jsonPath("$.workingTime").value(workingTime))
                .andExpect(jsonPath("$.drivingDirections").value(drivingDirections))
                .andExpect(jsonPath("$.securityContactDetails").value(securityContactDetails))
                .andExpect(jsonPath("$.info").value(info));
    }
    @Test
    public void testDeleteShelter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/shelter/1"))
                .andExpect(status().isOk());
        verify(shelterService, times(1)).deleteShelter(anyLong());
    }
}
