package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    Long reviewId;
    @NotNull Long userId;
    @NotNull Long filmId;
    @NotNull String content;
    @JsonProperty("isPositive")
    @NotNull Boolean positive;
    long useful;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return useful == review.useful && Objects.equals(reviewId, review.reviewId) && Objects.equals(userId, review.userId) && Objects.equals(filmId, review.filmId) && Objects.equals(content, review.content) && Objects.equals(positive, review.positive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, userId, filmId, content, positive, useful);
    }
}
