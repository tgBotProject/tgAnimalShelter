package pro.sky.teamproject.tgBot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.sky.teamproject.tgBot.model.user.Role;
import pro.sky.teamproject.tgBot.model.user.User;
import pro.sky.teamproject.tgBot.repository.UserRepository;

import java.util.List;

/**
 * Service class for User domain objects
 *
 * @author Dmitry Ldv236
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository repository;

    @Override
    public User addUser(User user) {

        user.setRole(Role.ADOPTER);
        return repository.save(user);
    }

    @Override
    public List<User> findUsers() {

        return repository.findAll();
    }

    @Override
    public User findUser(Long id) {

        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User not found [userId=%s]", id)));
    }

    @Override
    public List<User> findUsersByRole(String role) {

        List<User> foundUsers = repository.findUsersByRole(role);

        if (foundUsers.isEmpty()) {
            throw new EntityNotFoundException(String.format("Not found users with role %s", role));
        }
        return foundUsers;
    }

    @Override
    public User updateUser(User user) {

        User foundUser = findUser(user.getId());

        foundUser.setName(user.getName());
        foundUser.setPhone(user.getPhone());
        foundUser.setRole(user.getRole());

        return repository.save(foundUser);
    }

    @Override
    public void deleteUser(Long id) {

        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(String.format("User not found [Id=%s]", id));
        }
        repository.deleteById(id);
    }
}
