package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private static int id = 0;
    private final Map<Integer, User> users = new HashMap();

    @RequestMapping(method = RequestMethod.POST)
    public User addUser(@Valid @RequestBody User user) {
        UserValidator.validateUser(user);
        user.setId(++id);
        users.put(id, user);
        log.info("Пользователь " + user.getName() + " добавлен");
        return user;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с Id" + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь " + user.getName() + " обновлен");
        return user;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}

