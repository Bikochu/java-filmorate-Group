package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    void deleteUserById(long id);

    User findUserById(long id);

    List<User> getAllUsers();

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    List<User> getListFriendsUser(long id);
}
