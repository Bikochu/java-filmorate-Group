package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.integration.review.ReviewService;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updReview(@RequestBody @Valid Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public Review delReview(@PathVariable long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable long id) {
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false, defaultValue = "10") long count,
                                   @RequestParam(required = false) Long filmId) {
        if (filmId == null) {
            return reviewService.getListReview(count);
        } else {
            return reviewService.getListReviewByFilmId(filmId, count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable long id, @PathVariable long userId) {
        Review review = reviewService.getById(id);
        return reviewService.putOpinion(review, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable long id, @PathVariable long userId) {
        Review review = reviewService.getById(id);
        return reviewService.putOpinion(review, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review delLike(@PathVariable long id, @PathVariable long userId) {
        Review review = reviewService.getById(id);
        return reviewService.delOpinion(review, userId, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review delDislike(@PathVariable long id, @PathVariable long userId) {
        Review review = reviewService.getById(id);
        return reviewService.delOpinion(review, userId, false);
    }

}

