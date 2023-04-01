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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbTest {
    private final UserDbStorage userStorage;

    @Test
    void addUser() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user);
        User checkUser = userStorage.findUserById(1);
        assertTrue(checkUser.getId() == 1);
    }

    @Test
    void updateUser() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user);
        User updUser = new User(1, "gosha@mail.ru", "goshan", "Георгий Петров", LocalDate.of(2000, 05, 25));
        userStorage.updateUser(updUser);
        User checkUser = userStorage.findUserById(1);
        assertTrue(checkUser.getName().equals("Георгий Петров"));
    }

    @Test
    void findUserById() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user);
        User checkUser = userStorage.findUserById(1);
        assertTrue(checkUser.getId() == 1);
    }

    @Test
    void getAllUsers() {
        User user1 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        List<User> users = userStorage.getAllUsers();
        assertTrue(users.size() == 2);
    }

    @Test
    void addFriend() {
        User user1 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
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
        User user1 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        List<User> friends = userStorage.getListFriendsUser(1);
        assertTrue(friends.size() == 1);
        assertTrue(friends.get(0).getId() == 2L);
    }

    @Test
    void deleteFriend() {
        User user1 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        userStorage.deleteFriend(1, 2);
        User checkUser1 = userStorage.findUserById(1);
        assertFalse(checkUser1.getFriends().contains(2L));
    }

    @Test
    void deleteUserById() {
        User user1 = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user1);
        userStorage.deleteUserById(1);
        List<User> users = userStorage.getAllUsers();
        assertTrue(users.isEmpty());
    }
}