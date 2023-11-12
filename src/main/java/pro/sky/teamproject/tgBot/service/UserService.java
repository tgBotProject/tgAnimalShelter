package pro.sky.teamproject.tgBot.service;

import pro.sky.teamproject.tgBot.model.user.User;

import java.util.List;

public interface UserService {

    User addUser(User User);

    List<User> findUsers();

    User findUser(Long id);

    User findUserByChatId(Long chatId);

    List<User> findUsersByRole(String role);

    User updateUser(User User);

    void deleteUser(Long id);

}
