package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        UserValidator.validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
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
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }
        Set<Long> friendsUser = user.getFriends();
        Set<Long> friendsFriend = friend.getFriends();
        friendsUser.add(friendId);
        friendsFriend.add(id);
        user.setFriends(friendsUser);
        friend.setFriends(friendsFriend);
    }

    public void deleteFriend(long id, long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        Set<Long> friendsUser = user.getFriends();
        Set<Long> friendsFriend = friend.getFriends();
        if (friendsUser.contains(friendId)) {
            friendsUser.remove(friendId);
            friendsFriend.remove(id);
        }
    }

    public List<User> getListFriendsUser(long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден");
        }
        return user.getFriends().stream()
                .map(u -> userStorage.findUserById(u))
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(long id, long friendId) {
        List<User> mutualFriends = new ArrayList<>();
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }
        Set<Long> friendsUser = user.getFriends();
        Set<Long> friendsFriend = friend.getFriends();
        for (Long userId : friendsUser) {
            if (friendsFriend.contains(userId)) {
                mutualFriends.add(userStorage.findUserById(userId));
            }
        }
        return mutualFriends;
    }
}

