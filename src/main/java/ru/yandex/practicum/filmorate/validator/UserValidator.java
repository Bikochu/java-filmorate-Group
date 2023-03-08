package ru.yandex.practicum.filmorate.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    private final static Logger log = LoggerFactory.getLogger(UserValidator.class);
    private static LocalDate currentDate = LocalDate.now();

    public static void validateUser(User user) {
        if (user == null) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Пользователь не передан");
        }
        if (!StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (!StringUtils.hasText(user.getLogin()) || user.getLogin().contains(" ")) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (!StringUtils.hasText(user.getName())) {
            log.info("Пользователь не указал имя");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(currentDate)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
