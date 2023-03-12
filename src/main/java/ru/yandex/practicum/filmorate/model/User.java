package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private long id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    Set<Long> friends;
}
