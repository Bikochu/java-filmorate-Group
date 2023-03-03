package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    private List<User> users = new ArrayList<>();

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public void addUser(@RequestBody User user) {
        if (UserValidator.validateUser(user)) {
            users.add(user);
            log.info("Пользователь " + user.getName() + " добавлен");
        }
    }

    @RequestMapping(path = "/update", method = RequestMethod.PUT)
    public void updateUser(@RequestBody User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.add(i, user);
                log.info("Пользователь " + user.getName() + " обновлен");
            }
        }
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable int userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == userId) {
                users.remove(i);
            }
        }
    }
}
