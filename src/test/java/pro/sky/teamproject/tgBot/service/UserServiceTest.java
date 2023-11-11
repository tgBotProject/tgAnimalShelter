package pro.sky.teamproject.tgBot.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.teamproject.tgBot.model.user.Role;
import pro.sky.teamproject.tgBot.model.user.User;
import pro.sky.teamproject.tgBot.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void addUser() {
        User userIn = new User(1L, 1L, "name", "phone", null, null, null);
        User userOut = new User(1L, 1L, "name", "phone", null, Role.ADOPTER, null);

        when(userRepository.save(userIn)).thenReturn(userOut);

        assertEquals(userOut, userService.addUser(userIn));
        assertEquals(userOut.getChatId(), userService.addUser(userIn).getChatId());
        assertEquals(userOut.getName(), userService.addUser(userIn).getName());
        assertEquals(userOut.getPhone(), userService.addUser(userIn).getPhone());
        assertEquals(userOut.getRole(), userService.addUser(userIn).getRole());

        verify(userRepository, times(5)).save(userIn);
    }

    @Test
    void findUsers() {
        User user1 = new User();
        User user2 = new User();
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void findUser() {
        User user1 = new User();
        user1.setId(1l);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertEquals(user1, userService.findUser(1l));
    }

    @Test
    void findUserByChatId() {
        User user1 = new User();
        user1.setId(1l);
        user1.setChatId(2l);

        when(userRepository.findByChatId(anyLong())).thenReturn(user1);
        assertEquals(user1, userService.findUserByChatId(2l));

        when(userRepository.findByChatId(anyLong())).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> userService.findUserByChatId(2l));
    }

    @Test
    void findUsersByRole() {
        User user1 = new User();
        User user2 = new User();
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findUsersByRole(Role.ADOPTER)).thenReturn(expectedUsers);
        List<User> actualUsers = userService.findUsersByRole("ADOPTER");
        assertEquals(expectedUsers, actualUsers);

        assertThrows(IllegalArgumentException.class, () -> userService.findUsersByRole("unknownRole"));

        when(userRepository.findUsersByRole(Role.ADOPTER)).thenReturn(new ArrayList<>());
        assertThrows(EntityNotFoundException.class, () -> userService.findUsersByRole("ADOPTER"));
    }
}