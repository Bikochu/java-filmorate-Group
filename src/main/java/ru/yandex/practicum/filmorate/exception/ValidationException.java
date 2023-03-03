package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.controller.UserController;

public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }
}
