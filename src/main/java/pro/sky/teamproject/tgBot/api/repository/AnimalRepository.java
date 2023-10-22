package pro.sky.teamproject.tgBot.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.teamproject.tgBot.model.Animal;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

}
