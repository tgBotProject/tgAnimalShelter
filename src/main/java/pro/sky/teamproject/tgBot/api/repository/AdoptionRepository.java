package pro.sky.teamproject.tgBot.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.teamproject.tgBot.model.adoption.Adoption;
import pro.sky.teamproject.tgBot.model.adoption.Status;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository class for <code>Adoption</code> domain objects
 *
 * @author Dmitry Ldv236
 */
@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {

    List<Adoption> findAdoptionsByUserId(Long userId);

    List<Adoption> findAdoptionsByAnimalId(Long animalId);

    List<Adoption> findByUserIdAndStatusLike(Long userId, Status status);

    List<Adoption> findByTrialEndDateIsBeforeAndStatusLike(LocalDateTime time, Status status);
}