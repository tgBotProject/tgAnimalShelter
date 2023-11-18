package pro.sky.teamproject.tgBot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.teamproject.tgBot.model.user.Role;
import pro.sky.teamproject.tgBot.model.user.User;
import pro.sky.teamproject.tgBot.repository.UserRepository;
import pro.sky.teamproject.tgBot.utils.MethodLog;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Service class for User domain objects
 *
 * @author Dmitry Ldv236
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository repository;

    @Override
    public User addUser(User user) {
        log.info("Method {}, user - {}", MethodLog.getMethodName(), user);

        user.setRole(Role.ADOPTER);
        User createdUser = repository.save(user);
        log.info("Создан пользователь {}", createdUser);

        return createdUser;
    }

    @Override
    public List<User> findUsers() {
        log.info("Method {}", MethodLog.getMethodName());

        return repository.findAll();
    }

    @Override
    public User findUser(Long id) {
        log.info("Method {}, id - {}", MethodLog.getMethodName(), id);

        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User not found [userId=%s]", id)));
    }

    @Override
    public User findUserByChatId(Long chatId) {
        log.info("Method {}, id - {}", MethodLog.getMethodName(), chatId);
        return repository.findByChatId(chatId);
    }

    @Override
    public List<User> findUsersByRole(String role) {
        log.info("Method {}, role - {}", MethodLog.getMethodName(), role);
        List<Role> roles = Arrays.stream(Role.values()).toList();
        if (roles.stream().filter(r -> Objects.equals(r.name(), role)).findFirst().isEmpty()) {
            throw new IllegalArgumentException("Доступные роли: " + roles);
        }
        Role requestRole = Role.valueOf(role);
        List<User> foundUsers = repository.findUsersByRole(requestRole);

        if (foundUsers.isEmpty()) {
            throw new EntityNotFoundException(String.format("Not found users with role %s", role));
        }
        return foundUsers;
    }

    @Override
    public User updateUser(User user) {
        log.info("Method {}, user - {}", MethodLog.getMethodName(), user);

        User foundUser = findUser(user.getId());

        foundUser.setName(user.getName());
        foundUser.setPhone(user.getPhone());
        foundUser.setRole(user.getRole());

        return repository.save(foundUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Method {}, id - {}", MethodLog.getMethodName(), id);

        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(String.format("User not found [Id=%s]", id));
        }
        repository.deleteById(id);
    }
}
