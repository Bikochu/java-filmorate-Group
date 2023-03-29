package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final static Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private static long id = 0;
    private final Map<Long, User> users = new HashMap();

    @Override
    public User addUser(User user) {
        UserValidator.validateUser(user);
        user.setId(++id);
        users.put(id, user);
        log.info("Пользователь " + user.getName() + " добавлен");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с Id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь " + user.getName() + " обновлен");
        return user;
    }

    @Override
    public void deleteUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден");
        }
        users.remove(id);
    }

    @Override
    public User findUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(long id, long friendId) {

    }

    @Override
    public void deleteFriend(long id, long friendId) {

    }

    @Override
    public List<User> getListFriendsUser(long id) {
        return null;
    }
}
