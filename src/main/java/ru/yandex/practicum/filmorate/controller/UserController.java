package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private static int id = 0;
    private List<User> users = new ArrayList<>();

    @RequestMapping(method = RequestMethod.POST)
    public User addUser(@Valid @RequestBody User user) {
        UserValidator.validateUser(user);
        user.setId(id++);
        users.add(user);
        log.info("Пользователь " + user.getName() + " добавлен");
        return user;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public User updateUser(@Valid @RequestBody User user) {
        boolean checkUser = users.stream().anyMatch(user1 -> user1.getId() == user.getId());
        if (checkUser) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == user.getId()) {
                    users.set(i, user);
                    log.info("Пользователь " + user.getName() + " обновлен");
                    continue;
                }
            }
        } else {
            throw new ValidationException("Пользователь с " + user.getId() + " не найден");
        }
        return user;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return users;
    }
}

