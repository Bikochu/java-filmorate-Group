package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static long id = 1;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "insert into users(user_id, email, login, user_name, birthday) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setLong(1, id);
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setString(4, user.getName());
            stmt.setDate(5, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(id);
        id = keyHolder.getKey().longValue() + 1;
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set " +
                "email = ?, login = ?, user_name = ?, birthday = ?" +
                "where user_id = ?";
        int upd = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (upd == 0) {
            throw new NotFoundException("Пользователь с Id " + user.getId() + " не найден");
        }
        return user;
    }

    @Override
    public void deleteUserById(long id) {
        String sqlQueryToLikes = "update likes set user_id = null where user_id = ?";
        jdbcTemplate.update(sqlQueryToLikes, id);
        String sqlQueryToUsersFriend = "delete from users_friend where user_id = ?";
        jdbcTemplate.update(sqlQueryToUsersFriend, id);
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User findUserById(long id) {
        try {
            String sqlQuery = "select user_id, email, login, user_name, birthday " +
                    "from users where user_id = ?";
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            List<User> friends = getListFriendsUser(id);
            user.getFriends().addAll(friends.stream().map(u -> u.getId()).collect(Collectors.toSet()));
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден");
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "select user_id, email, login, user_name, birthday from users";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        users.stream().forEach(user -> {
            List<User> friends = getListFriendsUser(user.getId());
            user.getFriends().addAll(friends.stream().map(u -> u.getId()).collect(Collectors.toSet()));
        });
        return users;
    }

    @Override
    public void addFriend(long id, long friendId) {
        String checkQuery = "select friend_id from users_friend where user_id = ?";
        List<Long> friends = jdbcTemplate.queryForList(checkQuery, new Object[]{id}, Long.class);
        if (friends.contains(friendId)) {
            String sqlQueryFriend = " update users_friend set status  = true " +
                    "where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sqlQueryFriend, id, friendId);
        } else {
            String sqlQueryUser = "insert into users_friend(user_id, friend_id, status) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQueryUser,
                    id,
                    friendId,
                    true);
            String sqlQueryFriend = "insert into users_friend(user_id, friend_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQueryFriend,
                    friendId,
                    id);
        }
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        String sqlQueryDelFriend = "delete from users_friend where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQueryDelFriend, id, friendId);
        jdbcTemplate.update(sqlQueryDelFriend, friendId, id);
    }

    @Override
    public List<User> getListFriendsUser(long id) {
        String sqlQuery = "select user_id, email, login, user_name, birthday from users where user_id in (" +
                "select friend_id from users_friend where user_id = ? and status = true);";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("user_name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        return user;
    }
}
