package ru.yandex.practicum.filmorate.integration.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public Review addReview(Review review) {
        User user = userStorage.findUserById(review.getUserId());
        Film film = filmStorage.findFilmById(review.getFilmId());
        log.info("addReview " + review);
        Review res = reviewStorage.addReview(review);
        eventStorage.createEvent("REVIEW", "ADD", review.getUserId(), res.getReviewId());
        return res;
    }

    public Review updateReview(Review review) {
        User user = userStorage.findUserById(review.getUserId());
        Film film = filmStorage.findFilmById(review.getFilmId());
        log.info("updateReview " + review);
        Review res = reviewStorage.updateReview(review);
        eventStorage.createEvent("REVIEW", "UPDATE", res.getUserId(), res.getReviewId());
        return res;
    }

    public Review deleteReview(long reviewId) {
        Review review = reviewStorage.getById(reviewId);
        log.info("deleteReview " + review);
        reviewStorage.deleteReview(reviewId);
        eventStorage.createEvent("REVIEW", "REMOVE", review.getUserId(), reviewId);
        return review;
    }

    public Review getById(long reviewId) {
        log.info("getById " + reviewId);
        return reviewStorage.getById(reviewId);
    }

    public List<Review> getListReview(long limit) {
        log.info("getListReview " + limit);
        return reviewStorage.getListReview(limit);
    }

    public List<Review> getListReviewByFilmId(long filmId, long limit) {
        log.info("getListReviewByFilmId {} {}", filmId, limit);
        return reviewStorage.getListReviewByFilmId(filmId, limit);
    }

    public Review putOpinion(Review review, long userId, boolean like) {
        log.info("putOpinion {} {} {}", review, userId, like);
        return reviewStorage.putOpinion(review, userId, like);
    }

    public Review delOpinion(Review review, long userId, boolean like) {
        log.info("delOpinion {} {} {}", review, userId, like);
        return reviewStorage.delOpinion(review, userId, like);
    }

}
