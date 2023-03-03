package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    private List<User> users = new ArrayList<>();

    @RequestMapping(method = RequestMethod.POST)
    public void addUser(@Valid @RequestBody User user) {
        if (UserValidator.validateUser(user)) {
            users.add(user);
            log.info("Пользователь " + user.getName() + " добавлен");
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateUser(@Valid @RequestBody User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.add(i, user);
                log.info("Пользователь " + user.getName() + " обновлен");
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return users;
    }
}

