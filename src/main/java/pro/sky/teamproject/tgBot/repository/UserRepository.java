package pro.sky.teamproject.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.teamproject.tgBot.model.user.User;

import java.util.List;

/**
 * Repository class for <code>Adoption</code> domain objects
 *
 * @author Dmitry Ldv236
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByRole(String role);
}