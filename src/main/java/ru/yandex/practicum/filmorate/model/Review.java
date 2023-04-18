package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Review {
    private Long reviewId;
    private @NotNull Long userId;
    private @NotNull Long filmId;
    private @NotNull String content;
    @JsonProperty("isPositive")
    private @NotNull Boolean positive;
    private long useful;
}
