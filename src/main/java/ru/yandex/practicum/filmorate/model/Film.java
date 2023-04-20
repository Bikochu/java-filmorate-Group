package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    long id;
    String name;
    @Size(max = 200)
    String description;
    LocalDate releaseDate;
    int duration;
    final Set<Long> likes = new HashSet<>();
    final List<Genre> genres = new ArrayList<>();
    final List<Director> directors = new ArrayList<>();
    Mpa mpa;
    int rate;
}
