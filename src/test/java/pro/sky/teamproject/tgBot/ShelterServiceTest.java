package pro.sky.teamproject.tgBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.teamproject.tgBot.model.Shelter;
import pro.sky.teamproject.tgBot.repository.ShelterRepository;
import pro.sky.teamproject.tgBot.service.ShelterServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelterServiceTest {
    @InjectMocks
    private ShelterServiceImpl shelterService;
    @Mock
    private ShelterRepository shelterRepository;

    @Test
    public void addShelters() {
        Shelter shelter = this.buildTestingShelter();

        this.shelterService.addShelters(shelter);

        verify(this.shelterRepository).save(shelter);

    }
    @Test
    public void findShelters() {
        Shelter shelter = this.buildTestingShelter();

        when(shelterRepository.findById(1L)).thenReturn(Optional.of(shelter));
        Shelter result = this.shelterService.findShelters(1L);

        assertEquals(shelter.getId(), result.getId());
        verify(this.shelterRepository).findById(1L);
    }
    @Test
    public void deleteShelter(){

        this.shelterService.deleteShelter(1L);

        verify(this.shelterRepository).deleteById(1L);
    }

    @Test
    public void getAddressShelterById(){
        Long id = buildTestingShelter().getId();
        String addressShelter = buildTestingShelter().getAddressShelter();
        when(shelterRepository.getAddressShelterById(1L)).thenReturn(addressShelter);

        String result = shelterService.getAddressShelterById(id);

        assertNotNull(result);
        verify(shelterRepository).getAddressShelterById(1L);
    }

    @Test
    public void getWorkingTimeById() {
        Long id = buildTestingShelter().getId();
        String workingTime = buildTestingShelter().getWorkingTime();
        when(shelterRepository.getWorkingTimeById(1L)).thenReturn(workingTime);

        String result = shelterService.getWorkingTimeById(id);

        assertNotNull(result);
        verify(shelterRepository).getWorkingTimeById(1L);
    }

    @Test
    public void getDrivingDirectionsById() {
        Long id = buildTestingShelter().getId();
        String drivingDirections = buildTestingShelter().getDrivingDirections();
        when(shelterRepository.getDrivingDirectionsById(1L)).thenReturn(drivingDirections);

        String result = shelterService.getDrivingDirectionsById(id);

        assertNotNull(result);
        verify(shelterRepository).getDrivingDirectionsById(1L);
    }

    @Test
    public void getSecurityContactDetailsById(){
        Long id = buildTestingShelter().getId();
        String securityContactDetails = buildTestingShelter().getSecurityContactDetails();
        when(shelterRepository.getSecurityContactDetailsById(1L)).thenReturn(securityContactDetails);

        String result = shelterService.getSecurityContactDetailsById(id);

        assertNotNull(result);
        verify(shelterRepository).getSecurityContactDetailsById(1L);
    }

    @Test
    public void getInfoById(){
        Long id = buildTestingShelter().getId();
        String info = buildTestingShelter().getInfo();
        when(shelterRepository.getInfoById(1L)).thenReturn(info);

        String result = shelterService.getInfoById(id);

        assertNotNull(result);
        verify(shelterRepository).getInfoById(1L);
    }

    @Test
    public void findAllShelters() {
        List<Shelter> shelters = this.shelterRepository.findAll();
        assertEquals(0, shelters.size());
    }

    private Shelter buildTestingShelter() {
        Long id = 1L;
        String name = "text name";
        String addressShelter = "text address shelter";
        String workingTime = "text working time";
        String drivingDirections = "text driving directions";
        String securityContactDetails = "text security contact details";
        String info = "text info";

        Shelter shelter = new Shelter();

        shelter.setId(id);
        shelter.setName(name);
        shelter.setAddressShelter(addressShelter);
        shelter.setWorkingTime(workingTime);
        shelter.setDrivingDirections(drivingDirections);
        shelter.setSecurityContactDetails(securityContactDetails);
        shelter.setInfo(info);
        return shelter;
    }
}
