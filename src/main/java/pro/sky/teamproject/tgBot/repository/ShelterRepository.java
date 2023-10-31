package pro.sky.teamproject.tgBot.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.teamproject.tgBot.model.Shelter;

public interface ShelterRepository extends JpaRepository<Shelter,Long> {

    String getAddressShelterById(Long id);
    String getWorkingTimeById(Long id);
    String getDrivingDirectionsById(Long id);
    String getSecurityContactDetailsById(Long id);
    String getInfoById(Long id);
}
