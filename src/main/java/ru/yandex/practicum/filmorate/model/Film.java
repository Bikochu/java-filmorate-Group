package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private long id;
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    private int duration;
    Set<Long> likes;
}
