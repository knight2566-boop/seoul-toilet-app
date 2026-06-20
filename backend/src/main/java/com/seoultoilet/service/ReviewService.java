package com.seoultoilet.service;

import com.seoultoilet.dto.ReviewRequest;
import com.seoultoilet.entity.Review;
import com.seoultoilet.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviews(Long toiletId) {
        return reviewRepository.findByToiletIdOrderByCreatedAtDesc(toiletId);
    }

    public Review addReview(Long toiletId, ReviewRequest request) {
        Review review = new Review();
        review.setToiletId(toiletId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setPhotoUrl(request.getPhotoUrl());
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public Double getAverageRating(Long toiletId) {
        return reviewRepository.findAverageRatingByToiletId(toiletId);
    }
}
