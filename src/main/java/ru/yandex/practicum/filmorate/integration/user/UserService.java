package ru.yandex.practicum.filmorate.integration.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        UserValidator.validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        UserValidator.validateUser(user);
        return userStorage.updateUser(user);
    }

    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }

    public User findUserById(long id) {
        return userStorage.findUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(long id, long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getListFriendsUser(long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден");
        }
        return userStorage.getListFriendsUser(id);
    }

    public List<User> getMutualFriends(long id, long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        List<User> friendsUser = userStorage.getListFriendsUser(id);
        List<User> friendsFriend = userStorage.getListFriendsUser(friendId);

        friendsUser.retainAll(friendsFriend);
        return friendsUser;
    }
}

