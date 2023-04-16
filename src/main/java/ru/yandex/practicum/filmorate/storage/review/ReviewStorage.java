package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(long reviewId);

    Review getById(long reviewId);

    List<Review> getListReview(long limit);

    List<Review> getListReviewByFilmId(long filmId, long limit);

    Review putOpinion(Review review, long userId, boolean like);

    Review delOpinion(Review review, long userId, boolean like);
}
