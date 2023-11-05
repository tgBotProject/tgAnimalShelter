package pro.sky.teamproject.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.teamproject.tgBot.model.adoption.Adoption;
import pro.sky.teamproject.tgBot.model.adoption.Status;

import java.time.LocalDate;
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

    List<Adoption> findByTrialEndDateIsBeforeAndStatusLike(LocalDate date, Status status);
}