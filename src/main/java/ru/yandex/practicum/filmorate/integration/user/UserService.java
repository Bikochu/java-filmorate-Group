package ru.yandex.practicum.filmorate.integration.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    FilmStorage filmStorage;
    UserStorage userStorage;
    EventStorage eventStorage;

    @Autowired
    public UserService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("eventDbStorage") EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
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
            throw new NotFoundException(String.format("Пользователь с Id %d не найден или друг с Id %d не найден", id, friendId));
        }
        userStorage.addFriend(id, friendId);
        eventStorage.createEvent("FRIEND", "ADD", id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException(String.format("Пользователь с Id %d не найден или друг с Id %d не найден", id, friendId));
        }
        userStorage.deleteFriend(id, friendId);
        eventStorage.createEvent("FRIEND", "REMOVE", id, friendId);
    }

    public List<User> getListFriendsUser(long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с Id %d не найден", id));
        }
        return userStorage.getListFriendsUser(id);
    }

    public List<User> getMutualFriends(long id, long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException(String.format("Пользователь с Id %d не найден или друг с Id %d не найден", id, friendId));
        }
        List<User> friendsUser = userStorage.getListFriendsUser(id);
        List<User> friendsFriend = userStorage.getListFriendsUser(friendId);

        friendsUser.retainAll(friendsFriend);
        return friendsUser;
    }

    public List<Film> getRecommendations(long userId) {
        return filmStorage.getRecommendations(userId);
    }

    public List<Event> getEvents(long userId) {
        User user = findUserById(userId);
        return eventStorage.getAllEventsByUser(userId);
    }
}
