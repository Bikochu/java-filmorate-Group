package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class Film {
    private long id;
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private final Set<Long> likes = new HashSet<>();
    private final List<Genre> genres = new ArrayList<>();
    private final List<User> directors = new ArrayList<>();
    private Mpa mpa;
    private int rate;
}
