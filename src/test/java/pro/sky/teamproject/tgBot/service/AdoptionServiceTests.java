package pro.sky.teamproject.tgBot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityNotFoundException;
import pro.sky.teamproject.tgBot.model.adoption.Adoption;
import pro.sky.teamproject.tgBot.model.adoption.Status;
import pro.sky.teamproject.tgBot.repository.AdoptionRepository;

@ExtendWith(MockitoExtension.class)
public class AdoptionServiceTests {

    @Mock
    private AdoptionRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private AnimalService animalService;
    @InjectMocks
    private AdoptionServiceImpl adoptionService;

    @Test
    public void testThrowExceptionWhenUserHasCurrentAdoptions() {
        Long userId = 1L;
        Long animalId = 2L;

        List<Adoption> adoptions = new ArrayList<>();
        Adoption adoption = new Adoption();
        adoptions.add(adoption);

        when(repository.findByUserIdAndStatusLike(userId, Status.CURRENT)).thenReturn(adoptions);

        assertThrows(IllegalArgumentException.class, () -> adoptionService.addAdoption(userId, animalId));
        verify(userService, times(1)).findUser(userId);
        verify(repository, times(1)).findByUserIdAndStatusLike(userId, Status.CURRENT);
    }

    @Test
    public void testThrowExceptionsAndAfterAddAdoption() {
        Long userId = 1L;
        Long animalId = 2L;

        List<Adoption> adoptions = new ArrayList<>();
        adoptions.add(new Adoption());

        when(repository.findAdoptionsByAnimalId(animalId)).thenReturn(adoptions);

        adoptions.get(0).setStatus(Status.COMPLETED);
        assertThrows(IllegalStateException.class, () -> adoptionService.addAdoption(userId, animalId));

        adoptions.get(0).setStatus(Status.CURRENT);
        assertThrows(IllegalStateException.class, () -> adoptionService.addAdoption(userId, animalId));

        adoptions.get(0).setStatus(Status.CANCELLED);
        when(repository.save(new Adoption())).thenReturn(new Adoption());
        Adoption newAdoption = adoptionService.addAdoption(userId, animalId);

        assertNotNull(newAdoption);

        verify(repository, times(3)).findAdoptionsByAnimalId(animalId);
        verify(repository, times(1)).save(newAdoption);
    }

    @Test
    public void testFindAdoptions() {
        Adoption adoption1 = new Adoption();
        Adoption adoption2 = new Adoption();
        List<Adoption> expectedAdoptions = Arrays.asList(adoption1, adoption2);

        when(repository.findAll()).thenReturn(expectedAdoptions);

        List<Adoption> actualAdoptions = adoptionService.findAdoptions();

        assertEquals(expectedAdoptions, actualAdoptions);
    }

    @Test
    public void testFindAdoption() {
        Long userId = 1L;
        Adoption adoption = new Adoption();
        adoption.setId(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(adoption));

        Adoption result = adoptionService.findAdoption(userId);

        assertNotNull(result);
        assertEquals(result.getId(), userId);
    }

    @Test
    public void testFindAdoptionsByUserAndByAnimal() {
        Long userId = 1L;
        Long animalId = 1L;
        List<Adoption> adoptions = new ArrayList<>();

        when(repository.findAdoptionsByUserId(userId)).thenReturn(adoptions);
        when(repository.findAdoptionsByAnimalId(animalId)).thenReturn(adoptions);

        assertThrows(EntityNotFoundException.class, () -> adoptionService.findAdoptionsByUser(userId));
        assertThrows(EntityNotFoundException.class, () -> adoptionService.findAdoptionsByAnimal(animalId));

        adoptions.add(new Adoption());

        assertNotNull(adoptionService.findAdoptionsByUser(userId));
        assertNotNull(adoptionService.findAdoptionsByAnimal(animalId));
    }

    @Test
    public void updateAdoptionTest() {
        Long adoptionId = 1L;
        Adoption adoption = new Adoption();
        adoption.setId(adoptionId);

        when(repository.findById(adoptionId)).thenReturn(Optional.of(adoption));

        adoption.setStatus(Status.CANCELLED);
        assertThrows(IllegalStateException.class, () -> adoptionService.updateAdoption(adoption));

        adoption.setStatus(Status.COMPLETED);
        assertThrows(IllegalStateException.class, () -> adoptionService.updateAdoption(adoption));

        adoption.setStatus(Status.CURRENT);
        when(repository.save(adoption)).thenReturn(adoption);
        Adoption result = adoptionService.updateAdoption(adoption);

        assertEquals(adoption.getTrialEndDate(), result.getTrialEndDate());
        assertEquals(adoption.getStatus(), result.getStatus());
        assertEquals(adoption.getNote(), result.getNote());

        verify(repository, times(1)).save(adoption);
    }

    @Test
    public void testUpdateStatus() {
        Long adoptionId = 1L;
        Adoption adoption = new Adoption();
        adoption.setId(adoptionId);

        when(repository.findById(adoptionId)).thenReturn(Optional.of(adoption));

        adoption.setStatus(Status.CANCELLED);
        assertThrows(IllegalStateException.class, () -> adoptionService.updateStatus(adoptionId, Status.COMPLETED, ""));

        adoption.setStatus(Status.COMPLETED);
        assertThrows(IllegalStateException.class, () -> adoptionService.updateStatus(adoptionId, Status.CANCELLED, ""));

        adoption.setStatus(Status.CURRENT);
        when(repository.save(adoption)).thenReturn(adoption);
        Adoption result = adoptionService.updateStatus(adoptionId, Status.COMPLETED, "");

        assertEquals(adoption.getTrialEndDate(), result.getTrialEndDate());
        assertEquals(adoption.getStatus(), result.getStatus());
        assertEquals(adoption.getNote(), result.getNote());

        verify(repository, times(1)).save(adoption);
    }

    @Test
    public void prolongTrialForDaysTest() {
        Adoption adoption = new Adoption();
        adoption.setTrialEndDate(LocalDate.now());
        when(repository.findById(anyLong())).thenReturn(Optional.of(adoption));
        when(repository.save(any(Adoption.class))).thenReturn(adoption);

        adoptionService.prolongTrialForDays(1L, 5);

        LocalDate expectedEndDate = LocalDate.now().plusDays(5);
        assertEquals(expectedEndDate, adoption.getTrialEndDate());
    }
}