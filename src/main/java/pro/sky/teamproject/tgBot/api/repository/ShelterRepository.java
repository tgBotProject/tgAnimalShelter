package pro.sky.teamproject.tgBot.api.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.teamproject.tgBot.model.Shelters;
public interface ShelterRepository extends JpaRepository<Shelters,Long> {
}
