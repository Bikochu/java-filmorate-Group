package ru.yandex.practicum.filmorate.integration.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbTest {
    private final UserDbStorage userStorage;

    @Test
    void addUser() {
        User user = new User(1, "gosha@mail.ru", "goshan1", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user);
        List<User> users = userStorage.getAllUsers();
        assertTrue(users.stream().anyMatch(u -> u.getLogin().equals("goshan1")));
    }

    @Test
    void updateUser() {
        User user = new User(1, "gosha@mail.ru", "goshan2", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User updUser = new User(1, "gosha@mail.ru", "goshan3", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user);
        List<User> users = userStorage.getAllUsers();
        List<Long> id = users.stream()
                .filter(u -> u.getLogin().equals("goshan2"))
                .map(User::getId)
                .collect(Collectors.toList());
        updUser.setId(id.get(0));
        userStorage.updateUser(updUser);
        User checkUser = userStorage.findUserById(id.get(0));
        assertTrue(checkUser.getLogin().equals("goshan3"));
    }

    @Test
    void findUserById() {
        User user = new User(1, "gosha@mail.ru", "goshan4", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user);
        User checkUser = userStorage.findUserById(1);
        assertTrue(checkUser.getId() == 1);
    }

    @Test
    void getAllUsers() {
        User user1 = new User(1, "gosha@mail.ru", "goshan5", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "gosha@mail.ru", "goshan6", "Григорий Петров", LocalDate.of(2000, 05, 25));
        List<User> usersCheckBefore = userStorage.getAllUsers();
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        List<User> usersCheckAfter = userStorage.getAllUsers();
        assertTrue(usersCheckBefore.size() < usersCheckAfter.size());
        assertTrue(usersCheckAfter.size() - usersCheckBefore.size() == 2);
    }

    @Test
    void addFriend() {
        User user1 = new User(1, "gosha@mail.ru", "goshan7", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "gosha@mail.ru", "goshan8", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        User checkUser1 = userStorage.findUserById(1);
        User checkUser2 = userStorage.findUserById(2);
        assertTrue(checkUser1.getFriends().contains(2L));
        assertFalse(checkUser2.getFriends().contains(1L));
    }

    @Test
    void getListFriendsUser() {
        User user1 = new User(1, "gosha@mail.ru", "goshan9", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "gosha@mail.ru", "goshan10", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        List<User> friends = userStorage.getListFriendsUser(1);
        assertTrue(friends.size() == 1);
        assertTrue(friends.get(0).getId() == 2L);
    }

    @Test
    void deleteFriend() {
        User user1 = new User(1, "gosha@mail.ru", "goshan11", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "gosha@mail.ru", "goshan12", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        userStorage.deleteFriend(1, 2);
        User checkUser1 = userStorage.findUserById(1);
        assertFalse(checkUser1.getFriends().contains(2L));
    }

    @Test
    void deleteUserById() {
        User user1 = new User(1, "gosha@mail.ru", "goshan13", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user1);
        List<User> users = userStorage.getAllUsers();
        users.forEach(u -> userStorage.deleteUserById(u.getId()));
        users = userStorage.getAllUsers();
        assertTrue(users.isEmpty());
    }
}